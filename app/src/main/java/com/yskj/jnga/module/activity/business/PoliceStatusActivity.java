package com.yskj.jnga.module.activity.business;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yskj.jnga.R;
import com.yskj.jnga.module.base.RxBaseActivity;
import com.yskj.jnga.network.xml.BaseTable;
import com.yskj.jnga.network.xml.DataOperation;
import com.yskj.jnga.task.QueryDataTask;
import com.yskj.jnga.utils.Utils;
import com.yskj.jnga.widget.anim.dialog.DialogLoading;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;

public class PoliceStatusActivity extends RxBaseActivity {

	private TextView tv;
	private AlertDialog ad;
	private String keyWord;
	private MyHandler myHandler;
	private String currentdateStr;
	private TextView tv_details__qwdd, tv_details__qjlb, tv_details_jjrsj, tv_details_qjsj, tv_reson;


	@Override
	public int getLayoutId() {
		return R.layout.activity_police_status;
	}

	@Override
	public void initViews(Bundle savedInstanceState) {
		findView();
		initData();
	}

	@Override
	public void initToolBar() {

	}

	private void initData() {
		keyWord = getIntent().getStringExtra("PoliceStatusActivity");

	}

	@Override
	protected void onStart() {
		super.onStart();
		myHandler = new MyHandler(this);
		new policeStatusTask().execute();
	}

	private void findView() {

		ImageButton ib_back = (ImageButton) findViewById(R.id.ib_back);
		ib_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PoliceStatusActivity.this.finish();

			}
		});

		tv = (TextView) findViewById(R.id.tv_content);
		tv_details__qwdd = (TextView) findViewById(R.id.tv_details__qwdd);
		tv_details__qjlb = (TextView) findViewById(R.id.tv_details__qjlb);
		tv_details_jjrsj = (TextView) findViewById(R.id.tv_details_jjrsj);
		tv_details_qjsj = (TextView) findViewById(R.id.tv_details_qjsj);
		tv_reson = (TextView) findViewById(R.id.tv_reson);
	}

	private void updateView(BaseTable table) {

		StringBuffer sb = new StringBuffer();
		sb.append("<font color='red'>");
		sb.append(table.getField("QJ_PoliceName"));
		sb.append("同志");
		sb.append("</font>");
		String startDateStr = table.getField("QJ_STime");
		String endDateStr = table.getField("QJ_Etime");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date currentdate = sdf.parse(currentdateStr);
			Date startDate = sdf.parse(startDateStr);
			Date endDate = sdf.parse(endDateStr);

			if (currentdate.before(startDate) && currentdate.before(endDate)) {
				// 当前时间小于请假开始时间
				int day1 = (int) ((startDate.getTime() - currentdate.getTime()) / (1000 * 60 * 60 * 24));
				int day2 = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24));
				sb.append(day1 + "天后将进行休假，假期时长<font color='red' >" + day2 + "</font>天");
			} else if (currentdate.after(startDate) && currentdate.before(endDate)) {
				// 当前时间大于请假开始时间小于结束时间
				int day1 = (int) ((currentdate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24));
				int day2 = (int) ((endDate.getTime() - currentdate.getTime()) / (1000 * 60 * 60 * 24));
				sb.append(day1 + "天前开始休假,距离假期结束还有<font color='red' >" + day2 + "</font>天");

			} else if (currentdate.after(startDate) && currentdate.after(endDate)) {
				// 当前时间大于请假开始时间
				int day = (int) ((currentdate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24));
				sb.append("最近一次休假时间是<font color='red' >" + day + "</font>天前,近期暂无请假申请");

			} else if (currentdate.compareTo(startDate) == 0) {
				// 当前时间等于请假开始时间
				int day2 = (int) ((endDate.getTime() - currentdate.getTime()) / (1000 * 60 * 60 * 24));
				sb.append("于今天开始休假,距离休假结束时间还有<font color='red' >" + day2 + "</font>天");
			} else if (currentdate.compareTo(endDate) == 0) {
				// 当前时间等于请假结束
				int day1 = (int) ((currentdate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24));
				sb.append("于<font color='red' >" + day1 + "</font>前开始休假,截止今天假期结束");
			}

		} catch (ParseException e) {

			e.printStackTrace();
		}

		tv.setText(Html.fromHtml(sb.toString()));
		tv_details__qwdd.setText(table.getField("QJ_Address"));
		tv_details__qjlb.setText(table.getField("QJ_JType"));
		tv_details_jjrsj.setText(table.getField("QJ_PUnit_Use"));
		tv_details_qjsj.setText(table.getField("QJ_STime") + "至" + table.getField("QJ_Etime"));
		tv_reson.setText(table.getField("QJ_Reason"));
	}

	private class policeStatusTask extends AsyncTask<String, Integer, Integer> {

		private BaseTable table;

		@Override
		protected void onPreExecute() {
			ad = DialogLoading.getProgressDialog(PoliceStatusActivity.this, "正在查询...");
			ad.setCancelable(false);
			ad.setCanceledOnTouchOutside(false);
			if (ad != null && !ad.isShowing()) {
				ad.show();
			}
		}

		@Override
		protected Integer doInBackground(String... params) {
			currentdateStr = Utils.getNetTimeByType("yyyy-MM-dd");
			table = new BaseTable();
			table.setTableName("JNGA_QJ_INFO_QUERY");
			table.putField("QJ_JType", "3");
			table.putField("QJ_PoliceNum", keyWord);
			if (DataOperation.insertOrUpdateTable(table)) {
				return 1;
			}
			return -1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			switch (result) {
			case -1:
				if (ad != null && ad.isShowing()) {
					ad.cancel();
				}
				tv.setText("未查到有效数据");
				break;

			case 1:

				final Timer timer = new Timer();

				QueryDataTask.QueryDataListener qdl = new QueryDataTask.QueryDataListener() {

					@Override
					public void onCompleted() {
						myHandler.sendEmptyMessage(-1);

					}

				};
				timer.schedule(new QueryDataTask(timer, "JNGA_QJ_INFO_RESULT", qdl) {

					@Override
					public void doingTask() {

						String sql = "from ( select * from JNGA_QJ_INFO_RESULT where flag_id = '" + table.getContentId()
								+ "') JNGA_QJ_INFO_RESULT";
						ArrayList<BaseTable> tables = DataOperation.queryTable("JNGA_QJ_INFO_RESULT", sql);
						if (tables.size() != 0) {
							timer.cancel();
							Message msg = myHandler.obtainMessage();
							msg.what = 1;
							msg.obj = tables;
							myHandler.sendMessage(msg);

						}

					}

				}, 2 * 1000, 2 * 1000);

				break;

			default:
				break;
			}
		}

	}

	/**
	 * 消息队列
	 * 
	 * @author saber
	 *
	 */

	private class MyHandler extends Handler {

		WeakReference<PoliceStatusActivity> activityReference;

		public MyHandler(PoliceStatusActivity activity) {
			activityReference = new WeakReference<PoliceStatusActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {

			PoliceStatusActivity activity = activityReference.get();
			switch (msg.what) {
			case -1:

				if (ad != null && ad.isShowing()) {
					ad.cancel();
				}
				activity.tv.setText("未查到有效数据");
				break;
			case 1:

				if (ad != null && ad.isShowing()) {
					ad.cancel();
				}
				@SuppressWarnings("unchecked")
				ArrayList<BaseTable> tables = (ArrayList<BaseTable>) msg.obj;
				updateView(tables.get(0));
				break;

			default:
				break;
			}
		}
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		if (ad != null && ad.isShowing()) {
			ad.cancel();
		}

	}

}
