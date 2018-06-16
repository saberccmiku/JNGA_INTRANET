package com.yskj.jnga.module.fragment.office;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.yskj.jnga.App;
import com.yskj.jnga.R;
import com.yskj.jnga.adapter.CommonAdapter;
import com.yskj.jnga.adapter.ViewHolder;
import com.yskj.jnga.entity.OfficialVehForm;
import com.yskj.jnga.module.activity.office.OfficialVehiclesActivity;
import com.yskj.jnga.network.xml.BaseTable;
import com.yskj.jnga.network.xml.DataOperation;
import com.yskj.jnga.task.QueryDataTask;
import com.yskj.jnga.utils.BeanRefUtil;

import android.app.Activity;
import android.app.ProgressDialog;
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

public class OfficialVehListFragment extends Fragment {

	private View view;
	private ArrayList<OfficialVehForm> forms;
	private OfficialVehiclesActivity ova;
	public ProgressDialog pd;

	public static OfficialVehListFragment newInstance() {
		return new OfficialVehListFragment();
	}

	@Override
	public void onAttach(Activity activity) {
		ova = (OfficialVehiclesActivity) activity;
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_demo_list, null);
		findView();
		return view;
	}

	@Override
	public void onStart() {
		// 加载本地数据
		if (ova.status == 1) {
			new OfficialVehListTask().execute("1");
		} else if (ova.status == 3) {
			new OfficialVehListTask().execute("3");
		} else if (ova.status == 5) {
			new OfficialVehListTask().execute("5");
		}

		super.onStart();
	}

	public void initWedgit() {

		ListView listView = (ListView) view.findViewById(R.id.listView);

		CommonAdapter<OfficialVehForm> adapter = new CommonAdapter<OfficialVehForm>(getActivity(), forms,R.layout.item_official_veh_form) {


			@Override
			public void convert(ViewHolder viewHolder, OfficialVehForm form) {

				try {
					String date = form.getCreateTime().substring(0, form.getCreateTime().indexOf(" "));
					// 算两个日期间隔多少天
					SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
					int day = (int) ((new Date().getTime() - format.parse(date).getTime()) / (1000 * 3600 * 24));
					StringBuffer sb = new StringBuffer();
					sb.append("\t\t\t\t");
					sb.append(date);
					sb.append(form.getPUnit_Use());
					sb.append("的");
					sb.append(form.getPoliceName());
					if (ova.status == 1) {
						sb.append("同志发来一份关于公车申请的表单，望领导审批。");
						viewHolder.setText(R.id.tv_type, "关于公务车申请审批");
					} else if (ova.status == 3) {
						sb.append("同志还有未归还的车辆，请留意更新。");
						viewHolder.setText(R.id.tv_type, "关于公务车接还事项");
					} else if (ova.status == 5) {
						sb.append("同志的公车申请表单处理概况。");
						viewHolder.setText(R.id.tv_type, "关于公务车审批进度");
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
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ova.setForm(forms.get(position));
				if (ova.status == 1) {
					ova.initWedgit(3);
				} else if (ova.status == 3) {
					ova.initWedgit(4);
				} else if (ova.status == 5) {
					ova.initWedgit(5);
				}

			}
		});

	}

	private void findView() {

	}

	private class OfficialVehListTask extends AsyncTask<String, Integer, Integer> {

		private String error;
		private BaseTable progressTable;

		@Override
		protected void onPreExecute() {
			if (ova.status == 5) {
				pd = new ProgressDialog(getActivity(), android.R.style.Theme_Holo_Dialog);
				pd.setCanceledOnTouchOutside(false);
				pd.setMessage("正在加载数据");
				pd.show();
			}
		}

		@Override
		protected Integer doInBackground(String... params) {
			int result = 0;
			try {

				switch (params[0]) {
				case "1":

					List<OfficialVehForm> OfficialVehForm2 = App.sDbUtils.findAll(Selector
							.from(OfficialVehForm.class).where("BaoGao", "=", "1").orderBy("CreateTime", true));
					forms = (ArrayList<OfficialVehForm>) OfficialVehForm2;

					if (forms != null) {
						result = 1;
					} else {
						result = 0;
					}

					break;
				case "3":

					List<OfficialVehForm> OfficialVehForm1 = App.sDbUtils.findAll(Selector
							.from(OfficialVehForm.class).where("BaoGao", "=", "2").orderBy("CreateTime", true));
					forms = (ArrayList<OfficialVehForm>) OfficialVehForm1;

					if (forms != null) {
						result = 1;
					} else {
						result = 0;
					}

					break;
				case "5":
					progressTable = new BaseTable();
					progressTable.setTableName("JNGA_Car_UseTable_QUERY");
					progressTable.putField("Reason", "3");
					progressTable.putField("PoliceNum", App.getInstance().getSpUtil().getAccount());
					if (DataOperation.insertOrUpdateTable(progressTable)) {
						result = 2;
					} else {
						result = -1;
					}

					break;
				default:
					break;
				}

			} catch (DbException e) {
				e.printStackTrace();
				error = e.getMessage();
				result = -1;
			}

			return result;

		}

		@Override
		protected void onPostExecute(Integer result) {

			switch (result) {
			case -1:
				if (ova.status == 5 && pd != null && pd.isShowing()) {
					pd.cancel();
				}
				Toast.makeText(getActivity(), "加载数据异常:" + error, Toast.LENGTH_SHORT).show();
				break;
			case 0:
				Toast.makeText(getActivity(), "暂无数据", Toast.LENGTH_SHORT).show();
				break;
			case 1:
				initWedgit();
				break;

			case 2:
				final Timer timer = new Timer();
				QueryDataTask.QueryDataListener qdl = new QueryDataTask.QueryDataListener() {

					@Override
					public void onCompleted() {
						ova.mHandler.sendEmptyMessage(4);
					}
				};
				timer.schedule(new QueryDataTask(timer, "JNGA_Car_UseTable_RESULT", qdl) {

					@Override
					public void doingTask() {
						String sqlStr = "from ( SELECT * FROM JNGA_Car_UseTable_RESULT WHERE FLAG_ID = '"
								+ progressTable.getContentId() + "' ) JNGA_Car_UseTable_RESULT";
						ArrayList<BaseTable> tables = DataOperation.queryTable("JNGA_Car_UseTable_RESULT", sqlStr);

						if (tables != null && tables.size() != 0) {
							timer.cancel();
							for (BaseTable baseTable : tables) {
								OfficialVehForm form = (OfficialVehForm) BeanRefUtil.setDbValue(new OfficialVehForm(),
										baseTable);
								if (forms == null) {
									forms = new ArrayList<>();
									forms.add(form);
								} else {
									forms.clear();
									forms.add(form);
								}

							}
							ova.mHandler.sendEmptyMessage(3);
						}
					}
				}, 2 * 1000, 2 * 1000);

				break;

			default:
				break;
			}
		}

	}

	@Override
	public void onDestroy() {
		if (pd != null && pd.isShowing()) {
			pd.cancel();
		}
		super.onDestroy();
	}

}
