package com.yskj.jnga.module.activity.message;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.RxLifecycle;
import com.yskj.jnga.App;
import com.yskj.jnga.R;
import com.yskj.jnga.adapter.CommonRecyclerAdapter;
import com.yskj.jnga.adapter.CommonRecyclerViewHolder;
import com.yskj.jnga.entity.push.MessageInfo;
import com.yskj.jnga.entity.push.MessageReceive;
import com.yskj.jnga.entity.push.StatisticInfo;
import com.yskj.jnga.module.base.RxBaseActivity;
import com.yskj.jnga.network.json.RetrofitHelper;
import com.yskj.jnga.network.xml.BaseTable;
import com.yskj.jnga.network.xml.DataOperation;
import com.yskj.jnga.utils.Utils;
import com.yskj.jnga.widget.Button;
import com.yskj.jnga.widget.GifView;
import com.yskj.jnga.widget.refresh.PullToRefreshView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 信息推送详情界面
 */

public class MessageContentActivity extends RxBaseActivity {
    private StatisticInfo.BeanResult mBeanResult;
    private CommonRecyclerAdapter mAdapter;
    private ArrayList<MessageReceive> mMessages;
    private PullToRefreshView mRefreshView;
    private AlertDialog ad;
    public ProgressDialog pd;
    private MyHandler myHandler;
    public static final String sAction = "message_info";

    @Override
    public int getLayoutId() {
        return R.layout.activity_message_content;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        mBeanResult = (StatisticInfo.BeanResult) getIntent().getSerializableExtra("info");
        TextView title = findViewById(R.id.title);
        title.setText(mBeanResult.getNAME());

        ImageButton ib_back = findViewById(R.id.ib_back);
        ib_back.setOnClickListener(view -> MessageContentActivity.this.finish());

        //刷新控件
        mRefreshView = findViewById(R.id.message_pull_to_refresh);
        mRefreshView.setOnRefreshListener(this::loadData);

        myHandler = new MyHandler(this);
        initWidget();
    }

    private void initWidget() {
        mMessages = new ArrayList<>();
        RecyclerView rv = findViewById(R.id.message_list);
        rv.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CommonRecyclerAdapter<MessageReceive>(this, R.layout.item_message, mMessages) {
            @Override
            public void convertView(CommonRecyclerViewHolder holder, MessageReceive message) {
                GifView gif = holder.getView(R.id.gv_gif);
                gif.setMovieResource(R.raw.new_message_02);
                holder.setText(R.id.tv_type, message.getMS_MType());
                holder.setText(R.id.tv_send_time, message.getMS_CreateTime());
                holder.setText(R.id.tv_content, message.getMS_Content());
                holder.getView(R.id.rl_writer).setVisibility(View.GONE);
            }

        };
        rv.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new CommonRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                View v_container = LayoutInflater.from(MessageContentActivity.this).inflate(R.layout.dialog_notification, null);
                TextView tv_title = v_container.findViewById(R.id.tv_title);
                TextView tv_content = v_container.findViewById(R.id.tv_content);
                TextView tv_info = v_container.findViewById(R.id.tv_info);
                final EditText et_gconent = v_container.findViewById(R.id.et_gconent);
                Button btn_confirm = v_container.findViewById(R.id.btn_confirm);
                btn_confirm.setText("已阅");
                ad = new AlertDialog.Builder(MessageContentActivity.this).setView(v_container).show();
                final BaseTable ms_police_cks = new BaseTable();
                btn_confirm.setOnClickListener(v -> {

                    ms_police_cks.setTableName("MS_Police_cks");
                    ms_police_cks.putField("FLAG_ID", mMessages.get(position).getMS_MID());
                    ms_police_cks.putField("MS_PoliceNum", App.getInstance().getSpUtil().getAccount());
                    ms_police_cks.putField("MS_PoliceName", "");
                    ms_police_cks.putField("MS_FState", "");
                    ms_police_cks.putField("MS_GState", "");
                    ms_police_cks.putField("MS_GContent", TextUtils.isEmpty(et_gconent.getText().toString()) ? "已收到"
                            : et_gconent.getText().toString());
                    ms_police_cks.putField("MS_MID", "");
                    ms_police_cks.putField("MS_SP_State", "");
                    if (Utils.isNetworkAvailable(MessageContentActivity.this)) {

                        ad.hide();
                        myHandler.sendEmptyMessage(0);
                        new Thread(() -> {

                            StringBuffer sb = new StringBuffer();
                            BaseTable ms_police_receive = new BaseTable();
                            ms_police_receive.setTableName("MS_Police_receive");
                            ms_police_receive.setContentId(mMessages.get(position).getMS_MID());
                            ms_police_receive.putField("MS_GState", "已回复");

                            ms_police_cks.putField("MS_CreateTime",
                                    Utils.getNetTimeByType("yyyy/MM/dd HH:mm:ss"));

                            if (DataOperation.insertOrUpdateTable(ms_police_cks)) {

                                if (DataOperation.insertOrUpdateTable(ms_police_receive)) {
                                    sendBroadcast(mMessages.get(position));
                                    mMessages.remove(position);
                                    myHandler.sendEmptyMessage(1);
                                }else {
                                    myHandler.sendEmptyMessage(2);
                                }
                            }else {
                                myHandler.sendEmptyMessage(2);
                            }

                        }).start();

                    }

                });
                String title = mMessages.get(position).getMS_MType();
                String content = mMessages.get(position).getMS_Content();
                StringBuilder infoSb = new StringBuilder();
                infoSb.append("<strong>通知时间：</strong>[%s]");
                String info = String.format(infoSb.toString(), mMessages.get(position).getMS_CreateTime());

                tv_title.setText(title);
                tv_content.setText(content);
                tv_info.setText(Html.fromHtml(info));
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
        mRefreshView.setRefreshing(true);
        String policeNum = App.getInstance().getSpUtil().getAccount();
        StringBuffer sb = new StringBuffer();
        sb.append("select m.MS_POLICENUM,m.MS_CREATETIME ,m.MS_MID,m.CONTENTID,c.CONTENTNUMBER,c.DETAILS from ");
        sb.append("( ");
        sb.append("select MS_POLICENUM,MS_CREATETIME ,MS_MID,CONTENTID from ");
        sb.append("( ");
        sb.append("select a.*, rownum rn from ");
        sb.append("(select MS_POLICENUM,MS_POLICENAME,MS_PUNIT,MS_CREATETIME,MS_MID,CONTENTID from ");
        sb.append("ms_police_receive  where ms_gstate='未回复' and createts>(systimestamp -30)  and ms_policenum='");
        sb.append(policeNum);
        sb.append("' and ms_sp_state ='");
        sb.append(mBeanResult.getID());
        sb.append("' order by createts desc ) a ");
        sb.append("where rownum <=10");
        sb.append(") ");
        sb.append("where rn >=1 ");
        sb.append(") m ");
        sb.append("left join jnga_content c on c.partentid = m.MS_MID ");

        RetrofitHelper.getAppAPI().getMessageInfo(RetrofitHelper.createRequestBody(sb.toString()))
                .compose(RxLifecycle.bindUntilEvent(lifecycle(), ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(MessageInfo::getResult)
                .subscribe(new Observer<List<MessageInfo.Message>>() {
                    @Override
                    public void onCompleted() {
                        mRefreshView.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MessageContentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(List<MessageInfo.Message> messages) {
                        mMessages.clear();
                        mMessages.addAll(loadNetData(messages));
                        mAdapter.notifyDataSetChanged();
                    }
                });

    }

    private ArrayList<MessageReceive> loadNetData(List<MessageInfo.Message> messages) {
        ArrayList<MessageReceive> mss = new ArrayList<>();
        // 只查询三天内的数据
        for (MessageInfo.Message message : messages) {
            MessageReceive ms = new MessageReceive(message);
            mss.add(ms);
        }
        return mss;
    }

    public static class MyHandler extends Handler {

        WeakReference<MessageContentActivity> mActivityReference;

        MyHandler(MessageContentActivity activity) {
            mActivityReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MessageContentActivity activity = mActivityReference.get();
            switch (msg.what) {
                case 0:

                    activity.pd = new ProgressDialog(activity, android.R.style.Theme_Holo_Dialog);
                    activity.pd.setCanceledOnTouchOutside(false);
                    activity.pd.setMessage("上传中...");
                    activity.pd.show();

                    break;

                case 1:
                    if (activity.pd != null && activity.pd.isShowing()) {
                        activity.pd.cancel();
                    }
                    activity.mAdapter.notifyDataSetChanged();
                    break;
                case 2:

                    Toast.makeText(activity, "操作失败", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }
    }

    /*
 * 已阅信息通过广播发送给上级界面更新数据
 */
    public void sendBroadcast(MessageReceive ms) {
        Intent intent = new Intent(sAction);
        intent.putExtra(sAction, ms);
        sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ad != null && ad.isShowing()) {
            ad.cancel();
        }
        if (pd != null && pd.isShowing()) {
            pd.cancel();
        }

    }
}
