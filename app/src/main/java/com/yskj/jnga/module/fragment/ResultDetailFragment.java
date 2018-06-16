package com.yskj.jnga.module.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yskj.jnga.App;
import com.yskj.jnga.R;
import com.yskj.jnga.network.ApiConstants;
import com.yskj.jnga.network.xml.BaseTable;
import com.yskj.jnga.utils.WaterMarkbd;

public class ResultDetailFragment extends Fragment
{
	private View rootView;
	private String searchType;
	private BaseTable resultData;
	
	public static ResultDetailFragment newInstance(String searchType, BaseTable table)
	{
		ResultDetailFragment fragment = new ResultDetailFragment();
		fragment.searchType = searchType;
		fragment.resultData = table;
		return  fragment;
	}
	
	private void initVar()
	{
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		initVar();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		//根据数据类型的不同加载相应的视图
		if (ApiConstants.ONE_KEY_RESEARCH.equals(searchType))
		{
			VehicleViewHolder vvh;
			if(rootView == null)
			{
				rootView = inflater.inflate(R.layout.fragment_vehicle_result_detail, container, false);
				vvh = new VehicleViewHolder();
				vvh.ownerName = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_owner_name);
				vvh.ownerId = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_owner_id);
				vvh.carId = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_car_id);
				vvh.carLpnType = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_car_lpnType);
				vvh.carType = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_car_type);
				vvh.vehEngine = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_veh_engine);
				vvh.vehVIN = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_veh_vin);
				vvh.vehColor = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_veh_color);
				vvh.typeName = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_typename);
				vvh.time = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_time);
				vvh.address = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_address);
				vvh.info = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_info);
				vvh.zzdz = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_veh_zzdz);
				vvh.xxdz = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_veh_xxdz);
				vvh.ccrq = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_veh_ccrq);
				vvh.xszxbh = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_veh_xszxbh);
				vvh.phone = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_veh_phone);
				vvh.ccdj = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_veh_ccdj);
				vvh.zzcmc = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_veh_zzcmc);
				rootView.setTag(vvh);
			}
			else
			{
				vvh = (VehicleViewHolder) rootView.getTag();
			}
			
			vvh.ownerName.setText(resultData.getField("PPL_NAME"));
			vvh.ownerId.setText(resultData.getField("PPL_ID"));
			vvh.carId.setText(resultData.getField("VEH_LPN"));
			vvh.carLpnType.setText(resultData.getField("VEH_TYPE"));
			vvh.carType.setText(resultData.getField(getVehType(searchType)));
			vvh.vehEngine.setText(resultData.getField("VEH_EN"));
			vvh.vehVIN.setText(resultData.getField("VEH_VIN"));
			vvh.vehColor.setText(resultData.getField("VEH_COLOR"));
			vvh.typeName.setText(resultData.getField("VEH_TN"));
			vvh.time.setText("");
			vvh.address.setText("");
			vvh.info.setText(resultData.getField("REM"));
			vvh.zzdz.setText(resultData.getField("VEH_ZZDZ"));
			vvh.xxdz.setText(resultData.getField("VEH_XXDZ"));
			vvh.ccrq.setText(resultData.getField("VEH_CCRQ"));
			vvh.xszxbh.setText(resultData.getField("VEH_XSZXBH"));
			vvh.phone.setText(resultData.getField("VEH_PHONE"));
			vvh.ccdj.setText(resultData.getField("VEH_CCDJ"));
			vvh.zzcmc.setText(resultData.getField("VEH_ZZCMC"));
		}
		else if (ApiConstants.PERSON_RESEARCH.equals(searchType))
		{
			PersonViewHolder pvh;
			if(rootView == null)
			{
				rootView = inflater.inflate(R.layout.fragment_people_result_detail, container, false);
				pvh = new PersonViewHolder();
				pvh.name = (TextView) rootView.findViewById(R.id.tv_people_result_detial_ppl_name);
				pvh.sex = (TextView) rootView.findViewById(R.id.tv_people_result_detial_ppl_sex);
				pvh.nation = (TextView) rootView.findViewById(R.id.tv_people_result_detial_ppl_nation);
				pvh.birt = (TextView) rootView.findViewById(R.id.tv_people_result_detial_ppl_birt);
				pvh.address = (TextView) rootView.findViewById(R.id.tv_people_result_detial_ppl_address);
				pvh.id = (TextView) rootView.findViewById(R.id.tv_people_result_detail_ppl_id);
				pvh.info = (TextView) rootView.findViewById(R.id.tv_people_result_detial_info);
				pvh.caseType = (TextView) rootView.findViewById(R.id.tv_people_result_detail_case_type);
				rootView.setTag(pvh);
			}
			else
			{
				pvh = (PersonViewHolder) rootView.getTag();
			}
			
			pvh.name.setText(resultData.getField("PPL_NAME"));
			pvh.sex.setText(resultData.getField("PPL_SEX"));
			pvh.nation.setText(resultData.getField("PPL_NATION"));
			pvh.birt.setText(resultData.getField("PPL_BIRT"));
			pvh.address.setText(resultData.getField("PPL_ADD"));
			pvh.id.setText(resultData.getField("PPL_ID"));
			pvh.caseType.setText(resultData.getField("PPL_RECORD_CT"));
			pvh.info.setText(resultData.getField("REM"));
		}
		else if (ApiConstants.VEHICLE_RESEARCH.equals(searchType))
		{
			VehicleViewHolder vvh;
			if(rootView == null)
			{
				rootView = inflater.inflate(R.layout.fragment_vehicle_result_detail, container, false);
				vvh = new VehicleViewHolder();
				vvh.ownerName = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_owner_name);
				vvh.ownerId = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_owner_id);
				vvh.carId = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_car_id);
				vvh.carLpnType = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_car_lpnType);
				vvh.carType = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_car_type);
				vvh.vehEngine = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_veh_engine);
				vvh.vehVIN = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_veh_vin);
				vvh.vehColor = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_veh_color);
				vvh.typeName = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_typename);
				vvh.time = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_time);
				vvh.address = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_address);
				vvh.info = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_info);
				vvh.zzdz = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_veh_zzdz);
				vvh.xxdz = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_veh_xxdz);
				vvh.ccrq = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_veh_ccrq);
				vvh.xszxbh = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_veh_xszxbh);
				vvh.phone = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_veh_phone);
				vvh.ccdj = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_veh_ccdj);
				vvh.zzcmc = (TextView) rootView.findViewById(R.id.tv_vehicle_result_detial_veh_zzcmc);
				rootView.setTag(vvh);
			}
			else
			{
				vvh = (VehicleViewHolder) rootView.getTag();
			}
			
			vvh.ownerName.setText(resultData.getField("PPL_NAME"));
			vvh.ownerId.setText(resultData.getField("PPL_ID"));
			vvh.carId.setText(resultData.getField("VEH_LPN"));
			vvh.carLpnType.setText(resultData.getField("VEH_TYPE"));
			vvh.carType.setText(resultData.getField(getVehType(searchType)));
			vvh.vehEngine.setText(resultData.getField("VEH_EN"));
			vvh.vehVIN.setText(resultData.getField("VEH_VIN"));
			vvh.vehColor.setText(resultData.getField("VEH_COLOR"));
			vvh.typeName.setText(resultData.getField("VEH_TN"));
			vvh.time.setText("");
			vvh.address.setText("");
			vvh.info.setText(resultData.getField("REM"));
			vvh.zzdz.setText(resultData.getField("VEH_ZZDZ"));
			vvh.xxdz.setText(resultData.getField("VEH_XXDZ"));
			vvh.ccrq.setText(resultData.getField("VEH_CCRQ"));
			vvh.xszxbh.setText(resultData.getField("VEH_XSZXBH"));
			vvh.phone.setText(resultData.getField("VEH_PHONE"));
			vvh.ccdj.setText(resultData.getField("VEH_CCDJ"));
			vvh.zzcmc.setText(resultData.getField("VEH_ZZCMC"));
		}
		else if(ApiConstants.GOODS_RESEARCH.equals(searchType))
		{
			
		}
		else if(ApiConstants.CASE_RESEARCH.equals(searchType))
		{
			CaseViewHolder cvh;
			if(rootView == null)
			{
				rootView = inflater.inflate(R.layout.fragment_case_result_detail, container, false);
				cvh = new CaseViewHolder();
				cvh.caseid = (TextView) rootView.findViewById(R.id.tv_case_result_detial_caseid);
				cvh.slunit = (TextView) rootView.findViewById(R.id.tv_case_result_detial_slunit);
				cvh.ajstate = (TextView) rootView.findViewById(R.id.tv_case_result_detial_ajstate);
				cvh.jjtype = (TextView) rootView.findViewById(R.id.tv_case_result_detial_jjtype);
				cvh.ajtype = (TextView) rootView.findViewById(R.id.tv_case_result_detial_ajtype);
				cvh.anbie = (TextView) rootView.findViewById(R.id.tv_case_result_detial_anbie);
				cvh.ajname = (TextView) rootView.findViewById(R.id.tv_case_result_detial_ajname);
				cvh.bjtime = (TextView) rootView.findViewById(R.id.tv_case_result_detial_bjtime);
				cvh.starttime = (TextView) rootView.findViewById(R.id.tv_case_result_detial_starttime);
				cvh.endtime = (TextView) rootView.findViewById(R.id.tv_case_result_detial_endtime);
				cvh.quxian = (TextView) rootView.findViewById(R.id.tv_case_result_detial_quxian);
				cvh.street = (TextView) rootView.findViewById(R.id.tv_case_result_detial_street);
				cvh.address = (TextView) rootView.findViewById(R.id.tv_case_result_detial_address);
				cvh.area = (TextView) rootView.findViewById(R.id.tv_case_result_detial_area);
				cvh.selecttime = (TextView) rootView.findViewById(R.id.tv_case_result_detial_selecttime);
				cvh.selectspace = (TextView) rootView.findViewById(R.id.tv_case_result_detial_selectspace);
				cvh.worktool = (TextView) rootView.findViewById(R.id.tv_case_result_detial_worktool);
				cvh.selectpart = (TextView) rootView.findViewById(R.id.tv_case_result_detial_selectpart);
				cvh.selectobj = (TextView) rootView.findViewById(R.id.tv_case_result_detial_selectobj);
				cvh.content = (TextView) rootView.findViewById(R.id.tv_case_result_detial_content);
				cvh.zcyzb = (TextView) rootView.findViewById(R.id.tv_case_result_detial_zcyzb);
				cvh.zcyxb = (TextView) rootView.findViewById(R.id.tv_case_result_detial_zcyxb);
				cvh.methods = (TextView) rootView.findViewById(R.id.tv_case_result_detial_methods);
				cvh.chengdu = (TextView) rootView.findViewById(R.id.tv_case_result_detial_chengdu);
				cvh.aprice = (TextView) rootView.findViewById(R.id.tv_case_result_detial_aprice);
				rootView.setTag(cvh);
			}
			else
			{
				cvh = (CaseViewHolder) rootView.getTag();
			}
			
			cvh.caseid.setText(resultData.getField("RESULT_CASEID"));
			cvh.slunit.setText(resultData.getField("RESULT_SLUNIT"));
			cvh.ajstate.setText(resultData.getField("RESULT_AJSTATE"));
			cvh.jjtype.setText(resultData.getField("RESULT_JJTYPE"));
			cvh.ajtype.setText(resultData.getField("RESULT_AJTYPE"));
			cvh.anbie.setText(resultData.getField("RESULT_ANBIE"));
			cvh.ajname.setText(resultData.getField("RESULT_AJNAME"));
			cvh.bjtime.setText(resultData.getField("RESULT_BJTIME"));
			cvh.starttime.setText(resultData.getField("RESULT_START_TIME"));
			cvh.endtime.setText(resultData.getField("RESULT_END_TIME"));
			cvh.quxian.setText(resultData.getField("RESULT_QUXIAN"));
			cvh.street.setText(resultData.getField("RESULT_STREET"));
			cvh.address.setText(resultData.getField("RESULT_ADDRESS"));
			cvh.area.setText(resultData.getField("RESULT_AREA"));
			cvh.selecttime.setText(resultData.getField("RESULT_SELECT_TIME"));
			cvh.selectspace.setText(resultData.getField("RESULT_SELECT_SPACE"));
			cvh.worktool.setText(resultData.getField("RESULT_WORK_TOOL"));
			cvh.selectpart.setText(resultData.getField("RESULT_SELECT_PART"));
			cvh.selectobj.setText(resultData.getField("RESULT_SELECT_OBJ"));
			cvh.content.setText(resultData.getField("RESULT_CONTENT"));
			cvh.zcyzb.setText(resultData.getField("RESULT_ZCY_ZB"));
			cvh.zcyxb.setText(resultData.getField("RESULT_ZCY_XB"));
			cvh.methods.setText(resultData.getField("RESULT_METHODS"));
			cvh.chengdu.setText(resultData.getField("RESULT_CHENGDU"));
			cvh.aprice.setText(resultData.getField("RESULT_APRICE"));
		}
		
		//加水印
		if(rootView!=null){
			WaterMarkbd.setWaterMarkbd(getActivity(), rootView, App.getInstance().getSpUtil().getAccount());
		}
		
		return rootView;
	}
	
	private class PersonViewHolder //人员信息ViewHolder
	{
		public TextView name; //姓名
		public TextView sex; //性别
		public TextView nation; //民族
		public TextView birt; //出生日期
		public TextView address; //居住地
		public TextView id; //身份证
		public TextView caseType;//涉案类别
		public TextView info; //备注信息
	}
	
	private class VehicleViewHolder //车辆信息ViewHolder
	{
		public TextView ownerName; //车主姓名
		public TextView ownerId; //车主身份证
		public TextView carId; //车牌号
		public TextView carLpnType; //车牌种类
		public TextView carType; //车辆类型
		public TextView vehEngine; //发动机号
		public TextView vehVIN; //车架号
		public TextView vehColor; //车身颜色
		public TextView typeName; //车辆类型名称
		public TextView time;
		public TextView address;
		public TextView info; //备注信息
		public TextView zzdz;
		public TextView xxdz;
		public TextView ccrq;
		public TextView xszxbh;
		public TextView phone;
		public TextView ccdj;
		public TextView zzcmc;
	}
	
	private class CaseViewHolder //案件信息ViewHolder
	{
		public TextView caseid;
		public TextView slunit;
		public TextView ajstate;
		public TextView jjtype;
		public TextView ajtype;
		public TextView anbie;
		public TextView ajname;
		public TextView bjtime;
		public TextView starttime;
		public TextView endtime;
		public TextView quxian;
		public TextView street;
		public TextView address;
		public TextView area;
		public TextView selecttime;
		public TextView selectspace;
		public TextView worktool;
		public TextView selectpart;
		public TextView selectobj;
		public TextView content;
		public TextView zcyzb;
		public TextView zcyxb;
		public TextView methods;
		public TextView chengdu;
		public TextView aprice;
	}
	
	private String getVehType(String searchType)
	{
		switch(searchType)
		{
		
		}
		
		return "";
	}
}
