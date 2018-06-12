package com.yskj.jnga.module.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yskj.jnga.App;
import com.yskj.jnga.R;
import com.yskj.jnga.module.MainActivity;
import com.yskj.jnga.module.base.RxBaseActivity;
import com.yskj.jnga.network.ApiConstants;
import com.yskj.jnga.network.xml.BaseTable;
import com.yskj.jnga.network.xml.DataOperation;
import com.yskj.jnga.network.xml.DataSetList;
import com.yskj.jnga.network.xml.DocInfor;
import com.yskj.jnga.network.xml.PostHttp;
import com.yskj.jnga.network.xml.XmlPackage;
import com.yskj.jnga.utils.SpUtil;
import com.yskj.jnga.utils.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by 63987 on 2018/4/17
 * 登陆界面
 */

public class LoginActivity extends RxBaseActivity implements View.OnClickListener {

    private EditText mEdit_user_name, mEdit_user_password;
    private Context context = LoginActivity.this;
    private String TAG = "LoginActivity";
    public DataSetList datasetlist;
    private SpUtil mSpUtil;

    // 手机登录使用用户名、密码、设备类型、DEVICE_ID及SIM卡号
    // 平板登录使用用户名、密码、设备类型
    private String deviceId; // 设备唯一标识码
    private String simSerialNumber; // SIM卡号

    /*
     * login-wf#PSW#123账号密码，null未记住账号 ;isKeepAccout-是否记住账号密码;
     * isAutologin-是否自动登录;isPswShow-密码是否可见
     */

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

        initWidget();
        mSpUtil = App.getInstance().getSpUtil();
    }

    @Override
    public void initToolBar() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        loadData();

    }

    @Override
    public void loadData() {
        // 设置用户名可见
        if (!TextUtils.isEmpty(mSpUtil.getAccount())) {
            mEdit_user_name.setText(mSpUtil.getAccount());
        }

        if (!getIntent().getBooleanExtra("logout", false)) {
            if (mSpUtil.getIsAutoLogin()) {

                // Updated at 2016/03/22
                // 根据客户需求，应用登陆名在前段不区分大小写，服务器中用户名如有英文字母则用大写
                // 如用户输入xj001或XJ001，都以XJ001在数据库中查找
                new AsyncLoader().execute("login", mEdit_user_name.getText().toString().toUpperCase(),
                        Utils.md5(mEdit_user_password.getText().toString()));

            }
        }
    }

    @SuppressLint("HardwareIds")
    private void initVar() {

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 0x12);
        } else {
            if (tm != null) {
                deviceId = tm.getDeviceId();
                simSerialNumber = tm.getSimSerialNumber();
                String androidId = Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID);
                //Log.i(TAG, "设备码 " + deviceId + " SIM卡号 " + simSerialNumber + " Android Id " + androidId);

                if (Utils.isNetworkAvailable(context)) {
                    // Updated at 2016/03/22
                    // 根据客户需求，应用登陆名在前段不区分大小写，服务器中用户名如有英文字母则用大写
                    // 如用户输入xj001或XJ001，都以XJ001在数据库中查找
                    new AsyncLoader().execute("login", mEdit_user_name.getText().toString().toUpperCase(),
                            Utils.md5(mEdit_user_password.getText().toString()));
                }
            }
        }
    }


    private void initWidget() {
        LinearLayout ll_login = findViewById(R.id.ll_login);
        ll_login.setBackgroundDrawable(Utils.setSamsungBg(this, R.drawable.login_bj_img));
//        ll_login.setBackgroundDrawable(Utils.setSamsungBg(this, R.drawable.main_bg_02));
        RelativeLayout rl_login = findViewById(R.id.rl_login);
        rl_login.setBackgroundDrawable(Utils.setSamsungBg(this, R.drawable.bj_pic));
        // 控制登录用户名图标大小
        mEdit_user_name = findViewById(R.id.edit_user_name);
        Drawable icon_male = getResources().getDrawable(R.drawable.icon_male);
        icon_male.setBounds(10, 0, 25, 25);// 第一0是距左边距离，第二0是距上边距离，40分别是长宽
        mEdit_user_name.setCompoundDrawables(icon_male, null, null, null);// 只放左边
        // 控制登录用户名图标大小
        mEdit_user_password = findViewById(R.id.edit_user_password);
        Drawable icon_locked = getResources().getDrawable(R.drawable.icon_locked);
        icon_locked.setBounds(10, 0, 25, 25);// 第一0是距左边距离，第二0是距上边距离，40分别是长宽
        mEdit_user_password.setCompoundDrawables(icon_locked, null, null, null);// 只放左边

        TextView register_device = findViewById(R.id.register_device);
        register_device.setOnClickListener(this);
        ImageButton btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
    }

    private class AsyncLoader extends AsyncTask<String, Integer, Integer> {
        @Override
        protected Integer doInBackground(String... params) {

            if ("login".equals(params[0])) {
                try {
                    Log.i(TAG, params[1] + ", " + params[2] + "  登录");
                    datasetlist = PostHttp
                            .PostXML(XmlPackage.packageSelect("from JNGA_USERS where USER_ID='" + params[1] + "'", "",
                                    "", "", "SEARCHYOUNGCONTENT", new DocInfor("", "JNGA_USERS"), true, false));

                } catch (Exception e) {
                    e.printStackTrace();
                    return -1;
                }
                return 1;
            }
            return 0;
        }

        protected void onPreExecute() {

        }

        protected void onPostExecute(Integer result) {
            switch (result) {
                case -1:
                    if (Utils.isNetworkAvailable(context)) {
                        Toast.makeText(context, "访问服务失败", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "网络异常", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 1:

                    if (datasetlist != null && ApiConstants.REQUEST_RESULT_SUCCESS.equals(datasetlist.SUCCESS)) {
                        ArrayList<String> nameList = (ArrayList<String>) datasetlist.nameList;
                        ArrayList<String> valueList = (ArrayList<String>) datasetlist.valueList;
                        ArrayList<String> contentId = (ArrayList<String>) datasetlist.CONTENTID;

                        // 判断用户名是否存在
                        if (nameList == null || nameList.size() == 0) {
                            // if("login".equals(nameList.get(0))){
                            Toast.makeText(LoginActivity.this, "用户不存在", Toast.LENGTH_SHORT).show();

                        } else {
                            // 获取用户在服务器上用户表中的ID
                            String userContentId = contentId.get(0);
                            // 判断密码是否正确
                            // System.out.println("valueList:"+valueList);
                            // System.out.println("valueList.get(7):"+valueList.get(7));
                            if (valueList.get(2).equals(Utils.md5(mEdit_user_password.getText().toString()))) {
                                // Constants.USERID =
                                // mLoginAccount.getText().toString();
                                // Constants.PSWID = mLoginPsw.getText().toString();
                                String user_id = mEdit_user_name.getText().toString().toUpperCase();
                                //String user_pwd = mEdit_user_password.getText().toString();
                                //String user_auth = valueList.get(6);

                                // 保存用户名但不保存密码
                                // 每次进入系统必须输入密码
                                if (user_id.toUpperCase().equals("NX")
                                        || user_id.toUpperCase().equals("FJY")) {

                                    user_id = "103932";// 103932//105003//000077//101908//103841//105102//102492//XJ0999

                                }
                                mSpUtil.setAccount(user_id);

                                mSpUtil.setPwd(mEdit_user_password.getText().toString());
                                mSpUtil.setAuth(valueList.get(3));

                                if (getIntent() != null && (getIntent().getBooleanExtra("isFromWelcome", false)
                                        || getIntent().getBooleanExtra("isFailedAutoLogin", false))) {

                                    // 设备类型。1为Android手机，2为iOS手机，3为Android平板，4为iOS平板
                                    String devType = valueList.get(4);
                                    // devType="3";
                                    // 用户使用手机端
                                    if (devType.equals("1") || devType.equals("2")) {

                                        // 检测用户表DEVICE_ID及SIM卡号是否齐全
                                        // ValueList 1,设备唯一码； 3，SIM卡号

                                        if (valueList.get(5).equals(deviceId) && valueList.get(7).equals(simSerialNumber)) {

                                            mSpUtil.getString("Gesture", "1234");
                                            // 已经登陆过，下次splash自动登录
                                            mSpUtil.putBoolean("Logined", true);
                                            // 从登陆界面进入主界面
                                            mSpUtil.putBoolean("Flogin", true);
                                            // 解锁时间归0
                                            mSpUtil.putLong(mSpUtil.getString("Gesture", null) + "_DataNum", 0);
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class)
                                                    .putExtra("isLogin", true).putExtra("userContentId", userContentId));
                                            LoginActivity.this.finish();

                                        } else {
                                            Toast.makeText(LoginActivity.this, "用户未在后台注册或注册信息有误！", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    // 用户使用平板设备
                                    else if (devType.equals("3") || devType.equals("4")) {
                                        mSpUtil.getString("Gesture", "1234");
                                        // 已经登陆过，下次splash自动登录
                                        mSpUtil.putBoolean("Logined", true);
                                        // 从登陆界面进入主界面
                                        mSpUtil.putBoolean("Flogin", true);
                                        // 解锁时间归0
                                        mSpUtil.putLong(mSpUtil.getString("Gesture", null) + "_DataNum", 0);
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class)
                                                .putExtra("isLogin", true).putExtra("userContentId", userContentId));
                                        LoginActivity.this.finish();
                                    }
                                    // 其他类型
                                    else {
                                        Toast.makeText(LoginActivity.this, "用户未在后台注册或注册信息有误！", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    setResult(RESULT_OK);
                                }
                                // onBackPressed();
                            } else {
                                Toast.makeText(LoginActivity.this, "密码不正确", Toast.LENGTH_SHORT).show();
                            }
                        }

                    } else {
                        Toast.makeText(LoginActivity.this, "系统网络异常", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }

    private static class RegisterTask extends AsyncTask<String, Integer, Integer> {

        private WeakReference<LoginActivity> reference;

        RegisterTask(LoginActivity activity) {
            this.reference = new WeakReference<>(activity);
        }

        String loginAccount;

        @Override
        protected Integer doInBackground(String... params) {
            LoginActivity activity = reference.get();
            loginAccount = activity.mEdit_user_name.getText().toString().toUpperCase();
            if (activity.simSerialNumber == null) {
                return 1;

            } else if (loginAccount.equals("")) {
                return 2;

            } else {

                try {
                    // 判断设备是否已被注册,用户名已被使用
                    String sqlStr = "from (select * from JNGA_USERS where USER_ID = '" + loginAccount
                            + "' or DEV_ID = '" + activity.deviceId + "' ) JNGA_USERS";
                    ArrayList<BaseTable> tables = DataOperation.queryTable("JNGA_USERS", sqlStr);

                    if (tables.size() != 0) {
                        for (BaseTable table : tables) {
                            if (table.getField("USER_ID").equals(loginAccount)) {
                                return 6;
                            } else if (table.getField("DEV_ID").equals(activity.deviceId)) {
                                return 7;
                            }
                        }

                    }

                    BaseTable userTable = new BaseTable();
                    userTable.setTableName("JNGA_USERS");
                    userTable.putField("USER_ID", loginAccount);
                    userTable.putField("DEV_ID", activity.deviceId);
                    userTable.putField("PHONE_SIM", activity.simSerialNumber);
                    userTable.putField("USER_DEV", "1");

                    if (DataOperation.insertOrUpdateTable(userTable)) {
                        return 3;

                    } else {
                        return 4;

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return 5;

                }

            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            LoginActivity activity = reference.get();
            switch (result) {
                case 1:
                    Toast.makeText(activity, "请插入SIM卡", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(activity, "用户名不能为空", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(activity,
                            "恭喜用户" + loginAccount + "设备标识" + activity.deviceId + "SIM卡号为" + activity.simSerialNumber + "的设备注册成功，请联系管理员激活吧",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(activity, "注册失败", Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    Toast.makeText(activity, "网络异常", Toast.LENGTH_SHORT).show();
                    break;
                case 6:
                    Toast.makeText(activity, "该用户已被使用", Toast.LENGTH_SHORT).show();
                    break;
                case 7:
                    Toast.makeText(activity, "该设备已被注册", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_device:
                new RegisterTask(this).execute();
                break;
            case R.id.btn_login:
                initVar();

                break;
        }
    }
}
