package com.yskj.jnga.module.fragment.office;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yskj.jnga.R;
import com.yskj.jnga.adapter.CommonAdapter;
import com.yskj.jnga.adapter.ViewHolder;
import com.yskj.jnga.entity.ApprovalInfo;
import com.yskj.jnga.entity.OfficialVehForm;
import com.yskj.jnga.module.activity.office.OfficialVehiclesActivity;


public class OfficialVehProgressFragment extends Fragment {

	private OfficialVehiclesActivity ova;
	private View view;
	private TextView tv_driver, tv_applicant, tv_tel, tv_unit, tv_people, tv_to_add, tv_reason, tv_time;
	private ListView lv;
	private OfficialVehForm form;

	public static OfficialVehProgressFragment newInstance() {

		return new OfficialVehProgressFragment();
	}

	@Override
	public void onAttach(Activity activity) {
		ova = (OfficialVehiclesActivity) activity;
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_official_veh_progress, null);
		findView();
		initData();
		return view;
	}

	private void initData() {

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
	}

}
