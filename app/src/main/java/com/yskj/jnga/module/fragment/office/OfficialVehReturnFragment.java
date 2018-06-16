package com.yskj.jnga.module.fragment.office;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import com.lidroid.xutils.exception.DbException;
import com.yskj.jnga.App;
import com.yskj.jnga.R;
import com.yskj.jnga.entity.OfficialVehForm;
import com.yskj.jnga.module.activity.office.OfficialVehiclesActivity;
import com.yskj.jnga.network.xml.BaseTable;
import com.yskj.jnga.network.xml.DataOperation;
import com.yskj.jnga.task.QueryDataTask;
import com.yskj.jnga.utils.BeanRefUtil;
import com.yskj.jnga.utils.Utils;
import com.yskj.jnga.widget.Button;
import com.yskj.jnga.widget.dropedittext.DropEditText;
import com.yskj.jnga.widget.picker.CustomDatePicker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class OfficialVehReturnFragment extends Fragment implements OnClickListener {

	private OfficialVehiclesActivity ova;
	private View view;
	private TextView tv_driver, tv_applicant, tv_tel, tv_unit, tv_people, tv_to_add, tv_area, tv_reason, tv_time,
			tv_h_time, tv_j_time;
	public EditText et_num, et_name;
	private DropEditText et_car_state;
	private Button btn_confirm;
	private CustomDatePicker hDatePicker, jDatePicker;
	public String type; // 1.上传 2检索
	public ProgressDialog pd;
	private OfficialVehForm form;
	private BaseTable peopleInfo;

	public static OfficialVehReturnFragment newInstance() {

		return new OfficialVehReturnFragment();

	}

	@Override
	public void onAttach(Activity activity) {
		ova = (OfficialVehiclesActivity) activity;
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_official_veh_return, null);
		findView();
		initWidget();
		return view;
	}

	private void findView() {

		// 用车单位
		LinearLayout include_ycdw = (LinearLayout) view.findViewById(R.id.include_ycdw);
		tv_driver = (TextView) include_ycdw.findViewById(R.id.tv_driver);
		tv_applicant = (TextView) include_ycdw.findViewById(R.id.tv_applicant);
		tv_tel = (TextView) include_ycdw.findViewById(R.id.tv_tel);
		tv_unit = (TextView) include_ycdw.findViewById(R.id.tv_unit);
		tv_people = (TextView) include_ycdw.findViewById(R.id.tv_people);
		// 申请明细
		LinearLayout include_sqmx = (LinearLayout) view.findViewById(R.id.include_sqmx);
		tv_area = (TextView) include_sqmx.findViewById(R.id.tv_area);
		tv_to_add = (TextView) include_sqmx.findViewById(R.id.tv_to_add);
		tv_reason = (TextView) include_sqmx.findViewById(R.id.tv_reason);
		tv_time = (TextView) include_sqmx.findViewById(R.id.tv_time);

		// 接车明细
		et_num = (EditText) view.findViewById(R.id.et_num);
		et_name = (EditText) view.findViewById(R.id.et_name);
		tv_h_time = (TextView) view.findViewById(R.id.tv_h_time);
		tv_h_time.setOnClickListener(this);
		hDatePicker = initDatePicker(tv_h_time);
		tv_j_time = (TextView) view.findViewById(R.id.tv_j_time);
		tv_j_time.setOnClickListener(this);
		jDatePicker = initDatePicker(tv_j_time);
		et_car_state = (DropEditText) view.findViewById(R.id.et_car_state);
		setDropEditListener(et_car_state);

		btn_confirm = (Button) view.findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);

	}

	private void initWidget() {
		peopleInfo = App.getInstance().getSpUtil().getPoliceInfo();
		form = ova.getForm();
		tv_driver.setText(form.getDiver());
		tv_applicant.setText(form.getPoliceName());
		tv_tel.setText(form.getTel());
		tv_unit.setText(form.getPUnit_Use());
		tv_people.setText(form.getPassengers());
		tv_to_add.setText(form.getAddress());
		tv_area.setText(form.getArea());
		tv_reason.setText(form.getReason());
		tv_time.setText(form.getSTime() + "至" + form.getEtime());

	}

	private void setDropEditListener(DropEditText et) {

		et.setAdapter(new BaseAdapter() {

			@SuppressWarnings("serial")
			List<String> list = new ArrayList<String>() {
				{
					add("清洁");
					add("保养");
					add("加满油");
				}
			};

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView tv = new TextView(getActivity());
				tv.setText(list.get(position));
				return tv;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public Object getItem(int position) {

				return list.get(position);
			}

			@Override
			public int getCount() {

				return list.size();
			}
		});

	}

	private CustomDatePicker initDatePicker(final TextView tv) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, +30);
		date = calendar.getTime();
		String now = sdf.format(date);
		// tv.setText(now.split(" ")[0]);
		tv.setText(Utils.getNowTime("yyyy-MM-dd HH:mm"));

		CustomDatePicker customDatePicker = new CustomDatePicker(getActivity(), new CustomDatePicker.ResultHandler() {
			@Override
			public void handle(String time) { // 回调接口，获得选中的时间
				tv.setText(time.substring(0, 16));
			}
		}, Utils.getNowTime("yyyy-MM-dd HH:mm:ss"), now); // 初始化日期格式请用：yyyy-MM-dd
															// HH:mm，否则不能正常运行
		customDatePicker.showSpecificTime(true); // 显示时分
		customDatePicker.setIsLoop(false); // 不允许循环滚动

		return customDatePicker;

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_h_time:
			hDatePicker.show(tv_h_time.getText().toString());
			break;

		case R.id.tv_j_time:
			jDatePicker.show(tv_j_time.getText().toString());
			break;

		case R.id.btn_confirm:
			if (TextUtils.isEmpty(et_num.getText().toString())) {
				Toast.makeText(getActivity(), "请填写换车人警号", Toast.LENGTH_SHORT).show();
			} else if (TextUtils.isEmpty(et_name.getText().toString())) {
				Toast.makeText(getActivity(), "请填写换车人姓名", Toast.LENGTH_SHORT).show();
			} else if (TextUtils.isEmpty(et_car_state.getText().toString())) {
				Toast.makeText(getActivity(), "请填写车况", Toast.LENGTH_SHORT).show();
			} else {
				if (Utils.isNetworkAvailable(getActivity())) {
					type = "police";
					new OfficialVehReturnTask().execute("police");
				}
			}
			break;

		default:
			break;
		}

	}

	public class OfficialVehReturnTask extends AsyncTask<String, Integer, Integer> {

		private BaseTable policeInfo, vehCks;

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(getActivity(), android.R.style.Theme_Holo_Dialog);
			switch (type) {
			case "police":
				pd.setMessage("校验信息中...");
				break;
			case "check":
				pd.setMessage("复核信息中...");
				break;
			case "upload":
				pd.setMessage("上传表单中...");
				break;
			default:
				break;
			}

			pd.setCanceledOnTouchOutside(false);
			pd.show();
		}

		private int result = 0;

		@Override
		protected Integer doInBackground(String... params) {
			switch (params[0]) {
			case "police":

				policeInfo = new BaseTable();
				policeInfo.setTableName("XNGA_POLICE_REQ");
				policeInfo.putField("RESULT_POLICE_NUM", et_num.getText().toString());
				if (DataOperation.insertOrUpdateTable(policeInfo)) {
					result = 1;
				} else {
					result = 0;
				}

				break;
			case "upload":

				vehCks = BeanRefUtil.getDbBaeTable(form);
				vehCks.setTableName("JNGA_Car_UseTable_CKS");
				vehCks.putField("Car_H_PoliceName", et_name.getText().toString());
				vehCks.putField("Car_H_PoliceNum", et_num.getText().toString());
				vehCks.putField("Car_HTime", Utils.getFormatTime(tv_h_time.getText().toString()));
				vehCks.putField("Car_J_PoliceName", peopleInfo.getField("RESULT_POLICE_NAME"));
				vehCks.putField("Car_J_PoliceNum", peopleInfo.getField("RESULT_POLICE_NUM"));
				vehCks.putField("Car_JTime", Utils.getFormatTime(tv_j_time.getText().toString()));
				vehCks.putField("HState_Car", et_car_state.getText().toString());
				if (DataOperation.insertOrUpdateTable(vehCks)) {
					result = 2;
				} else {
					result = -1;
				}

				break;
			case "check":

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
				Toast.makeText(getActivity(), "操作失败", Toast.LENGTH_SHORT).show();
				break;
			case 0:
				if (pd != null && pd.isShowing()) {
					pd.cancel();
				}
				break;
			case 1:
				pd.setMessage("复核信息中...");
				type = "check";
				final Timer timer = new Timer();
				QueryDataTask.QueryDataListener qdl = new QueryDataTask.QueryDataListener() {

					@Override
					public void onCompleted() {

						ova.mHandler.sendEmptyMessage(2);

					}
				};
				timer.schedule(new QueryDataTask(timer, "XNGA_POLICE_RESULT", qdl) {

					@Override
					public void doingTask() {

						String sqlStr = "from ( select * from XNGA_POLICE_RESULT where FLAG_ID ='"
								+ policeInfo.getContentId() + "' ) XNGA_POLICE_RESULT";
						ArrayList<BaseTable> policeInfos = DataOperation.queryTable("XNGA_POLICE_RESULT", sqlStr);
						if (policeInfos != null && policeInfos.size() != 0) {
							timer.cancel();
							Message msg = ova.mHandler.obtainMessage();
							msg.what = 1;
							msg.obj = policeInfos.get(0);
							ova.mHandler.sendMessage(msg);
						}

					}
				}, 0, 2 * 1000);

				break;
			case 2:
				if (pd != null && pd.isShowing()) {
					pd.cancel();
				}
				try {
					App.sDbUtils.deleteById(OfficialVehForm.class, form.getId()); // 删除本地缓存
					ova.initWedgit(1);
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;

			default:
				break;
			}
		}

	}

	/**
	 * 提交表单到服务器
	 */

	public void executeTask(String type) {
		new OfficialVehReturnTask().execute(type);
	}

}
