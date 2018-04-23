package com.yskj.jnga.module.activity.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.RxLifecycle;
import com.yskj.jnga.App;
import com.yskj.jnga.R;
import com.yskj.jnga.adapter.CommonRecyclerAdapter;
import com.yskj.jnga.adapter.CommonRecyclerViewHolder;
import com.yskj.jnga.entity.push.StatisticInfo;
import com.yskj.jnga.module.base.RxBaseActivity;
import com.yskj.jnga.network.json.RetrofitHelper;
import com.yskj.jnga.widget.GifView;
import com.yskj.jnga.widget.refresh.PullToRefreshView;
import java.util.ArrayList;
import java.util.List;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 信息推送二级三级分类界面
 */

public class MessagePushActivity extends RxBaseActivity {
    private ProgressBar load_local_progress;
    private String mTitle;
    public static final int REFRESH_DELAY = 2000;
    private ArrayList<StatisticInfo.BeanResult> mBeanResults;
    private CommonRecyclerAdapter mAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_message_push;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        mTitle = getIntent().getStringExtra("title");
        load_local_progress = findViewById(R.id.load_local_progress);
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(mTitle);
        tv_title.setVisibility(View.VISIBLE);
        //返回控件
        ImageButton btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v -> MessagePushActivity.this.finish());
        //刷新控件
        PullToRefreshView refreshView = findViewById(R.id.message_pull_to_refresh);
        refreshView.setOnRefreshListener(() -> refreshView.postDelayed(() -> refreshView.setRefreshing(false), REFRESH_DELAY));
        //加载列表
        initWidget();
    }

    /**
     * 加载列表
     */
    private void initWidget() {
        RecyclerView rv = findViewById(R.id.message_list);
        rv.setLayoutManager(new LinearLayoutManager(this));
        mBeanResults = new ArrayList<>();
        mAdapter = new CommonRecyclerAdapter<StatisticInfo.BeanResult>(this, R.layout.item_message_list, mBeanResults) {
            @Override
            public void convertView(CommonRecyclerViewHolder holder, StatisticInfo.BeanResult result) {
                holder.setText(R.id.tv_title, result.getNAME());
                holder.setText(R.id.tv_conent, "历史信息" + result.getCOUNT() + ":有效统计30天");
                if (result.getCOUNT() != 0) {
                    GifView gif = holder.getView(R.id.gif);
                    gif.setMovieResource(R.raw.new_message);
                }
            }

        };
        rv.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new CommonRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MessagePushActivity.this, MessageContentActivity.class);
                intent.putExtra("info",mBeanResults.get(position));
                startActivity(intent);
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
    protected void onStart() {
        super.onStart();
        loadData();
    }

    @Override
    public void loadData() {
        String policeNum = App.getInstance().getSpUtil().getAccount();
        load_local_progress.setVisibility(View.VISIBLE);
        StringBuffer sb = new StringBuffer();
        sb.append("select n.id,n.name,sum( case when r.contentid is not null then 1 else 0 end ) count from");
        sb.append("(select * from 	ms_police_receive  where createts>(systimestamp -30)  and ms_policenum='");
        sb.append(policeNum);
        sb.append("' ) r ");
        sb.append("right join ");
        sb.append("(select distinct t.id id,t.tname name  from jnga_ms_type t where t.pid = ");
        sb.append("(select id from  jnga_ms_type  where tname = '");
        sb.append(mTitle);
        sb.append("' ) ) n ");
        sb.append("	on n.id = r.ms_sp_state   group by n.id,n.name ");
        RetrofitHelper.getAppAPI().getStatisticInfo(RetrofitHelper.createRequestBody(sb.toString()))
                .compose(RxLifecycle.bindUntilEvent(lifecycle(), ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(StatisticInfo::getResult)
                .subscribe(new Observer<List<StatisticInfo.BeanResult>>() {
                    @Override
                    public void onCompleted() {
                        load_local_progress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MessagePushActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(List<StatisticInfo.BeanResult> beanResults) {
                        mBeanResults.clear();
                        mBeanResults.addAll(beanResults);
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }
}
