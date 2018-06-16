package com.yskj.jnga.module.activity.office;

import java.util.ArrayList;

import com.yskj.jnga.R;
import com.yskj.jnga.adapter.CommonAdapter;
import com.yskj.jnga.adapter.ViewHolder;
import com.yskj.jnga.entity.ApprovalInfo;
import com.yskj.jnga.module.base.RxBaseActivity;
import com.yskj.jnga.network.xml.BaseTable;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


public class HolidaysProgressActivity extends RxBaseActivity implements OnClickListener {

	private BaseTable table;
	private TextView tv_aaplicant_name, tv_aaplicant_unit, tv_work_time, tv_details__qwdd, tv_details__qjlb,
			tv_details_qjsj, tv_details_jjrsj, tv_reson;
	private ListView lv;
	private ArrayList<ApprovalInfo> approvalInfos;


	@Override
	public int getLayoutId() {
		return R.layout.activity_holidays_progress;
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

	}

	private void initData() {
		approvalInfos = new ArrayList<>();
		Intent intent = getIntent();
		if (intent != null) {
			table = (BaseTable) intent.getSerializableExtra("HolidayResultList");
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

		case R.id.ib_back:
			this.finish();
			break;

		default:
			break;
		}

	}


}
