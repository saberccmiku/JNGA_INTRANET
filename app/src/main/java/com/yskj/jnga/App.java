package com.yskj.jnga;

import android.app.Application;

import com.yskj.jnga.utils.SpUtil;

/**
 * Created by 63987 on 2018/4/6
 */

public class App extends Application {

    private static App sApp;
    private SpUtil mSpUtil;
    public static final String SP_FILE_NAME = "sp_menu";

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
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
