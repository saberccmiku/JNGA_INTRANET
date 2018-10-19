package com.yskj.jnga.module.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.yskj.jnga.App;
import com.yskj.jnga.R;
import com.yskj.jnga.module.MainActivity;
import com.yskj.jnga.module.base.RxBaseActivity;
import com.yskj.jnga.network.ApiConstants;
import com.yskj.jnga.task.QueryPoliceInfoTask;
import com.yskj.jnga.utils.Utils;

import bingo.sso.client.android.Constants;
import bingo.sso.client.android.SSOClient;
import bingo.sso.client.android.SSOClientBuilder;
import bingo.sso.client.android.listener.LoginListener;
import bingo.sso.client.android.modle.User;

/**
 * Created by 63987 on 2018/4/17
 * 欢迎界面
 */

public class SplashActivity extends RxBaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

        ImageView showPicture = findViewById(R.id.guide_picture);
        Animation fadeInScale = AnimationUtils.loadAnimation(this, R.anim.welcome_fade_in_scale);
        showPicture.setBackgroundDrawable(Utils.setSamsungBg(this, R.drawable.jnga_welcome));
        showPicture.startAnimation(fadeInScale);
        fadeInScale.setAnimationListener(new Animation.AnimationListener() {

            public void onAnimationStart(Animation animation) {

            }

            public void onAnimationRepeat(Animation animation) {

            }

            public void onAnimationEnd(Animation animation) {

                ssoLogin();
            }
        });
    }

    @Override
    public void initToolBar() {

    }

    /**
     * 接入市局的单点登陆验证
     */
    private void ssoLogin() {

        Intent intent = getIntent();
        String acccesToken = intent.getStringExtra(Constants.KEY_ACCESSTOKEN);
        String refreshToken = intent.getStringExtra(Constants.KEY_REFRESHTOKEN);
        String ssoUrl = intent.getStringExtra(Constants.KEY_SSOURL);
        String uamUrl = intent.getStringExtra(Constants.KEY_UAMURL);

        if (ssoUrl == null) {
            ssoUrl = ApiConstants.SSO_URL;
        }
        if (uamUrl == null) {
            uamUrl = ApiConstants.UAM_URL;
        }

        SSOClient ssoClient = new SSOClientBuilder().setSsoBaseEndpoint(ssoUrl).setUamBaseEndpoint(uamUrl).build();
        ssoClient.login(acccesToken, refreshToken, new LoginListener() {
            @Override
            public void onSuccess(User user) {
                //登录成功，返回user对象，回调在子线程
                //直接采用sso的登陆系统获取警员信息进入主界面，通过警员获取警员信息
                App.getInstance().getSpUtil().setAccount(user.getUserId());
                new QueryPoliceInfoTask().setPoliceInfo(user.getUserId());
                //江南公安的登陆系统
                //startActivity(new Intent(SplashActivity.this, LoginActivity.class).putExtra("isFromWelcome", true));
                overridePendingTransition(R.anim.welcome_fade_in, R.anim.welcome_fade_out);
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                SplashActivity.this.finish();
            }


            // err 异常信息
            @Override
            public void onError(String err) {
                //登录失败
                Toast.makeText(SplashActivity.this, err, Toast.LENGTH_SHORT).show();
            }

            /*
             *  token失效,
             * code == 1  refreshToken 无效
             * code == 2  accessToken 无效
             * */
            @Override
            public void onTokenInvalid(int code) {
                //登录失败
                switch (code) {
                    case 1:
                        Toast.makeText(SplashActivity.this, "refreshToken 无效", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(SplashActivity.this, "accessToken 无效", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(SplashActivity.this, "SSO登陆异常：" + code, Toast.LENGTH_SHORT).show();
                        break;

                }

            }
        });
    }
}
