package com.yskj.jnga.module.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.yskj.jnga.R;
import com.yskj.jnga.module.MainActivity;
import com.yskj.jnga.module.base.RxBaseActivity;
import com.yskj.jnga.utils.Utils;

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

                startActivity(new Intent(SplashActivity.this, LoginActivity.class).putExtra("isFromWelcome", true));
                finish();
                overridePendingTransition(R.anim.welcome_fade_in, R.anim.welcome_fade_out);
            }
        });
    }

    @Override
    public void initToolBar() {

    }
}
