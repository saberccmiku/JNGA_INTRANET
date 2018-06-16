package com.yskj.jnga;

import android.app.Application;

import com.lidroid.xutils.DbUtils;
import com.yskj.jnga.utils.SpUtil;

/**
 * Created by 63987 on 2018/4/6
 */

public class App extends Application {

    private static App sApp;
    private SpUtil mSpUtil;
    public static final String SP_FILE_NAME = "sp_menu";
    public static DbUtils sDbUtils;

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
        // 创建本地数据库
        if (sDbUtils == null) {
            sDbUtils = DbUtils.create(getApplicationContext());
        }
    }

    public static App getInstance() {
        return sApp;
    }

    public synchronized SpUtil getSpUtil() {
        if (mSpUtil == null)
            mSpUtil = new SpUtil(this, SP_FILE_NAME);
        return mSpUtil;
    }
}
