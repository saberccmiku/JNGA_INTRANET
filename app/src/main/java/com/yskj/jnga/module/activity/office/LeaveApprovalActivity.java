package com.yskj.jnga.module.activity.office;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
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
import com.yskj.jnga.entity.ApprovalInfo;
import com.yskj.jnga.module.base.RxBaseActivity;
import com.yskj.jnga.network.xml.BaseTable;
import com.yskj.jnga.network.xml.DataOperation;
import com.yskj.jnga.utils.SpUtil;
import com.yskj.jnga.utils.Utils;
import com.yskj.jnga.widget.Button;
import com.yskj.jnga.widget.anim.dialog.DialogLoading;

public class LeaveApprovalActivity extends RxBaseActivity implements OnClickListener {

	private BaseTable table;
	private TextView tv_aaplicant_name, tv_aaplicant_unit, tv_work_time, tv_details__qwdd, tv_details__qjlb,
			tv_details_qjsj, tv_details_jjrsj, tv_reson;
//	private LinearLayout ll_select_leader;
	private ListView lv;
	private EditText et_spyj;
//	private EditText et_leader_list;
	private RadioGroup rg_spjg;
	private ArrayList<ApprovalInfo> approvalInfos;
	private SpUtil spUtil;
	private String isCompleted = "未完成";
	private AlertDialog ad;
	private ArrayList<BaseTable> tables;
	private int position;
//	private ArrayList<BaseTable> selectLeaderList;
	private BaseTable policeTable;
	private String taskType;// 当前线程任务类别 >1.loadCar:加载车库信息 2.uploadForm:上传审批表单

	@Override
	public int getLayoutId() {
		return R.layout.activity_leave_approval;
	}

	@Override
	public void initViews(Bundle savedInstanceState) {
		if (getActionBar() != null) {
			getActionBar().hide();
		}

		findView();
		initData();
		initWidget();
	}

	@Override
	public void initToolBar() {

	}

	private void findView() {

		Button btn_confirm = (Button) findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);
		ImageButton ib_back = (ImageButton) findViewById(R.id.ib_back);
		ib_back.setOnClickListener(this);

		// 个人信息
		LinearLayout include_grxx = (LinearLayout) findViewById(R.id.include_grxx);
		tv_aaplicant_name = (TextView) include_grxx.findViewById(R.id.tv_aaplicant_name);
		tv_aaplicant_unit = (TextView) include_grxx.findViewById(R.id.tv_aaplicant_unit);
		tv_work_time = (TextView) include_grxx.findViewById(R.id.tv_work_time);

		// 假期明细
		LinearLayout include_jqmx = (LinearLayout) findViewById(R.id.include_jqmx);
		tv_details__qwdd = (TextView) include_jqmx.findViewById(R.id.tv_details__qwdd);
		tv_details__qjlb = (TextView) include_jqmx.findViewById(R.id.tv_details__qjlb);
		tv_details_qjsj = (TextView) include_jqmx.findViewById(R.id.tv_details_qjsj);
		tv_details_jjrsj = (TextView) include_jqmx.findViewById(R.id.tv_details_jjrsj);
		tv_reson = (TextView) include_jqmx.findViewById(R.id.tv_reson);
		// 审批流程
		LinearLayout include_splc = (LinearLayout) findViewById(R.id.include_splc);
		lv = (ListView) include_splc.findViewById(R.id.lv);

		// 我的审批
		// rb_spjg_agree rb_spjg_disagree
		rg_spjg = (RadioGroup) findViewById(R.id.rg_spjg);
		//选择分管领导名单
		//ll_select_leader = (LinearLayout) findViewById(R.id.ll_select_leader);
//		et_leader_list = (EditText) findViewById(R.id.et_leader_list);
//		et_leader_list.setOnClickListener(this);
		et_spyj = (EditText) findViewById(R.id.et_spyj);
		rg_spjg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_spjg_agree:
					et_spyj.setText("拟同意。");
					break;
				case R.id.rb_spjg_disagree:
					et_spyj.setText("驳回。");
					break;
				default:
					break;
				}

			}
		});

	}

	private void initData() {
//		selectLeaderList = new ArrayList<>();
		spUtil = App.getInstance().getSpUtil();
		policeTable = spUtil.getPoliceInfo();
		tables = spUtil.getHolidayForm();
		approvalInfos = new ArrayList<>();
		Intent intent = getIntent();
		if (intent != null) {
			position = intent.getIntExtra("HolidayResultList", 0);
			table = tables.get(position);

			// 单位审批
			int id1 = 1;
			String level1 = "单位审批";
			String handlerName1 = table.getField("QJ_PoliceName_SP1");
			String Opinion1 = table.getField("QJ_YJ_SP1");
			String result1 = table.getField("QJ_State_SP1");
			String approvalTime1;
			if (TextUtils.isEmpty(result1)) {
				approvalTime1 = "";
			} else {
				approvalTime1 = table.getField("QJ_Date_SP1");
			}

			ApprovalInfo approvalInfo1 = new ApprovalInfo(id1, level1, handlerName1, Opinion1, result1, approvalTime1);
			// 政治出审批
			int id2 = 2;
			String level2 = "政治审批";
			String handlerName2 = table.getField("QJ_PoliceName_SP2");
			String Opinion2 = table.getField("QJ_YJ_SP2");
			String result2 = table.getField("QJ_State_SP2");
			String approvalTime2;
			if (TextUtils.isEmpty(result2)) {
				approvalTime2 = "";
			} else {
				approvalTime2 = table.getField("QJ_Date_SP2");
			}

			ApprovalInfo approvalInfo2 = new ApprovalInfo(id2, level2, handlerName2, Opinion2, result2, approvalTime2);
			// 分管审批
			int id3 = 3;
			String level3 = "分管审批";
			String handlerName3 = table.getField("QJ_PoliceName_SP3");
			String Opinion3 = table.getField("QJ_YJ_SP3");
			String result3 = table.getField("QJ_State_SP3");
			String approvalTime3;
			if (TextUtils.isEmpty(result3)) {
				approvalTime3 = "";
			} else {
				approvalTime3 = table.getField("QJ_Date_SP3");
			}

			ApprovalInfo approvalInfo3 = new ApprovalInfo(id3, level3, handlerName3, Opinion3, result3, approvalTime3);
			// 分局审批
			int id4 = 4;
			String level4 = "分局审批";
			String handlerName4 = table.getField("QJ_PoliceName_SP4");
			String Opinion4 = table.getField("QJ_YJ_SP4");
			String result4 = table.getField("QJ_State_SP4");
			String approvalTime4;
			if (TextUtils.isEmpty(result4)) {
				approvalTime4 = "";
			} else {
				approvalTime4 = table.getField("QJ_Date_SP4");
			}

			ApprovalInfo approvalInfo4 = new ApprovalInfo(id4, level4, handlerName4, Opinion4, result4, approvalTime4);
			// 审批进度
			approvalInfos.add(approvalInfo1);
			approvalInfos.add(approvalInfo2);
			approvalInfos.add(approvalInfo3);
			approvalInfos.add(approvalInfo4);

		}

	}

	private void initWidget() {

		tv_aaplicant_name.setText(table.getField("QJ_PoliceName"));
		tv_aaplicant_unit.setText(table.getField("QJ_Punit"));
		tv_work_time.setText(table.getField("QJ_WTime"));
		tv_details__qwdd.setText(table.getField("QJ_Address"));
		tv_details__qjlb.setText(table.getField("QJ_JType"));
		tv_reson.setText(table.getField("QJ_Reason"));
		String stime = table.getField("QJ_STime");
		String etime = table.getField("QJ_Etime");
		tv_details_qjsj.setText(stime + "至" + etime);
		tv_details_jjrsj.setText(table.getField("QJ_PUnit_Use"));
		et_spyj.setText("拟同意。");
		//政治处显示分管领导
//		if (policeTable.getField("RESULT_POLICE_UINT").equals("政治处")) {
//
//			if (table.getFieldValue("QJ_PType").contains("正职") || table.getFieldValue("QJ_PType").contains("副职")) {
//				ll_select_leader.setVisibility(View.VISIBLE);
//			} else {
//				ll_select_leader.setVisibility(View.GONE);
//			}
//
//		} else {
//			ll_select_leader.setVisibility(View.GONE);
//		}

		CommonAdapter<ApprovalInfo> adapter = new CommonAdapter<ApprovalInfo>(this, approvalInfos,R.layout.item_approval) {

			@Override
			public void convert(ViewHolder viewHolder, ApprovalInfo info) {
				viewHolder.setText(R.id.tv_id, String.valueOf(info.getId()));
				viewHolder.setText(R.id.tv_level, info.getLevel());
				viewHolder.setText(R.id.tv_handler_name, info.getHandlerName());
				viewHolder.setText(R.id.tv_opinion, info.getOpinion());
				viewHolder.setText(R.id.tv_result, info.getResult());
				viewHolder.setText(R.id.tv_approval_time, info.getApprovalTime());

			}
		};

		lv.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_confirm:
			
			if (Utils.isNetworkAvailable(LeaveApprovalActivity.this)) {
				taskType = "upload";
				new LeavaApprovalTask().execute("upload");
			}
			
//			if (policeTable.getField("RESULT_POLICE_UINT").equals("政治处") && getRgValue(rg_spjg).equals("同意")
//					&& table.getFieldValue("QJ_State_SP1").equals("同意")
//					&& TextUtils.isEmpty(et_leader_list.getText().toString())
//					&& (table.getFieldValue("QJ_PType").contains("正职")
//							|| table.getFieldValue("QJ_PType").contains("副职"))) {
//
//				Toast.makeText(LeaveApprovalActivity.this, "请指定分局领导", Toast.LENGTH_SHORT).show();
//
//			} else {
//				if (Util.isNetworkAvailable(LeaveApprovalActivity.this)) {
//					taskType = "upload";
//					new LeavaApprovalTask().execute("upload");
//				}
//			}
			
			break;
		case R.id.ib_back:
			this.finish();
			break;
//		case R.id.et_leader_list:
//			if (Util.isNetworkAvailable(this)) {
//				taskType = "loadLeader";
//				new LeavaApprovalTask().execute("loadLeader");
//			} else {
//				Toast.makeText(this, "网络异常", Toast.LENGTH_SHORT).show();
//			}
//
//			break;

		default:
			break;
		}

	}

	private class LeavaApprovalTask extends AsyncTask<String, Integer, Integer> {

		private ArrayList<BaseTable> leaderList;

		@Override
		protected void onPreExecute() {
			if (taskType.equals("upload")) {
				ad = DialogLoading.getProgressDialog(LeaveApprovalActivity.this, "正在上传...");
			} else {
				ad = DialogLoading.getProgressDialog(LeaveApprovalActivity.this, "正在加载...");
			}

			ad.show();

		}

		@Override
		protected Integer doInBackground(String... params) {
			int result = 0;

			switch (params[0]) {
			case "loadLeader":

				leaderList = DataOperation.queryTable("JNGA_ROLE");
				if (leaderList != null && leaderList.size() != 0) {
					result = 2;
				} else {
					result = 3;
				}

				break;
			case "upload":
				try {
					if (DataOperation.insertOrUpdateTable(getApprovalTable())) {

						// 提交审核之后减少本地信息条数
						Utils.updateMessageCount(spUtil, -1);
						// 更新本地缓存
						tables.remove(position);
						spUtil.setHolidayForm(tables);

						result = 1;
					}
				} catch (Exception e) {

					e.printStackTrace();
					result = -1;
				}
				break;
			}

			return result;
		}

		@Override
		protected void onPostExecute(Integer result) {

			if (ad.isShowing()) {
				ad.cancel();
			}

			switch (result) {
			case -1:
				Toast.makeText(LeaveApprovalActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
				break;
			case 0:

				break;
			case 1:
				LeaveApprovalActivity.this.finish();
				break;
//			case 2:
//				showLeaderDialog(et_leader_list, leaderList);
//				break;
			case 3:
				Toast.makeText(LeaveApprovalActivity.this, "暂无可用数据，请联系管理员", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}

	}

	/**
	 * 设置不同审批人员审批表提交数据
	 * 
	 * @return
	 */
	public BaseTable getApprovalTable() {

		String policeType = policeTable.getField("RESULT_POLICE_DISTRICT");
		String policeUnit = policeTable.getField("RESULT_POLICE_UINT");
		String otherType = table.getField("QJ_PType");
		// 前提：内网触发器已经将能收到请假的数据过滤，所以这里提交的数据一定是需要本人审批的。
		// 过滤规则为
		if ((policeType.equals("基础正职领导") && !policeUnit.equals("政治处")) || policeType.equals("基础副职领导")) {
			// 如果审批人不是政治处，并且是基础正副职领导，那么下一步提交给政治处
			isCompleted = "未完成";
			table.putField("QJ_YJ_SP1", et_spyj.getText().toString());
			table.putField("QJ_PoliceNum_SP1", policeTable.getField("RESULT_POLICE_NUM"));
			table.putField("QJ_PoliceName_SP1", policeTable.getField("RESULT_POLICE_NAME"));
			table.putField("QJ_State_SP1",
					((RadioButton) findViewById(rg_spjg.getCheckedRadioButtonId())).getText().toString());
			table.putField("QJ_Date_SP1", Utils.getNetTime().substring(0, 10));

		} else if (policeUnit.equals("政治处") && policeType.equals("基础正职领导")) {
			if (otherType.equals("民警")) {
				isCompleted = "完成";// 民警的请假本单位领导审批，因为本单位是政治处所以直接完结，不提交政治处
			} else {
				isCompleted = "未完成";// 政治处领导的请假本单位领导审批，因为本单位是政治处所以直接提交给分局审批
			}
			table.putField("QJ_YJ_SP2", et_spyj.getText().toString());
			table.putField("QJ_PoliceNum_SP2", policeTable.getField("RESULT_POLICE_NUM"));
			table.putField("QJ_PoliceName_SP2", policeTable.getField("RESULT_POLICE_NAME"));
			table.putField("QJ_State_SP2",
					((RadioButton) findViewById(rg_spjg.getCheckedRadioButtonId())).getText().toString());
			table.putField("QJ_Date_SP2", Utils.getNetTime());
//			if ((policeTable.getField("RESULT_POLICE_UINT").equals("政治处") && getRgValue(rg_spjg).equals("同意")
//					&& table.getFieldValue("QJ_State_SP1").equals("同意")
//					&& TextUtils.isEmpty(et_leader_list.getText().toString())
//					&& (table.getFieldValue("QJ_PType").contains("正职")
//							|| table.getFieldValue("QJ_PType").contains("副职")))) {// 如果是领导的申请并且政治处同意，则需要分局或者分管审批
//				table.putFieldValue("QJ_BiaoShi", selectLeaderList.get(0).getField("PoliceNum") + "-");
//			}

		} else if (policeType.equals("分局副职领导")) {
			isCompleted = "完成";
			table.putField("QJ_YJ_SP3", et_spyj.getText().toString());
			table.putField("QJ_PoliceNum_SP3", policeTable.getField("RESULT_POLICE_NUM"));
			table.putField("QJ_PoliceName_SP3", policeTable.getField("RESULT_POLICE_NAME"));
			table.putField("QJ_State_SP3",
					((RadioButton) findViewById(rg_spjg.getCheckedRadioButtonId())).getText().toString());
			table.putField("QJ_Date_SP3", Utils.getNetTime());

		} else if (policeType.equals("分局正职领导")) {
			isCompleted = "完成";
			table.putField("QJ_YJ_SP4", et_spyj.getText().toString());
			table.putField("QJ_PoliceNum_SP4", policeTable.getField("RESULT_POLICE_NUM"));
			table.putField("QJ_PoliceName_SP4", policeTable.getField("RESULT_POLICE_NAME"));
			table.putField("QJ_State_SP4",
					((RadioButton) findViewById(rg_spjg.getCheckedRadioButtonId())).getText().toString());
			table.putField("QJ_Date_SP4", Utils.getNetTime());
		}

		// 时间值处理
		handlerTime(new String[] { "QJ_CreateTime", "QJ_WTime", "QJ_STime", "QJ_Etime", "QJ_Date_SP1", "QJ_Date_SP2",
				"QJ_Date_SP3", "QJ_Date_SP4", "QJ_XJ_Date", "QJ_XJ_PTime", "QJ_XJ_ZTime" });

		table.putField("QJ_SPState", isCompleted);
		table.setTableName("JNGA_QJ_INFO_CKS");
		table.setContentId("");
		return table;
	}

	/**
	 * 时间值处理
	 * 
	 * @param array
	 */
	public void handlerTime(String[] array) {

		for (String str : array) {
			if (!TextUtils.isEmpty(table.getField(str))) {
				table.putField(str, table.getField(str).substring(0, 10));
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

	/**
	 * 名单列表
	 */
//	public void showLeaderDialog(final EditText et, final ArrayList<BaseTable> tables) {
//		
//		View v_container = LayoutInflater.from(this).inflate(R.layout.dialog_radio, null);
//		ad = new AlertDialog.Builder(this).setView(v_container).show();
//		TextView tv_title_01 = (TextView) v_container.findViewById(R.id.tv_title_01);
//		tv_title_01.setText("人员名单");
//		TextView tv_count = (TextView) v_container.findViewById(R.id.tv_count);
//		tv_count.setText(String.valueOf(tables.size()));
//		final CustomRadioGroup rg = (CustomRadioGroup) v_container.findViewById(R.id.rg);
//
//		for (BaseTable baseTable : tables) {
//			RadioButton radioButton = new RadioButton(this);
//			RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,
//					RadioGroup.LayoutParams.WRAP_CONTENT);
//			layoutParams.setMargins(10, 10, 10, 10);
//			radioButton.setLayoutParams(layoutParams);
//			radioButton.setText(baseTable.getField("PoliceName"));
//			radioButton.setTextSize(12);
//			// radioButton.setButtonDrawable(android.R.color.transparent);//隐藏单选圆形按钮
//			radioButton.setGravity(Gravity.CENTER);
//			radioButton.setPadding(10, 10, 10, 10);
//			rg.addView(radioButton);// 将单选按钮添加到RadioGroup中
//		}
//		
//		rg.setSingleColumn(false);
//		rg.setColumnNumber(3);
//		rg.setColumnHeight(getResources().getDimensionPixelSize(R.dimen.x100));
//
//		ImageButton ib_close = (ImageButton) v_container.findViewById(R.id.ib_close);
//		ib_close.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				ad.cancel();
//
//			}
//		});
//
//		ad.setOnCancelListener(new OnCancelListener() {
//
//			@Override
//			public void onCancel(DialogInterface dialog) {
//				selectLeaderList.clear();
//				selectLeaderList.add(tables.get(rg.indexOfChild(rg.findViewById(rg.getCheckedRadioButtonId()))));
//				et.setText(getRgValue(rg));
//				et.setTextColor(Color.parseColor("#DC143C"));
//			}
//		});
//
//	}

//	private String getRgValue(RadioGroup rg) {
//
//		RadioButton rb = (RadioButton) rg.findViewById(rg.getCheckedRadioButtonId());
//
//		return rb.getText().toString();
//
//	}

}
