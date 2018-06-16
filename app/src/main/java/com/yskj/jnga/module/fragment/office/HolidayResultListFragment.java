package com.yskj.jnga.module.fragment.office;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.yskj.jnga.App;
import com.yskj.jnga.R;
import com.yskj.jnga.adapter.CommonAdapter;
import com.yskj.jnga.adapter.ViewHolder;
import com.yskj.jnga.module.activity.office.HolidaysProgressActivity;
import com.yskj.jnga.module.activity.office.HolidaysActivity;
import com.yskj.jnga.module.activity.office.LeaveApprovalActivity;
import com.yskj.jnga.network.xml.BaseTable;
import com.yskj.jnga.network.xml.DataOperation;
import com.yskj.jnga.task.QueryDataTask;
import com.yskj.jnga.utils.Utils;

public class HolidayResultListFragment extends Fragment {

	public static Context c;
	private HolidaysActivity mActivity;
	private View mContentView;
	private ListView listView;
	private ArrayList<BaseTable> tables = null;
	public ProgressDialog pd;

	public static HolidayResultListFragment newInstance(Context c) {
		HolidayResultListFragment.c = c;
		HolidayResultListFragment fragment = new HolidayResultListFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.fragment_demo_list, null);
		mActivity = (HolidaysActivity) getActivity();

		findView();

		return mContentView;
	}

	@Override
	public void onStart() {
		super.onStart();
		if (Utils.isNetworkAvailable(mActivity)) {
			if (mActivity.status == 1) {
				new MyTask().execute("1");
			} else if (mActivity.status == 5) {
				new MyTask().execute("5");
			}
		}

	}

	private void findView() {
		mActivity.img.setImageResource(R.drawable.shjm_title);

		listView = (ListView) mContentView.findViewById(R.id.listView);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				if (mActivity.status == 1) {
					Intent intent = new Intent(mActivity, LeaveApprovalActivity.class);
					intent.putExtra("HolidayResultList", position);
					startActivity(intent);
				} else if (mActivity.status == 5) {
					Intent intent = new Intent(mActivity, HolidaysProgressActivity.class);
					intent.putExtra("HolidayResultList",  tables.get(position));
					startActivity(intent);
				}

			}
		});

	}

	public class MyTask extends AsyncTask<String, Integer, Integer> {

		private BaseTable myTable;

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(mActivity);
			pd.setCanceledOnTouchOutside(false);
			pd.setMessage("加载数据中...");
			pd.show();

		}

		@Override
		protected Integer doInBackground(String... params) {
			int result = 0;
			switch (params[0]) {
			case "1":
				tables = mActivity.mSpUtil.getHolidayForm();
				result = 1;
				break;
			case "5":
				myTable = new BaseTable();
				myTable.setTableName("JNGA_QJ_INFO_QUERY");
				myTable.putField("QJ_PoliceNum", App.getInstance().getSpUtil().getAccount());
				myTable.putField("QJ_JType", "1");
				try {
					if (DataOperation.insertOrUpdateTable(myTable)) {
						result = 2;
					} else {
						result = -1;
					}
				} catch (Exception e) {
					result = -1;
					e.printStackTrace();
				}

				break;

			default:
				break;
			}
			return result;
		}

		@Override
		protected void onPostExecute(Integer result) {

			switch (result) {
			case -1:
				if (pd != null && pd.isShowing()) {
					pd.cancel();
				}
				Toast.makeText(mActivity, "操作异常", Toast.LENGTH_SHORT).show();
				break;
			case 0:
				if (pd != null && pd.isShowing()) {
					pd.cancel();
				}
				break;
			case 1:
				if (pd != null && pd.isShowing()) {
					pd.cancel();
				}
				initWedgit();
				break;
			case 2:

				final Timer timer = new Timer();
				QueryDataTask.QueryDataListener qdl = () -> mActivity.mHandler.sendEmptyMessage(4);
				timer.schedule(new QueryDataTask(timer, "JNGA_Car_UseTable_RESULT", qdl) {

					@Override
					public void doingTask() {
						String sqlStr = "from ( SELECT * FROM JNGA_QJ_INFO_RESULT WHERE FLAG_ID = '"
								+ myTable.getContentId() + "' ) JNGA_QJ_INFO_RESULT";
						try {
							tables = DataOperation.queryTable("JNGA_QJ_INFO_RESULT",sqlStr);
							if (tables != null && tables.size() != 0) {
								timer.cancel();
								mActivity.mHandler.sendEmptyMessage(3);
							}
						} catch (Exception e) {
							mActivity.mHandler.sendEmptyMessage(2);
							e.printStackTrace();
						}

					}
				}, 2 * 1000, 2 * 1000);

				break;

			default:
				break;
			}

		}

	}

	public void initWedgit() {

		if (tables != null) {
			CommonAdapter<BaseTable> adapter = new CommonAdapter<BaseTable>(mActivity, tables,R.layout.item_holiday_result) {

				@Override
				public void convert(ViewHolder viewHolder, BaseTable bean) {
					try {
						String date = bean.getField("QJ_CreateTime").substring(0,
								bean.getField("QJ_CreateTime").indexOf(" "));
						// 算两个日期间隔多少天
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
						int day = (int) ((new Date().getTime() - format.parse(date).getTime()) / (1000 * 3600 * 24));
						StringBuffer sb = new StringBuffer();
						sb.append("\t\t\t\t");
						sb.append(date);
						sb.append(bean.getField("QJ_Punit"));
						sb.append("的");
						sb.append(bean.getField("QJ_PoliceName"));
						if (mActivity.status == 1) {
							sb.append("同志发来一份关于请假申请的表单，望领导审批。");
							viewHolder.setText(R.id.tv_type, "关于请假申请审批" + "(" + bean.getField("QJ_JType") + ")");
						} else if (mActivity.status == 4) {
							sb.append("同志还未销假，请留意更新。");
							viewHolder.setText(R.id.tv_type, "关于销假事项");
						} else if (mActivity.status == 5) {
							sb.append("同志的请假申请表单处理概况。");
							viewHolder.setText(R.id.tv_type, "关于个人请假审批进度");
						}
						viewHolder.setText(R.id.tv_content, sb.toString());
						String interval;
						if (day == 0) {
							interval = "今天";
						} else {
							interval = day + "天前";
						}
						viewHolder.setText(R.id.tv_date, interval);

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			};

			listView.setAdapter(adapter);
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (pd != null && pd.isShowing()) {
			pd.cancel();
		}
	}

}
