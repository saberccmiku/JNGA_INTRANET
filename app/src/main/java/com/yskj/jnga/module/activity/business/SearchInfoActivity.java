package com.yskj.jnga.module.activity.business;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageButton;

import com.yskj.jnga.R;
import com.yskj.jnga.module.base.BaseActivity;
import com.yskj.jnga.module.fragment.SearchInfoFragment;

/**
 * 业务查询界面，根据点击的查询类型呈现不同的界面
 *
 * @author perng
 */
public class SearchInfoActivity extends BaseActivity {
    private ActivityStateListener activityStateListenerAdapter;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private SearchInfoFragment searchInfoFragment; // 搜索页面Fragment
    private ImageButton ib_search;

    @Override
    public int getLayoutId() {
        return R.layout.activity_search_info;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

        ImageButton ib_back = findViewById(R.id.ib_back);
        ib_back.setOnClickListener(v->onBackPressed());
        ib_search = findViewById(R.id.ib_search);
        ib_search.setOnClickListener(v->searchInfoFragment.search());
        GetSearchFragment();

        setActivityStateListener(new ActivityStateListenerApdater() {
            @Override
            public void onSaveInstanceState(Bundle outState) {
                isSave = true;
            }

            @Override
            public void onResume() {
                if (isSave) {
                    if (isBackPressed) {
                        SearchInfoActivity.super.onBackPressed();
                        isBackPressed = false;
                    }
                    isSave = false;
                }
            }
        });
        if (activityStateListenerAdapter != null)
            activityStateListenerAdapter.onCreate(savedInstanceState);

    }

    @Override
    public void initToolBar() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (activityStateListenerAdapter != null)
            activityStateListenerAdapter.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (activityStateListenerAdapter != null)
            activityStateListenerAdapter.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (activityStateListenerAdapter != null)
            activityStateListenerAdapter.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (activityStateListenerAdapter != null)
            activityStateListenerAdapter.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (activityStateListenerAdapter != null)
            activityStateListenerAdapter.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (activityStateListenerAdapter != null)
            activityStateListenerAdapter.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (activityStateListenerAdapter != null)
            activityStateListenerAdapter.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (activityStateListenerAdapter != null)
            activityStateListenerAdapter.onRestoreInstanceState(savedInstanceState);
    }


    /**
     * 根据SearchMainActivity中传入的Extra切换不同的Fragment界面
     */
    private void GetSearchFragment() {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        String tag = (String) getIntent().getSerializableExtra("Tag"); // "Tag"即搜索类别

        /** 应项目需求，暂时屏蔽部分页面功能 *//*
         * if(!Constants.ONE_KEY_RESEARCH.equals(tag) &&
         * !Constants.GOODS_RESEARCH.equals(tag) &&
         * !Constants.CASE_RESEARCH.equals(tag)) {
         * //打开搜索页面 searchInfoFragment = new
         * SearchInfoFragment(tag);
         * //将搜索类别信息参数传入(searchInfoFragment会根据搜索类别，
         * 展示对应的搜索页面)
         * fragmentTransaction.add(R.id.content_frame,
         * searchInfoFragment).commit(); }
         */

        // 打开搜索页面
        searchInfoFragment = SearchInfoFragment.newInstance(tag); // 将搜索类别信息参数传入(searchInfoFragment会根据搜索类别，展示对应的搜索页面)
        fragmentTransaction.add(R.id.content_frame, searchInfoFragment).commit();
    }

    private boolean isSave = false;
    private boolean isBackPressed = false;

    @Override
    public void onBackPressed() {
        if (!isSave) {
            super.onBackPressed();
        } else {
            isBackPressed = true;
        }
    }

    public void isShowSearch(boolean isShow){

        if (isShow){
            ib_search.setVisibility(View.VISIBLE);
        }else{
            ib_search.setVisibility(View.GONE);
        }
    }

    private void setActivityStateListener(ActivityStateListener activityStateListener) {
        this.activityStateListenerAdapter = activityStateListener;
    }

    private class ActivityStateListenerApdater implements ActivityStateListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {

        }

        @Override
        public void onRestart() {

        }

        @Override
        public void onStart() {

        }

        @Override
        public void onResume() {

        }

        @Override
        public void onPause() {

        }

        @Override
        public void onStop() {

        }

        @Override
        public void onDestroy() {

        }

        @Override
        public void onSaveInstanceState(Bundle outState) {

        }

        @Override
        public void onRestoreInstanceState(Bundle savedInstanceState) {

        }
    }

    private interface ActivityStateListener {
        public void onCreate(Bundle savedInstanceState);

        public void onRestart();

        public void onStart();

        public void onResume();

        public void onPause();

        public void onStop();

        public void onDestroy();

        public void onSaveInstanceState(Bundle outState);

        public void onRestoreInstanceState(Bundle savedInstanceState);
    }
}
