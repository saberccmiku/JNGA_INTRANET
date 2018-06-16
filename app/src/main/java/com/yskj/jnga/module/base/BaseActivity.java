package com.yskj.jnga.module.base;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

public abstract class BaseActivity extends FragmentActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //禁止横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //设置布局内容
        setContentView(getLayoutId());
        //初始化控件
        initViews(savedInstanceState);
    }

    /**
     * 设置布局
     *
     * @return 布局id
     */
    public abstract int getLayoutId();

    /**
     * 初始化控件
     *
     * @param savedInstanceState 状态值
     */
    public abstract void initViews(Bundle savedInstanceState);

    /**
     * 初始化toolbar
     */
    public abstract void initToolBar();

    /**
     * 加载数据
     */
    public void loadData() {
    }

    /**
     * 显示进度条
     */
    public void showProgressBar() {
    }

    /**
     * 隐藏进度条
     */
    public void hideProgressBar() {
    }

    /**
     * 初始化recyclerView
     */
    public void initRecyclerView() {
    }

    /**
     * 初始化refreshLayout
     */
    public void initRefreshLayout() {
    }

    /**
     * 设置数据显示
     */
    public void finishTask() {
    }

}
