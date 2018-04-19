package com.yskj.jnga.utils;

import android.app.Activity;
import android.os.Build;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.yskj.jnga.widget.SystemBarTintManager;


/**
 * Created by saber on 2018/3/6
 * 界面工具
 */

public class UIUtils {

    /**
     * 设置通知栏状态
     *
     * @param activity activity
     * @param flag     是否设置
     *                 需要API 19
     */
    public static void setTranslucentStatus(Activity activity, boolean flag) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            if (flag) {
                layoutParams.flags |= bits;
            } else {
                layoutParams.flags &= ~bits;
            }
            window.setAttributes(layoutParams);
        }
    }

    /**
     * 设置通知栏颜色
     *
     * @param activity activity
     * @param flag     是否设置
     * @param color    颜色代码
     */
    public static void setNotificationColor(Activity activity, boolean flag, int color) {
        setTranslucentStatus(activity, flag);
        SystemBarTintManager manager = new SystemBarTintManager(activity);
        manager.setStatusBarTintEnabled(true);
        manager.setStatusBarTintColor(color);
    }

    public static void setStatusBarAttribute(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = activity.getWindow().getDecorView();
            //让应用主题内容占用系统状态栏的空间
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            //设置状态栏颜色为透明
            activity.getWindow().setStatusBarColor(color);
        }
    }

    public static void hideStatusBar(Activity activity) {
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        activity.getWindow().setAttributes(attrs);

    }

    public static void showStatusBar(Activity activity) {
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        activity.getWindow().setAttributes(attrs);
    }


    public static void disableNvigationViewScrollbars(NavigationView navigationView) {

        if (navigationView != null) {
            NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
            if (navigationMenuView != null) {
                navigationMenuView.setVerticalScrollBarEnabled(false);
            }
        }

    }
}
