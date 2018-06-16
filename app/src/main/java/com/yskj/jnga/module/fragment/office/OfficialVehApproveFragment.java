package com.yskj.jnga.module.fragment.office;

import java.util.ArrayList;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.exception.DbException;
import com.yskj.jnga.App;
import com.yskj.jnga.R;
import com.yskj.jnga.adapter.CommonAdapter;
import com.yskj.jnga.adapter.ViewHolder;
import com.yskj.jnga.entity.ApprovalInfo;
import com.yskj.jnga.entity.OfficialVehForm;
import com.yskj.jnga.module.activity.office.OfficialVehiclesActivity;
import com.yskj.jnga.network.xml.BaseTable;
import com.yskj.jnga.network.xml.DataOperation;
import com.yskj.jnga.utils.BeanRefUtil;
import com.yskj.jnga.utils.Utils;
import com.yskj.jnga.widget.AutoGridView;
import com.yskj.jnga.widget.Button;

public class OfficialVehApproveFragment extends Fragment {

	private OfficialVehiclesActivity ova;
	private View view;
	private TextView tv_driver, tv_applicant, tv_tel, tv_unit, tv_people, tv_to_add, tv_reason, tv_time;
	private ListView lv;
	private RadioGroup rb_spjg_agree;
	private EditText et_spyj;
	private OfficialVehForm form;
	private ProgressDialog pd;
	private BaseTable peopleInfo;
	private LinearLayout inclede_wdsp, ll_select_car;
	private AlertDialog ad;
	private ArrayList<BaseTable> selectCarList;
	private EditText et_car_list;
	private String taskType;// 当前线程任务类别 >1.loadCar:加载车库信息 2.uploadForm:上传审批表单
	private String unit;// 用户所在单位

	public static OfficialVehApproveFragment newInstance() {

		return new OfficialVehApproveFragment();
	}

	@Override
	public void onAttach(Activity activity) {
		ova = (OfficialVehiclesActivity) activity;
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_official_veh_approve, null);
		findView();
		initData();
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
		tv_to_add = (TextView) include_sqmx.findViewById(R.id.tv_to_add);
		tv_reason = (TextView) include_sqmx.findViewById(R.id.tv_reason);
		tv_time = (TextView) include_sqmx.findViewById(R.id.tv_time);
		// 审批进度
		LinearLayout include_splc = (LinearLayout) view.findViewById(R.id.include_splc);
		lv = (ListView) include_splc.findViewById(R.id.lv);
		// 我的审批
		inclede_wdsp = (LinearLayout) view.findViewById(R.id.inclede_wdsp);
		et_spyj = (EditText) inclede_wdsp.findViewById(R.id.et_spyj);
		et_spyj.setText("拟同意。");
		ll_select_car = (LinearLayout) inclede_wdsp.findViewById(R.id.ll_select_car);// select
		ll_select_car.setVisibility(View.VISIBLE); // car
		rb_spjg_agree = (RadioGroup) inclede_wdsp.findViewById(R.id.rg_spjg);
		et_car_list = (EditText) inclede_wdsp.findViewById(R.id.et_car_list);

		// 控件的监听事件
		setListener();

	}

	private void setListener() {
		// 我的审批
		rb_spjg_agree.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				RadioButton rb = (RadioButton) group.findViewById(checkedId);
				switch (rb.getText().toString()) {
				case "同意":
					if (peopleInfo.getField("RESULT_POLICE_UINT").equals("办公室")) {
						ll_select_car.setVisibility(View.VISIBLE);
					}
					et_spyj.setText("拟同意.");
					break;
				case "不同意":
					if (peopleInfo.getField("RESULT_POLICE_UINT").equals("办公室")) {
						ll_select_car.setVisibility(View.GONE);
					}
					et_spyj.setText("驳回.");
					break;
				default:
					break;
				}

			}
		});

		// 选择车辆
		et_car_list.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Utils.isNetworkAvailable(getActivity())) {
					taskType = "loadCar";
					new OfficialVehApproveTask().execute("loadCar");
				} else {
					Toast.makeText(getActivity(), "网络异常", Toast.LENGTH_SHORT).show();
				}

			}
		});


		// 提交按钮
		Button btn_confirm = (Button) view.findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (getRgValue(rb_spjg_agree).equals("同意") && TextUtils.isEmpty(et_car_list.getText().toString())
						&& unit.equals("办公室") && form.getState_SP1().equals("同意")) {
					// 当办公室同意但是没有指派车辆则不允许上传
					Toast.makeText(getActivity(), "请指派车辆", Toast.LENGTH_SHORT).show();

				}  else {
					if (Utils.isNetworkAvailable(getActivity())) {
						taskType = "uploadForm";
						new OfficialVehApproveTask().execute("uploadForm");
					} else {
						Toast.makeText(getActivity(), "网络异常", Toast.LENGTH_SHORT).show();
					}
				}

			}
		});

	}

	private void initData() {
		// 已选择车库信息
		selectCarList = new ArrayList<>();
		// 用户信息
		peopleInfo = App.getInstance().getSpUtil().getPoliceInfo();
		unit = peopleInfo.getField("RESULT_POLICE_UINT");
		if (!unit.equals("办公室")) {// 非办公室用户隐藏指派车辆功能
			ll_select_car.setVisibility(View.GONE);
		}
		form = ova.getForm();
		tv_driver.setText(form.getDiver());
		tv_applicant.setText(form.getPoliceName());
		tv_tel.setText(form.getTel());
		tv_unit.setText(form.getPUnit_Use());
		tv_people.setText(form.getPassengers());
		tv_to_add.setText(form.getAddress());
		tv_reason.setText(form.getReason());
		tv_time.setText("从" + getSimpleDate(form.getSTime()) + "至" + getSimpleDate(form.getEtime()));
		// 审批进度
		ArrayList<ApprovalInfo> approvalInfos = new ArrayList<>();
		// 本单位
		ApprovalInfo approvalInfo1 = new ApprovalInfo();
		approvalInfo1.setId(1);
		approvalInfo1.setLevel("本单位");
		approvalInfo1.setHandlerName(form.getPoliceName_SP1());
		approvalInfo1.setOpinion(form.getYJ_SP1());
		approvalInfo1.setResult(form.getState_SP1());
		approvalInfo1.setApprovalTime(getSimpleDate(form.getDate_SP1()));
		approvalInfos.add(approvalInfo1);
		// 办公室
		ApprovalInfo approvalInfo2 = new ApprovalInfo();
		approvalInfo2.setId(2);
		approvalInfo2.setLevel("办公室");
		approvalInfo2.setHandlerName(form.getPoliceName_SP2());
		approvalInfo2.setOpinion(form.getYJ_SP2());
		approvalInfo2.setResult(form.getState_SP2());
		approvalInfo2.setApprovalTime(getSimpleDate(form.getDate_SP2()));
		approvalInfos.add(approvalInfo2);
		// 分局领导
		ApprovalInfo approvalInfo3 = new ApprovalInfo();
		approvalInfo3.setId(3);
		approvalInfo3.setLevel("分局领导");
		approvalInfo3.setHandlerName(form.getPoliceName_SP3());
		approvalInfo3.setOpinion(form.getYJ_SP3());
		approvalInfo3.setResult(form.getState_SP3());
		approvalInfo3.setApprovalTime(getSimpleDate(form.getDate_SP3()));
		approvalInfos.add(approvalInfo3);
		CommonAdapter<ApprovalInfo> adapter = new CommonAdapter<ApprovalInfo>(getActivity(), approvalInfos,R.layout.item_approval) {

			@Override
			public void convert(ViewHolder viewHolder, ApprovalInfo approvalInfo) {

				viewHolder.setText(R.id.tv_id, String.valueOf(approvalInfo.getId()));
				viewHolder.setText(R.id.tv_level, approvalInfo.getLevel());
				viewHolder.setText(R.id.tv_handler_name, approvalInfo.getHandlerName());
				viewHolder.setText(R.id.tv_opinion, approvalInfo.getOpinion());
				viewHolder.setText(R.id.tv_result, approvalInfo.getResult());
				viewHolder.setText(R.id.tv_approval_time, approvalInfo.getApprovalTime());

			}
		};

		lv.setAdapter(adapter);
	}

	/**
	 * 格式化时间
	 * 
	 * @param date
	 *            得到的时间字符串
	 * @return 截取后的日期
	 */
	public String getSimpleDate(String date) {
		return TextUtils.isEmpty(date) ? "" : date.substring(0, date.indexOf(" "));
	}

	/**
	 * OfficialVehApproveFragment的内部线程任务
	 * 
	 * @author saber
	 *
	 */

	private class OfficialVehApproveTask extends AsyncTask<String, Integer, Integer> {

		private ArrayList<BaseTable> carList, leaderList;

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(getActivity(), android.R.style.Theme_Holo_Dialog);
			pd.setCanceledOnTouchOutside(false);
			switch (taskType) {
			case "loadCar":
				pd.setMessage("加载车库信息...");
				break;
			case "uploadForm":
				pd.setMessage("上传审批表单...");
				break;
			case "loadLeader":
				pd.setMessage("加载名单中...");
				break;
			default:
				break;
			}

			pd.show();
		}

		@Override
		protected Integer doInBackground(String... params) {
			int result = 0;

			switch (params[0]) {
			case "loadCar":
				String sqlCar = "from ( SELECT * FROM JJ_GWC WHERE SPARE2 ='空闲' and deptname ='办公室' ) JJ_GWC";
				carList = DataOperation.queryTable("JJ_GWC", sqlCar);
				if (carList != null && carList.size() != 0) {
					result = 2;
				} else {
					result = 0;
				}
				break;
			case "loadLeader":
				String sqlLeader = "from ( SELECT * FROM JNGA_ROLE WHERE PUNIT = '1') JNGA_ROLE";
				leaderList = DataOperation.queryTable("JNGA_ROLE", sqlLeader);
				if (leaderList != null && leaderList.size() != 0) {
					result = 3;
				} else {
					result = 0;
				}
				break;
			case "uploadForm":
				if (peopleInfo != null) {

					BaseTable table = BeanRefUtil.getDbBaeTable(form);
					String num = "", name = "", sate = "", yj = "", date = "";
					if (TextUtils.isEmpty(form.getState_SP1()) && TextUtils.isEmpty(form.getState_SP2())
							&& TextUtils.isEmpty(form.getState_SP3())) {// 判断是不是本单位,可能包含办公室本地位的一级审批
						num = "PoliceNum_SP1";
						name = "PoliceName_SP1";
						sate = "State_SP1";
						yj = "YJ_SP1";
						date = "Date_SP1";
					} else if (!TextUtils.isEmpty(form.getState_SP1()) && TextUtils.isEmpty(form.getState_SP2())
							&& TextUtils.isEmpty(form.getState_SP3())) {// 判断是不是办公室
						num = "PoliceNum_SP2";
						name = "PoliceName_SP2";
						sate = "State_SP2";
						yj = "YJ_SP2";
						date = "Date_SP2";

						if (getRgValue(rb_spjg_agree).equals("同意")) {// 领导同意才选择车牌，不同意不传入车牌值
							table.putField("CID", et_car_list.getText().toString());// 选择的车牌号
						}

					} else if (!TextUtils.isEmpty(form.getState_SP1()) && !TextUtils.isEmpty(form.getState_SP2())
							&& TextUtils.isEmpty(form.getState_SP3())) {// 判断是不是分局领导
						num = "PoliceNum_SP3";
						name = "PoliceName_SP3";
						sate = "State_SP3";
						yj = "YJ_SP3";
						date = "Date_SP3";
					}

					table.setTableName("JNGA_Car_UseTable_CKS");
					table.putField(num, peopleInfo.getField("RESULT_POLICE_NUM"));
					table.putField(name, peopleInfo.getField("RESULT_POLICE_NAME"));
					table.putField(sate, getRgValue(rb_spjg_agree));
					table.putField(yj, et_spyj.getText().toString());
					table.putField(date, Utils.getNetTimeByType("yyyy/MM/dd HH:mm:ss"));

					if (DataOperation.insertOrUpdateTable(table)) {
						result = 1;
					} else {
						result = -1;
					}

				} else {
					result = -1;
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
				Toast.makeText(getActivity(), "上传失败", Toast.LENGTH_SHORT).show();
				break;
			case 0:
				Toast.makeText(getActivity(), "未检测到有效数据", Toast.LENGTH_SHORT).show();
				break;
			case 1:
				try {
					App.sDbUtils.deleteById(OfficialVehForm.class, form.getId());// 删除本地缓存
					ova.initWedgit(1);// 返回反馈列表
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 2:
				showCarDialog(et_car_list, carList, selectCarList);
				break;
			case 3:
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

	private String getRgValue(RadioGroup rg) {

		RadioButton rb = (RadioButton) rg.findViewById(rg.getCheckedRadioButtonId());

		return rb.getText().toString();

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
				AutoGridView gv_window = (AutoGridView) viewHolder.getView(R.id.gv_window);
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
			sb.append(people.getField("GWCNO").trim());
			sb.append(",");

		}
		et.setText(Html.fromHtml("<font color='#FF34B3'>" + sb.toString()));
	}


}
