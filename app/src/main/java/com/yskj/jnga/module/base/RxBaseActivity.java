package com.yskj.jnga.module.base;

import android.os.Bundle;
import android.view.Window;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

/**
 * Created by 63987 on 2018/4/6
 */

public abstract class RxBaseActivity extends RxAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
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
