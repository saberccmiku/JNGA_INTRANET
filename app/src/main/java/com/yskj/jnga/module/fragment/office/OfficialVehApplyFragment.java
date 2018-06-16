package com.yskj.jnga.module.fragment.office;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.yskj.jnga.App;
import com.yskj.jnga.R;
import com.yskj.jnga.adapter.CommonAdapter;
import com.yskj.jnga.adapter.ViewHolder;
import com.yskj.jnga.module.activity.office.OfficialVehiclesActivity;
import com.yskj.jnga.network.xml.BaseTable;
import com.yskj.jnga.network.xml.DataOperation;
import com.yskj.jnga.utils.Utils;
import com.yskj.jnga.widget.Button;
import com.yskj.jnga.widget.CustomRadioGroup;
import com.yskj.jnga.widget.dropedittext.DropEditText;
import com.yskj.jnga.widget.picker.CustomDatePicker;

public class OfficialVehApplyFragment extends Fragment implements OnClickListener {

	private View view;
	private EditText et_driver, et_tel, et_unit, et_riders, et_to_add, et_reason, et_car;
	private TextView tv_start_time, tv_end_time;
	private LinearLayout ll_car;
	private DropEditText et_area;
	private CustomDatePicker startDatePicker, endDatePicker;
	private AlertDialog ad;
	private Button btn_confirm;
	private ProgressDialog pd;
	private OfficialVehiclesActivity ova;
	private String taskType;
	private BaseTable policeInfo;
	private ArrayList<BaseTable> selectCarList;
	private RadioGroup rg_select;

	public static OfficialVehApplyFragment newInstance() {
		return new OfficialVehApplyFragment();
	}

	@Override
	public void onAttach(Activity activity) {
		ova = (OfficialVehiclesActivity) activity;
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.fragment_official_veh_apply, null);
		findView();
		initData();
		return view;
	}

	private void initData() {

		policeInfo = App.getInstance().getSpUtil().getPoliceInfo();
		// 已选择车库信息
		selectCarList = new ArrayList<>();

	}

	private void findView() {
		// 用车单位
		LinearLayout include_unit = (LinearLayout) view.findViewById(R.id.include_unit);
		et_driver = (EditText) include_unit.findViewById(R.id.et_driver);
		et_tel = (EditText) include_unit.findViewById(R.id.et_tel);
		et_unit = (EditText) include_unit.findViewById(R.id.et_unit);
		et_riders = (EditText) include_unit.findViewById(R.id.et_riders);
		// 用车明细
		LinearLayout ovad = (LinearLayout) view.findViewById(R.id.include_ycmx);
		et_area = (DropEditText) ovad.findViewById(R.id.et_area);
		setDropEditListener(et_area);
		et_to_add = (EditText) ovad.findViewById(R.id.et_to_add);
		tv_start_time = (TextView) ovad.findViewById(R.id.tv_start_time);
		tv_start_time.setOnClickListener(this);
		startDatePicker = initDatePicker(tv_start_time);
		tv_end_time = (TextView) ovad.findViewById(R.id.tv_end_time);
		tv_end_time.setOnClickListener(this);
		endDatePicker = initDatePicker(tv_end_time);
		et_reason = (EditText) ovad.findViewById(R.id.et_reason);
		et_reason.setOnClickListener(this);

		rg_select = (RadioGroup) ovad.findViewById(R.id.rg_select);
		ll_car = (LinearLayout) ovad.findViewById(R.id.ll_car);
		et_car = (EditText) ovad.findViewById(R.id.et_car);
		et_car.setOnClickListener(this);
		rg_select.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				RadioButton rb = (RadioButton) group.findViewById(checkedId);
				if (rb.getText().equals("分局")) {
					ll_car.setVisibility(View.GONE);
				} else {
					ll_car.setVisibility(View.VISIBLE);
				}

			}
		});

		// 上传
		btn_confirm = (Button) view.findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);

	}

	private class OfficialVehApplyTask extends AsyncTask<String, Integer, Integer> {

		private ArrayList<BaseTable> carList;

		@Override
		protected void onPreExecute() {

			pd = new ProgressDialog(getActivity(), android.R.style.Theme_Holo_Dialog);
			pd.setCancelable(false);
			switch (taskType) {
			case "upload":
				pd.setMessage("上传中...");
				break;
			case "loadCar":
				pd.setMessage("加载车库...");
				break;
			default:
				break;
			}

			pd.show();
		}

		@Override
		protected Integer doInBackground(String... params) {

			int result = -1;
			switch (params[0]) {

			case "upload":

				if (policeInfo != null) {
					BaseTable table = new BaseTable();
					table.setTableName("JNGA_Car_UseTable_CKS");
					table.putField("ID", "");
					table.putField("PoliceNum", policeInfo.getField("RESULT_POLICE_NUM"));
					table.putField("PoliceName", policeInfo.getField("RESULT_POLICE_NAME"));
					table.putField("Punit", policeInfo.getField("RESULT_POLICE_UINT"));
					table.putField("CreateTime", Utils.getNetTimeByType("yyyy/MM/dd HH:mm:ss"));
					table.putField("PUnit_Use", et_unit.getText().toString());
					table.putField("Diver", et_driver.getText().toString());
					table.putField("Tel", et_tel.getText().toString());
					table.putField("Passengers", et_riders.getText().toString());
					table.putField("Reason", et_reason.getText().toString());
					table.putField("Area", et_area.getText().toString());
					table.putField("Address", et_to_add.getText().toString());
					table.putField("STime", Utils.getFormatTime(tv_start_time.getText().toString()).substring(0, 10));
					table.putField("Etime", Utils.getFormatTime(tv_end_time.getText().toString()).substring(0, 10));

					if (getRgValue(rg_select).equals("单位")) {
						table.putField("CID", et_car.getText().toString());// 选择的车牌号
					}

					DataOperation.insertOrUpdateTable(table);

					result = 1;
				} else {
					result = -1;
				}
				break;

			case "loadCar":
				String sqlCar = "from ( SELECT * FROM JJ_GWC WHERE SPARE2 ='空闲' and deptname = '"
						+ policeInfo.getField("RESULT_POLICE_UINT") + "' ) JJ_GWC";
				carList = DataOperation.queryTable("JJ_GWC", sqlCar);
				if (carList != null && carList.size() != 0) {
					result = 2;
				} else {
					result = 0;
				}
				break;

			default:
				break;
			}

			return result;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (pd != null && pd.isShowing()) {
				pd.cancel();
			}

			switch (result) {
			case -1:
				Toast.makeText(getActivity(), "操作失败", Toast.LENGTH_SHORT).show();
				break;
			case 0:
				Toast.makeText(getActivity(), "抱歉,没有可选的车辆或者数据异常", Toast.LENGTH_SHORT).show();
				break;
			case 1:
				ova.initWedgit(0);
				break;
			case 2:
				showCarDialog(et_car, carList, selectCarList);
				break;
			default:
				break;
			}
		}

	}

	private void setDropEditListener(DropEditText et) {
		et.setAdapter(new BaseAdapter() {

			@SuppressWarnings("serial")
			private List<String> list = new ArrayList<String>() {
				{
					add("本市");
					add("区外");
					add("区内");
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

	/**
	 * 名单列表
	 */
	public void showDialog(final EditText et) {
		ArrayList<String> reasons = new ArrayList<>();
		reasons.add("各类处突、安保任务及大型勤务活动");
		reasons.add("5人以上几种参加的会议、参观学习等活动");
		reasons.add("涉案人数众多的案件侦办");
		reasons.add("市外、区外办案，无配车单位室内办案");
		reasons.add("无配车单位到关押场所提审、押送");
		reasons.add("无配车单位下所、队开展指导、检查");
		reasons.add("日常勤务督察");
		reasons.add("机要工作");
		reasons.add("经分局领导批准的特殊情况(附报告)");
		View v_container = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_radio, null);
		ad = new AlertDialog.Builder(getActivity()).setView(v_container).show();
		TextView tv_title_01 = (TextView) v_container.findViewById(R.id.tv_title_01);
		tv_title_01.setText("用车事由");
		TextView tv_count = (TextView) v_container.findViewById(R.id.tv_count);
		tv_count.setText(String.valueOf(reasons.size()));
		final CustomRadioGroup rg = (CustomRadioGroup) v_container.findViewById(R.id.rg);

		for (String str : reasons) {
			RadioButton radioButton = new RadioButton(getActivity());
			RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,
					RadioGroup.LayoutParams.WRAP_CONTENT);
			layoutParams.setMargins(10, 10, 10, 10);
			radioButton.setLayoutParams(layoutParams);
			radioButton.setText(str);
			radioButton.setTextSize(12);
			// radioButton.setButtonDrawable(android.R.color.transparent);//隐藏单选圆形按钮
			radioButton.setGravity(Gravity.CENTER);
			radioButton.setPadding(10, 10, 10, 10);
			rg.addView(radioButton);// 将单选按钮添加到RadioGroup中
		}
		rg.setSingleColumn(false);
		rg.setColumnNumber(1);
		rg.setColumnHeight(getResources().getDimensionPixelSize(R.dimen.x100));

		ImageButton ib_close = (ImageButton) v_container.findViewById(R.id.ib_close);
		ib_close.setOnClickListener(this);

		ad.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				et.setText(getRgValue(rg));
				et.setTextColor(Color.parseColor("#FF0000"));
			}
		});

	}

	private String getRgValue(RadioGroup rg) {

		RadioButton rb = (RadioButton) rg.findViewById(rg.getCheckedRadioButtonId());

		return rb == null ? "" : rb.getText().toString();

	}

	@Override
	public void onDestroy() {

		if (ad != null && ad.isShowing()) {
			ad.cancel();

		}
		if (pd != null && pd.isShowing()) {
			pd.cancel();
		}

		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_confirm:

			if (TextUtils.isEmpty(et_driver.getText().toString())) {
				Toast.makeText(getActivity(), "请填写驾驶员姓名", Toast.LENGTH_SHORT).show();
			} else if (TextUtils.isEmpty(et_tel.getText().toString())) {
				Toast.makeText(getActivity(), "请填写驾驶员手机号码", Toast.LENGTH_SHORT).show();

			} else if (TextUtils.isEmpty(et_unit.getText().toString())) {
				Toast.makeText(getActivity(), "请填写驾驶员单位", Toast.LENGTH_SHORT).show();

			} else if (TextUtils.isEmpty(et_to_add.getText().toString())) {
				Toast.makeText(getActivity(), "请填写前往地点", Toast.LENGTH_SHORT).show();

			} else if (TextUtils.isEmpty(tv_start_time.getText().toString())) {
				Toast.makeText(getActivity(), "请填写用车起始时间", Toast.LENGTH_SHORT).show();

			} else if (TextUtils.isEmpty(tv_end_time.getText().toString())) {
				Toast.makeText(getActivity(), "请填写用车结束时间", Toast.LENGTH_SHORT).show();

			} else if (TextUtils.isEmpty(et_reason.getText().toString())) {
				Toast.makeText(getActivity(), "请填写用车事由", Toast.LENGTH_SHORT).show();
			} else if (TextUtils.isEmpty(getRgValue(rg_select))) {
				Toast.makeText(getActivity(), "请选择车属机构", Toast.LENGTH_SHORT).show();
			} else if (getRgValue(rg_select).equals("单位") && TextUtils.isEmpty(et_car.getText().toString())) {
				Toast.makeText(getActivity(), "请选择单位车辆", Toast.LENGTH_SHORT).show();
			} else if (!checkDate()) {
				Toast.makeText(getActivity(), "结束时间必须大于开始时间", Toast.LENGTH_SHORT).show();
			} else {

				if (Utils.isNetworkAvailable(getActivity())) {
					taskType = "upload";
					new OfficialVehApplyTask().execute("upload");
				}
			}

			break;

		case R.id.et_reason:
			showDialog(et_reason);
			break;

		case R.id.ib_close:
			ad.cancel();
			break;
		case R.id.tv_start_time:

			startDatePicker.show(tv_start_time.getText().toString());
			break;

		case R.id.tv_end_time:

			endDatePicker.show(tv_end_time.getText().toString());
			break;

		case R.id.et_car:

			if (Utils.isNetworkAvailable(getActivity())) {
				taskType = "loadCar";
				new OfficialVehApplyTask().execute("loadCar");
			}
			break;

		default:
			break;
		}

	}

	/**
	 * 车辆选择
	 */
	private void showCarDialog(final EditText et, final ArrayList<BaseTable> tables,
			final ArrayList<BaseTable> selectTables) {

		ArrayList<String> UnitList = new ArrayList<>();
		for (BaseTable baseTable : tables) {
			if (!UnitList.contains(baseTable.getField("DEPTNAME"))) {
				UnitList.add(baseTable.getField("DEPTNAME"));
			}
		}

		View v_container = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_window_blast, null);
		ad = new AlertDialog.Builder(getActivity()).setView(v_container).show();
		ImageButton ib_close = (ImageButton) v_container.findViewById(R.id.ib_close);
		ib_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (ad != null && ad.isShowing()) {
					ad.cancel();
				}

			}
		});
		ListView lv_window = (ListView) v_container.findViewById(R.id.lv_window);
		TextView tv_title_01 = (TextView) v_container.findViewById(R.id.tv_title_01);
		TextView tv_count = (TextView) v_container.findViewById(R.id.tv_count);
		tv_title_01.setText("可用车信息");
		tv_count.setText(String.valueOf(tables.size()));
		CommonAdapter<String> adapter = new CommonAdapter<String>(getActivity(), UnitList,R.layout.item_blast_people_list) {

			@Override
			public void convert(ViewHolder viewHolder, final String bean) {
				viewHolder.setText(R.id.tv_unit_name, bean);
				GridView gv_window = (GridView) viewHolder.getView(R.id.gv_window);
				final ArrayList<BaseTable> peopleTables = new ArrayList<>();
				for (BaseTable baseTable : tables) {
					if (baseTable.getField("DEPTNAME").equals(bean)) {
						peopleTables.add(baseTable);
					}
				}
				CommonAdapter<BaseTable> peopleAdapter = new CommonAdapter<BaseTable>(getActivity(), peopleTables,R.layout.item_window_feedback) {

					@Override
					public void convert(ViewHolder viewHolder, BaseTable table) {
						LinearLayout ll_window_item = viewHolder.getView(R.id.ll_window_item);
						GradientDrawable gd = (GradientDrawable) ll_window_item.getBackground();
						if (!selectTables.contains(table)) {
							gd.setColor(Color.parseColor("#f3f4f5"));// 蓝色
						} else {
							gd.setColor(Color.parseColor("#42a5bf"));
						}
						viewHolder.setText(R.id.tv_01, table.getField("GWCNO"));
						TextView tv_01 = viewHolder.getView(R.id.tv_01);
						tv_01.setTextColor(Color.parseColor("#999999"));

					}
				};
				gv_window.setAdapter(peopleAdapter);
				gv_window.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

						GradientDrawable gd = (GradientDrawable) view.getBackground();
						if (!selectTables.contains(peopleTables.get(position))) {
							selectTables.add(peopleTables.get(position));
							gd.setColor(Color.parseColor("#42a5bf"));
						} else {
							selectTables.remove(peopleTables.get(position));
							gd.setColor(Color.parseColor("#f3f4f5"));
						}

					}
				});
			}
		};

		lv_window.setAdapter(adapter);

		ad.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {

				saveSelectData(et, selectTables);

			}
		});

	}

	/**
	 * 保存已选择的车辆信息
	 * 
	 * @param et
	 *            展示车辆车偏好的EditText
	 * @param selectTables
	 *            所选择的车辆的集合
	 */
	private void saveSelectData(EditText et, ArrayList<BaseTable> selectTables) {
		StringBuffer sb = new StringBuffer();
		for (BaseTable people : selectTables) {
			sb.append(people.getField("GWCNO"));
			sb.append(",");

		}
		et.setText(Html.fromHtml("<font color='#FF34B3'>" + sb.toString()));
	}

	private boolean checkDate() {
		String startTime = tv_start_time.getText().toString();
		String endTime = tv_end_time.getText().toString();
		if (!TextUtils.isEmpty(startTime) && !TextUtils.isEmpty(startTime)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			try {
				if (sdf.parse(startTime).before(sdf.parse(endTime))) {
					return true;
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}

		}
		return false;
	}

}
