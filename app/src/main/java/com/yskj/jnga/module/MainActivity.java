package com.yskj.jnga.module;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.RxLifecycle;
import com.yskj.jnga.App;
import com.yskj.jnga.R;
import com.yskj.jnga.adapter.CommonRecyclerAdapter;
import com.yskj.jnga.adapter.CommonRecyclerViewHolder;
import com.yskj.jnga.entity.MenuEntity;
import com.yskj.jnga.entity.push.StatisticInfo;
import com.yskj.jnga.module.activity.message.MessagePushMenuActivity;
import com.yskj.jnga.module.base.RxBaseActivity;
import com.yskj.jnga.network.json.RetrofitHelper;
import com.yskj.jnga.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends RxBaseActivity {

    private ArrayList<MenuEntity> menuEntities;
    private CommonRecyclerAdapter mCommonRecyclerAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        NavigationView navigationMenuView = findViewById(R.id.nav);
        UIUtils.disableNvigationViewScrollbars(navigationMenuView);
        findView();
        loadData();
    }

    private void findView() {
        RecyclerView rv_menu = findViewById(R.id.rv_menu);
        rv_menu.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
        menuEntities = new ArrayList<>();
        MenuEntity seEntity = new MenuEntity("执法监督", R.drawable.zfjd, null);
        MenuEntity pwEntity = new MenuEntity("业务工作", R.drawable.ywgz, null);
        MenuEntity dwEntity = new MenuEntity("日常办公", R.drawable.rcbg, null);
        MenuEntity tmEntity = new MenuEntity("队伍管理", R.drawable.dwgl, null);
        MenuEntity tdEntity = new MenuEntity("待办工作", R.drawable.dbgz, null);
        MenuEntity mpEntity = new MenuEntity("信息推送", R.drawable.xxts, new Intent(MainActivity.this, MessagePushMenuActivity.class));
        menuEntities.add(seEntity);
        menuEntities.add(pwEntity);
        menuEntities.add(dwEntity);
        menuEntities.add(tmEntity);
        menuEntities.add(tdEntity);
        menuEntities.add(mpEntity);
        mCommonRecyclerAdapter = new CommonRecyclerAdapter<MenuEntity>(this, R.layout.item_nemu, menuEntities) {
            @Override
            public void convertView(CommonRecyclerViewHolder holder, MenuEntity entity) {
                holder.setBackgroundResource(R.id.iv, entity.getResId());
                holder.setText(R.id.tv, entity.getTitle());
                holder.setText(R.id.tv_cont, String.valueOf(entity.getMark()));
                if (entity.getMark() > 0) {
                    holder.getView(R.id.tv_cont).setVisibility(View.VISIBLE);
                } else {
                    holder.getView(R.id.tv_cont).setVisibility(View.GONE);
                }
            }
        };
        rv_menu.setAdapter(mCommonRecyclerAdapter);
        mCommonRecyclerAdapter.setOnItemClickListener(new CommonRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (menuEntities.get(position).getIntent() == null) {
                    Toast.makeText(MainActivity.this, "该功能需要定位功能请使用外网客户端操作", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(menuEntities.get(position).getIntent());
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void loadData() {
        String policeNum = App.getInstance().getSpUtil().getAccount();
        StringBuffer sb = new StringBuffer();
        sb.append("select count(1) count from ms_police_receive r1 ");
        sb.append("where r1.ms_gstate = '未回复' and r1.ms_policenum ='");
        sb.append(policeNum);
        sb.append("' and to_date(r1.ms_createtime,'yyyy-mm-dd hh24:mi:ss') > sysdate-3 ");
        RetrofitHelper.getAppAPI()//基础url
                .getStatisticInfo(RetrofitHelper.createRequestBody(sb.toString()))//接口后缀URL
                .compose(RxLifecycle.bindUntilEvent(lifecycle(), ActivityEvent.DESTROY))//设计是否备份数据
                .map(StatisticInfo::getResult)//得到JSON子数组
                .subscribeOn(Schedulers.io())//设计线程读写方式
                .observeOn(AndroidSchedulers.mainThread())//指定线程运行的位置
                .subscribe(new Observer<List<StatisticInfo.BeanResult>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(List<StatisticInfo.BeanResult> beanResults) {
                        if (beanResults != null && beanResults.size() != 0) {
                            //更新信息推送给图标上的数量字符
                            menuEntities.get(5).setMark(beanResults.get(0).getCOUNT());
                            mCommonRecyclerAdapter.notifyDataSetChanged();
                        }

                    }

                });
    }
}