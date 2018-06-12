package com.yskj.jnga.module.activity.message;

import android.content.Intent;
import android.graphics.Color;
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

import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
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
                //holder.setTextColor(R.id.tv_title, Color.WHITE);
                holder.setText(R.id.tv_content, "有效统计3天,更多请联系大数据中心");
                if (result.getCOUNT() != 0) {
                    GifView gif = holder.getView(R.id.gif);
                    gif.setMovieResource(R.raw.new_message);
                }
                ColumnChartView columnChartView = holder.getView(R.id.ccv);
                generateDefaultData(columnChartView, result);
                columnChartView.setOnValueTouchListener(new ColumnChartOnValueSelectListener() {

                    @Override
                    public void onValueDeselected() {

                    }

                    @Override
                    public void onValueSelected(int arg0, int arg1, SubcolumnValue arg2) {

//                        String title = "";
//                        switch (arg0) {
//                            case 0:
//                                title = "总计";
//                                break;
//                            case 1:
//                                title = "未读";
//                                break;
//                            case 2:
//                                title = "已读";
//                                break;
//
//                            default:
//                                break;
//                        }

                        Intent intent = new Intent(MessagePushActivity.this, MessageContentActivity.class);
                        intent.putExtra("info", mBeanResults.get(holder.getLayoutPosition()));
                        startActivity(intent);


                    }
                });
            }

        };
        rv.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new CommonRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MessagePushActivity.this, MessageContentActivity.class);
                intent.putExtra("info", mBeanResults.get(position));
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
        sb.append("select n.id,n.name,sum( case when r.contentid is not null then 1 else 0 end ) count ,");
        sb.append(" sum( case when r.contentid is not null and r.ms_gstate ='已回复' then 1 else 0 end ) READ , ");
        sb.append(" sum( case when r.contentid is not null and r.ms_gstate ='未回复' then 1 else 0 end ) UNREAD  ");
        sb.append(" from ");
        sb.append("(select * from 	ms_police_receive  where createts>(systimestamp -3)  and ms_policenum='");
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


    private void generateDefaultData(ColumnChartView ccv, StatisticInfo.BeanResult bean) {
        int numColumns = 3;
        // Column can have many numSubColumns, here by default I use 1 subColumn
        // in each of 8 columns.
        List<Column> columns = new ArrayList<>();
        List<SubcolumnValue> values;
        ArrayList<AxisValue> axisValuesX = new ArrayList<>();// 定义X轴刻度值的数据集合
        // ArrayList<AxisValue> axisValuesY = new ArrayList<>();//定义Y轴刻度值的数据集合
        for (int i = 0; i < numColumns; ++i) {

            values = new ArrayList<>();
            switch (i) {
                case 0:
                    values.add(new SubcolumnValue(bean.getCOUNT(), ChartUtils.pickColor()));
                    axisValuesX.add(new AxisValue(i).setValue(i).setLabel("总计"));// 添加X轴显示的刻度值并设置X轴显示的内容
                    break;
                case 1:
                    values.add(new SubcolumnValue(bean.getREAD(), ChartUtils.pickColor()));
                    axisValuesX.add(new AxisValue(i).setValue(i).setLabel("已读"));// 添加X轴显示的刻度值并设置X轴显示的内容
                    break;
                case 2:
                    values.add(new SubcolumnValue(bean.getUNREAD(), ChartUtils.pickColor()));
                    axisValuesX.add(new AxisValue(i).setValue(i).setLabel("未读"));// 添加X轴显示的刻度值并设置X轴显示的内容
                    break;

                default:
                    break;
            }
            //axisValuesX.add(new AxisValue(i).setValue(i));// 添加X轴显示的刻度值并设置X轴显示的内容
            // values.add(new SubcolumnValue(Float.valueOf(data.get("COUNT")),
            // ChartUtils.pickColor()));
            // axisValuesY.add(new AxisValue(i).setValue(i));// 添加Y轴显示的刻度值
            // axisValuesX.add(new
            // AxisValue(i).setValue(i).setLabel(data.get("TYPE")));//
            // 添加X轴显示的刻度值并设置X轴显示的内容
            // axisValuesY.add(new AxisValue(i).setValue(i));// 添加Y轴显示的刻度值
            // axisValuesX.add(new
            // AxisValue(i).setValue(i).setLabel(dataList.get(i).get("TYPE")));//
            // 添加X轴显示的刻度值并设置X轴显示的内容

            Column column = new Column(values);
            column.setHasLabels(true);
            column.setHasLabelsOnlyForSelected(false);
            columns.add(column);

        }

        ColumnChartData data = new ColumnChartData(columns);
        Axis axisX = new Axis();
        axisX.setValues(axisValuesX);
        Axis axisY = new Axis().setHasLines(true);
        // axisY.setValues(axisValuesY);
        //axisX.setName("类型");
        axisY.setName("数量");
        axisX.setTextColor(Color.WHITE);// 设置X轴文字颜色
        axisY.setTextColor(Color.WHITE);// 设置Y轴文字颜色
        axisX.setHasTiltedLabels(true);
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);
        data.setFillRatio(0.5F); // 参数即为柱子宽度的倍数，范围只能在0到1之间
        ccv.setColumnChartData(data);
        ccv.setValueSelectionEnabled(false);

    }
}
