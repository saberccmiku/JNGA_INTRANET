package com.yskj.jnga.module.activity.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yskj.jnga.App;
import com.yskj.jnga.R;
import com.yskj.jnga.adapter.CommonAdapter;
import com.yskj.jnga.adapter.ViewHolder;
import com.yskj.jnga.module.base.RxBaseActivity;
import com.yskj.jnga.network.xml.BaseTable;
import com.yskj.jnga.network.xml.DataOperation;
import com.yskj.jnga.task.QueryDataTask;
import com.yskj.jnga.utils.Utils;
import com.yskj.jnga.widget.SystemBarTintManager;
import com.yskj.jnga.widget.anim.dialog.DialogLoading;
import com.yskj.jnga.widget.picker.CustomDatePicker;

public class SearchAlarmActivity extends RxBaseActivity implements OnClickListener {

	private CustomDatePicker startTimeDatePicker, endTimeDatePicker;
	private TextView tv_start_time, tv_end_time, tv_station;
	private AlertDialog ad;
	private ImageButton ib_back, ib_search;
	private AlertDialog loadingDialog;
	private ArrayList<BaseTable> alarmTables;
	private ListView lv;
	private CommonAdapter<BaseTable> adapter;


	@Override
	public int getLayoutId() {
		return R.layout.activity_search_alarm;
	}

	@Override
	public void initViews(Bundle savedInstanceState) {
		// 设置通知栏颜色
		setNotificationColor();
		findView();
		initWidget();
	}

	@Override
	public void initToolBar() {

	}

	private void initWidget() {
		alarmTables = new ArrayList<>();
		adapter = new CommonAdapter<BaseTable>(this, alarmTables,R.layout.item_message_push) {

			@Override
			public void convert(ViewHolder viewHolder, BaseTable table) {
				viewHolder.setText(R.id.tv_type, table.getField("JCJ_AFDD"));
				viewHolder.setText(R.id.tv_send_time, table.getField("JCJ_BJSJ"));
				viewHolder.setText(R.id.tv_content, table.getField("JCJ_BJNR"));
				viewHolder.setText(R.id.tv_department, table.getField("JCJ_SSJG"));
				viewHolder.setText(R.id.tv_sender, table.getField("JCJ_BJSM"));

			}
		};

		lv.setAdapter(adapter);

	}

	private void findView() {
		tv_start_time = (TextView) findViewById(R.id.tv_start_time);
		tv_end_time = (TextView) findViewById(R.id.tv_end_time);
		tv_start_time.setOnClickListener(this);
		tv_end_time.setOnClickListener(this);
		startTimeDatePicker = initDatePicker(tv_start_time);
		endTimeDatePicker = initDatePicker(tv_end_time);

		tv_station = (TextView) findViewById(R.id.tv_station);
		tv_station.setOnClickListener(this);
		ib_back = (ImageButton) findViewById(R.id.ib_back);
		ib_back.setOnClickListener(this);
		ib_search = (ImageButton) findViewById(R.id.ib_search);
		ib_search.setOnClickListener(this);

		lv = (ListView) findViewById(R.id.lv);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_start_time:
			startTimeDatePicker.show(tv_start_time.getText().toString());
			break;
		case R.id.tv_end_time:
			endTimeDatePicker.show(tv_end_time.getText().toString());
			break;
		case R.id.tv_station:
			showDialog(tv_station);
			break;
		case R.id.ib_back:
			this.finish();
			break;
		case R.id.ib_search:
			if (Utils.isNetworkAvailable(this)) {
				if (TextUtils.isEmpty(tv_start_time.getText().toString())) {
					Toast.makeText(this, "请选择起始时间", Toast.LENGTH_SHORT).show();
				} else if (TextUtils.isEmpty(tv_end_time.getText().toString())) {
					Toast.makeText(this, "请选择结束时间", Toast.LENGTH_SHORT).show();
				} else if (TextUtils.isEmpty(tv_station.getText().toString())) {
					Toast.makeText(this, "请选择管辖机构名称", Toast.LENGTH_SHORT).show();
				} else {
					new searchAlarmTask().execute("load");
				}
			}
			break;
		default:
			break;
		}

	}

	private CustomDatePicker initDatePicker(final TextView tv) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, -30);
		date = calendar.getTime();
		String startTime = sdf.format(date);
		// tv.setText(now.split(" ")[0]);
		tv.setText(Utils.getNowTime("yyyy-MM-dd HH:mm"));

		CustomDatePicker customDatePicker = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
			@Override
			public void handle(String time) { // 回调接口，获得选中的时间
				tv.setText(time.substring(0, 16));
			}
		}, startTime, Utils.getNowTime("yyyy-MM-dd HH:mm")); // 初始化日期格式请用：yyyy-MM-dd
															// HH:mm，否则不能正常运行
		customDatePicker.showSpecificTime(true); // 显示时分
		customDatePicker.setIsLoop(false); // 不允许循环滚动

		return customDatePicker;

	}

	private void showDialog(final TextView textView) {

		String[] departments = { "福建园派出所", "明阳派出所", "吴圩派出所", "水上派出所", "沙井派出所", "亭子派出所", "那洪派出所", "五一派出所", "经济技术开发区派出所",
				"江南派出所", "江西派出所", "苏圩派出所", "延安派出所" };
		ArrayList<String> departmentList = new ArrayList<>();
		for (String department : departments) {
			departmentList.add(department);
		}

		View v_container = LayoutInflater.from(this).inflate(R.layout.dialog_window_feedback, null);
		ad = new AlertDialog.Builder(this).setView(v_container).show();
		GridView gv_window = (GridView) v_container.findViewById(R.id.gv_window);
		CommonAdapter<String> adapter = new CommonAdapter<String>(this, departmentList,R.layout.item_window_feedback) {

			@Override
			public void convert(ViewHolder viewHolder, final String bean) {
				viewHolder.setText(R.id.tv_01, bean);
				TextView tv = viewHolder.getView(R.id.tv_01);
				tv.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						textView.setText(bean);
						ad.hide();
					}
				});

			}
		};

		gv_window.setAdapter(adapter);

	}

	// 设置通知栏颜色
	private void setNotificationColor() {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Utils.setTranslucentStatus(this, true);
			SystemBarTintManager tintManager = new SystemBarTintManager(this);
			tintManager.setStatusBarTintEnabled(true);
			tintManager.setStatusBarTintResource(R.drawable.message_view);// 通知栏所需颜色
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (ad != null && ad.isShowing()) {
			ad.cancel();
		}
		if (loadingDialog != null && loadingDialog.isShowing()) {
			loadingDialog.cancel();
		}
	}

	public class searchAlarmTask extends AsyncTask<Object, Integer, Integer> {

		@Override
		protected void onPreExecute() {
			if (loadingDialog == null) {
				loadingDialog = DialogLoading.getProgressDialog(SearchAlarmActivity.this, "查询中...\n(返回键取消)"); // 显示提示对话框

			}
			if (loadingDialog != null && !loadingDialog.isShowing()) {
				loadingDialog.show();
			}

		}

		@SuppressWarnings("unchecked")
		@Override
		protected Integer doInBackground(Object... params) {
			int result = 0;

			switch ((String) params[0]) {
			case "load":
				BaseTable alarmQueryTable = new BaseTable();
				alarmQueryTable.setTableName("JNGA_JCJ_QUERY");
				alarmQueryTable.putField("JCJ_POLICENUM", App.getInstance().getSpUtil().getAccount());
				alarmQueryTable.putField("JCJ_BJSJ", tv_start_time.getText().toString());
				alarmQueryTable.putField("JCJ_CJSJ", tv_end_time.getText().toString());
				alarmQueryTable.putField("JCJ_SSJG", tv_station.getText().toString());
				alarmQueryTable.putField("JCJ_JD", "10");// 每页数量
				alarmQueryTable.putField("JCJ_WD", "1");// 页码
				if (DataOperation.insertOrUpdateTable(alarmQueryTable)) {
					result = 1;
					new SearchAlarmTimer("JNGA_JCJ_RESULT", alarmQueryTable.getContentId());
				} else {
					result = -1;
				}

				break;
			case "update":
				alarmTables.clear();
				alarmTables.addAll((Collection<? extends BaseTable>) params[1]);

				result = 2;
				break;
			case "noData":
				alarmTables.clear();
				result = 3;
				break;
			}

			return result;
		}

		@Override
		protected void onPostExecute(Integer result) {

			switch (result) {
			case -1:
				Toast.makeText(SearchAlarmActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
				break;
			case 0:
				break;
			case 1:

				break;
			case 2:
				if (loadingDialog != null && loadingDialog.isShowing()) {
					loadingDialog.cancel();
				}
				adapter.notifyDataSetChanged();
				break;
			case 3:
				if (loadingDialog != null && loadingDialog.isShowing()) {
					loadingDialog.cancel();
				}
				adapter.notifyDataSetChanged();
				Toast.makeText(SearchAlarmActivity.this, "暂无有效数据", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}

		}

	}

	private class SearchAlarmTimer extends Timer {

		private QueryDataTask.QueryDataListener qdl = () -> new searchAlarmTask().execute("noData");

		public SearchAlarmTimer(final String tableName, final String contentId) {
			this.schedule(new QueryDataTask(this, "JNGA_JCJ_RESULT", qdl) {

				@Override
				public void doingTask() {
					String sqlStr = "from ( select * from " + tableName + " where flag_id = '" + contentId + "' ) "
							+ tableName;
					ArrayList<BaseTable> tables = DataOperation.queryTable(tableName, sqlStr, 1, 5);
					if (tables != null && tables.size() != 0) {
						SearchAlarmTimer.this.cancel();
						new searchAlarmTask().execute("update", tables);
					}

				}

			}, 2 * 1000, 2 * 1000);
		}
	}

}
