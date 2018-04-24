package com.yskj.jnga.module.activity.message;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.RxLifecycle;
import com.yskj.jnga.App;
import com.yskj.jnga.R;
import com.yskj.jnga.adapter.CommonRecyclerAdapter;
import com.yskj.jnga.adapter.CommonRecyclerViewHolder;
import com.yskj.jnga.entity.MenuEntity;
import com.yskj.jnga.entity.push.MessageReceive;
import com.yskj.jnga.entity.push.StatisticInfo;
import com.yskj.jnga.module.base.RxBaseActivity;
import com.yskj.jnga.network.json.RetrofitHelper;
import com.yskj.jnga.widget.GifView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MessagePushMenuActivity extends RxBaseActivity implements OnClickListener {

    private ArrayList<MenuEntity> mMenuEntities;
    private CommonRecyclerAdapter mAdapter;
    private TextView tv_count_01, tv_count_02;
    private GifView mGif;
    private ImageView mPng;
    private InformationReceiver ir;

    //总栏目
    private MenuEntity[] menusEntityList_03 = {new MenuEntity("20180206000002", "警务工作", R.drawable.jwxx, 0),
            new MenuEntity("20180206000001", "非警务工作", R.drawable.fjwxx, 0)};
    // 警务工作
    private MenuEntity[] menusEntityList_01 = {new MenuEntity("20180206000007", "110警情", R.mipmap.police_alarm, 0),
            new MenuEntity("20180206000008", "执法办案", R.mipmap.baqgl, 0),
            new MenuEntity("20180206000009", "社会面管控", R.mipmap.other, 0)};

    // 非警务工作
    private MenuEntity[] menusEntityList_02 = {new MenuEntity("20180206000003", "勤务报备", R.mipmap.qwbbqd, 0),
            new MenuEntity("20180206000004", "请销假", R.mipmap.zdry, 0),
            new MenuEntity("20180206000005", "公务用车", R.mipmap.ddccz, 0),
            new MenuEntity("20180206000022", "分局消息", R.mipmap.other, 0)};


    @Override
    public int getLayoutId() {
        return R.layout.activity_message_push_menu;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

        // 注册广播
        ir = new InformationReceiver();
        IntentFilter filter = new IntentFilter(MessageContentActivity.sAction);
        this.registerReceiver(ir, filter);

        ImageButton ib_back = findViewById(R.id.ib_back);
        ib_back.setOnClickListener(this);
        ImageButton ib_01 = findViewById(R.id.ib_01);
        ib_01.setOnClickListener(this);
        ImageButton ib_02 = findViewById(R.id.ib_02);
        ib_02.setOnClickListener(this);

        tv_count_01 = findViewById(R.id.tv_count_01);
        tv_count_02 = findViewById(R.id.tv_count_02);

        mGif = findViewById(R.id.gv_gif);
        mPng = findViewById(R.id.iv);

        RecyclerView rv = findViewById(R.id.rv);
        rv.setLayoutManager(new GridLayoutManager(MessagePushMenuActivity.this, 3));
        mMenuEntities = new ArrayList<>();
        mAdapter = new CommonRecyclerAdapter<MenuEntity>(this, R.layout.item_nemu, mMenuEntities) {
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
        rv.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new CommonRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startActivity(new Intent(MessagePushMenuActivity.this, MessagePushActivity.class).putExtra("title", mMenuEntities.get(position).getTitle()));
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        updateMenuView(menusEntityList_01);
    }

    /**
     * 根据按钮改变菜单
     *
     * @param menuEntities 菜单数组
     */
    private void updateMenuView(MenuEntity[] menuEntities) {

        List<MenuEntity> entities = Arrays.asList(menuEntities);
        mMenuEntities.clear();
        mMenuEntities.addAll(entities);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 更新主菜单
     *
     * @param menuEntities 主菜单数组
     */
    private void updateBottomMenu(MenuEntity[] menuEntities) {

        for (MenuEntity menuEntity : menuEntities) {

            if (menuEntity.getTitle().equals("警务工作")) {
                setBottomMenuText(tv_count_01, menuEntity.getMark());
            } else if (menuEntity.getTitle().equals("非警务工作")) {
                setBottomMenuText(tv_count_02, menuEntity.getMark());
            }
        }

    }

    private void setBottomMenuText(TextView tv, int count) {

        if (count == 0) {
            tv.setVisibility(View.GONE);
            return;
        } else {
            tv.setText(String.valueOf(count));
        }
        tv.setVisibility(View.VISIBLE);
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
        setProgress(true);
        String policeNum = App.getInstance().getSpUtil().getAccount();
        StringBuffer sb = new StringBuffer();
        sb.append("select t1.id id,t1.pid pid,sum( case when r2.contentid is not null then 1 else 0 end ) count from ms_police_receive r2 ");
        sb.append("right join ");
        sb.append("(select * from (select t.id,t.pid from jnga_ms_type t where t.ms_slevel =3 )) t1 ");
        sb.append(" on  r2.ms_gstate = '未回复' and r2.ms_policenum ='");
        sb.append(policeNum);
        sb.append("' and to_date(r2.ms_createtime,'yyyy-mm-dd hh24:mi:ss') > sysdate-3 and t1.id = r2.ms_sp_state group by id,pid");
        RetrofitHelper.getAppAPI()//基础url
                .getStatisticInfo(RetrofitHelper.createRequestBody(sb.toString()))//接口后缀URL
                .compose(RxLifecycle.bindUntilEvent(lifecycle(), ActivityEvent.DESTROY))//设计是否备份数据
                .map(StatisticInfo::getResult)//得到JSON子数组
                .subscribeOn(Schedulers.io())//设计线程读写方式
                .observeOn(AndroidSchedulers.mainThread())//指定线程运行的位置
                .subscribe(new Observer<List<StatisticInfo.BeanResult>>() {
                    @Override
                    public void onCompleted() {
                        setProgress(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MessagePushMenuActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(List<StatisticInfo.BeanResult> beanResults) {

                        //统计二级分类对应的三级分类数量
                        if (beanResults != null && beanResults.size() != 0) {
                            for (MenuEntity entity : menusEntityList_01) {
                                entity.setMark(0);
                                for (StatisticInfo.BeanResult beanResult : beanResults) {
                                    if (entity.getId().equals(beanResult.getPID())) {
                                        entity.setMark(entity.getMark() + beanResult.getCOUNT());
                                    }
                                }
                            }

                            for (MenuEntity entity : menusEntityList_02) {
                                entity.setMark(0);
                                for (StatisticInfo.BeanResult beanResult : beanResults) {
                                    if (entity.getId().equals(beanResult.getPID())) {
                                        entity.setMark(entity.getMark() + beanResult.getCOUNT());
                                    }

                                }
                            }


                            //统计一级分类对应的二级分类数量
                            menusEntityList_03[0].setMark(0);
                            for (MenuEntity entity : menusEntityList_01) {

                                menusEntityList_03[0].setMark(menusEntityList_03[0].getMark() + entity.getMark());
                            }
                            menusEntityList_03[1].setMark(0);
                            for (MenuEntity entity : menusEntityList_02) {

                                menusEntityList_03[1].setMark(menusEntityList_03[1].getMark() + entity.getMark());
                            }

                            //更新UI
                            mAdapter.notifyDataSetChanged();
                            updateBottomMenu(menusEntityList_03);
                        }

                    }
                });
    }

    /**
     * 设置信息推送首界面数据加载状态
     *
     * @param isLoad 是否加载数据
     */
    private void setProgress(boolean isLoad) {

        if (isLoad) {
            mGif.setMovieResource(R.raw.zdtb);
            mGif.setVisibility(View.VISIBLE);
            mPng.setVisibility(View.GONE);
        } else {
            mGif.setVisibility(View.GONE);
            mPng.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_back:
                MessagePushMenuActivity.this.finish();
                break;
            case R.id.ib_01:
                updateMenuView(menusEntityList_01);
                break;
            case R.id.ib_02:
                updateMenuView(menusEntityList_02);
                break;
        }
    }


    /**
     * 处理下级界面信息处理信息情况，更新本界面
     *
     * @author saber
     *
     */
    private class InformationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            MessageReceive ms = (MessageReceive) intent.getSerializableExtra(MessageContentActivity.sAction);
            for (MenuEntity result : mMenuEntities) {
                if (result.getId().equals(ms.getMS_MID())) {
                    int count = result.getMark() - 1;
                    if (count - 1 < 0) {
                        result.setMark(0);
                    } else {
                        result.setMark(count);
                    }

                }
            }
            mAdapter.notifyDataSetChanged();

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ir != null) {
            unregisterReceiver(ir);
        }
    }
}
