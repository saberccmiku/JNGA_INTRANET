package com.yskj.jnga.module.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.yskj.jnga.R;
import com.yskj.jnga.entity.HistoryRecord;
import com.yskj.jnga.network.ApiConstants;
import com.yskj.jnga.network.xml.DataSetList;
import com.yskj.jnga.network.xml.DocInfor;
import com.yskj.jnga.network.xml.PostHttp;
import com.yskj.jnga.network.xml.XmlPackage;
import com.yskj.jnga.utils.IOUtils;
import com.yskj.jnga.utils.Utils;
import com.yskj.jnga.widget.dropedittext.DropEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 搜索页面Fragment； 根据传入的搜索类型参数，展示对应的搜索页面；
 * 
 */
public class SearchInfoFragment extends Fragment implements OnClickListener {
	private View rootView; // Fragment的根视图
	private WidgetList widgetList; // 根视图中包含的一个子组件列表(该列表中包含了根视图中所有的子组件)

	private DataSetList resultData;
	private String searchType;
	private HistoryRecord historyRecord;
	private final String cacheFilePath = Environment.getExternalStorageDirectory().getPath() + ApiConstants.fileCachePath
			+ "searchRecord";
	private String[] lpnTypes = { "", "临时", "正式" };
	private String selectedLpnType = "";

	public SearchInfoFragment(){}

	public static SearchInfoFragment newInstance(String searchType) {
		SearchInfoFragment fragment = new SearchInfoFragment();
		fragment.searchType = searchType;
		return fragment;
	}

	private void initVar() {
		historyRecord = (HistoryRecord) IOUtils.readObjectFromFile(IOUtils.mkFileWithDirs(cacheFilePath));
		if (historyRecord == null)
			historyRecord = new HistoryRecord();
	}

	private void initWidget() {
		// 根据页面的不同，动态加载相应的子组件
		if (ApiConstants.ONE_KEY_RESEARCH.equals(searchType)) {
			widgetList = new WidgetList.OnekeyWidgetList();
			((WidgetList.OnekeyWidgetList) widgetList).etKeyword = (AutoCompleteTextView) rootView
					.findViewById(R.id.et_onekey_search_keyword);
//			((WidgetList.OnekeyWidgetList) widgetList).btnSearch = (ButtonFlat) rootView
//					.findViewById(R.id.btn_onekey_search_search);

//			((WidgetList.OnekeyWidgetList) widgetList).btnSearch.setRippleSpeed(30);
//			((WidgetList.OnekeyWidgetList) widgetList).btnSearch.setRippleColor(Color.parseColor("#8dbfe7"));

			((WidgetList.OnekeyWidgetList) widgetList).etKeyword.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, historyRecord.oneKeySearchRecord.keyWord));
			((WidgetList.OnekeyWidgetList) widgetList).etKeyword.setThreshold(0);
		} else if (ApiConstants.PERSON_RESEARCH.equals(searchType)) {
			widgetList = new WidgetList.PeopleWidgetList();
			((WidgetList.PeopleWidgetList) widgetList).etPplName = (AutoCompleteTextView) rootView
					.findViewById(R.id.et_people_search_ppl_name);
			((WidgetList.PeopleWidgetList) widgetList).etPplId = (AutoCompleteTextView) rootView
					.findViewById(R.id.et_people_search_ppl_id);
			((WidgetList.PeopleWidgetList) widgetList).etPplSex = (AutoCompleteTextView) rootView
					.findViewById(R.id.et_people_search_ppl_sex);
			((WidgetList.PeopleWidgetList) widgetList).etPplBirt = (AutoCompleteTextView) rootView
					.findViewById(R.id.et_people_search_ppl_birt);
			((WidgetList.PeopleWidgetList) widgetList).etPplAdd = (AutoCompleteTextView) rootView
					.findViewById(R.id.et_people_search_ppl_add);
//			((WidgetList.PeopleWidgetList) widgetList).btnSearch = (ButtonFlat) rootView
//					.findViewById(R.id.btn_person_search_search);
//
//			((WidgetList.PeopleWidgetList) widgetList).btnSearch.setRippleSpeed(30);
//			((WidgetList.PeopleWidgetList) widgetList).btnSearch.setRippleColor(Color.parseColor("#8dbfe7"));

			((WidgetList.PeopleWidgetList) widgetList).etPplName.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, historyRecord.peopleSearchRecord.pplName));
			((WidgetList.PeopleWidgetList) widgetList).etPplId.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, historyRecord.peopleSearchRecord.pplId));
			((WidgetList.PeopleWidgetList) widgetList).etPplSex.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, historyRecord.peopleSearchRecord.pplSex));
			((WidgetList.PeopleWidgetList) widgetList).etPplBirt.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, historyRecord.peopleSearchRecord.pplBirt));
			((WidgetList.PeopleWidgetList) widgetList).etPplAdd.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, historyRecord.peopleSearchRecord.pplAdd));
			((WidgetList.PeopleWidgetList) widgetList).etPplName.setThreshold(0);
			((WidgetList.PeopleWidgetList) widgetList).etPplId.setThreshold(0);
			((WidgetList.PeopleWidgetList) widgetList).etPplSex.setThreshold(0);
			((WidgetList.PeopleWidgetList) widgetList).etPplBirt.setThreshold(0);
			((WidgetList.PeopleWidgetList) widgetList).etPplAdd.setThreshold(0);
		} else if (ApiConstants.VEHICLE_RESEARCH.equals(searchType)) {
			widgetList = new WidgetList.VehicleWidgetList();
			((WidgetList.VehicleWidgetList) widgetList).etVehLpn = (AutoCompleteTextView) rootView
					.findViewById(R.id.et_vehicle_search_ven_lpn);
			((WidgetList.VehicleWidgetList) widgetList).det = (DropEditText) rootView.findViewById(R.id.det);
			((WidgetList.VehicleWidgetList) widgetList).sLpnType = (Spinner) rootView
					.findViewById(R.id.s_vehicle_search_ven_lpnType);
			((WidgetList.VehicleWidgetList) widgetList).etVehEn = (AutoCompleteTextView) rootView
					.findViewById(R.id.et_vehicle_search_ven_en);
			((WidgetList.VehicleWidgetList) widgetList).etVehVin = (AutoCompleteTextView) rootView
					.findViewById(R.id.et_vehicle_search_ven_vin);
			((WidgetList.VehicleWidgetList) widgetList).etVehColor = (AutoCompleteTextView) rootView
					.findViewById(R.id.et_vehicle_search_ven_color);
			((WidgetList.VehicleWidgetList) widgetList).etPplName = (AutoCompleteTextView) rootView
					.findViewById(R.id.et_vehicle_search_ppl_name);
			((WidgetList.VehicleWidgetList) widgetList).etPplSex = (AutoCompleteTextView) rootView
					.findViewById(R.id.et_vehicle_search_ppl_sex);
			((WidgetList.VehicleWidgetList) widgetList).etPplBirt = (AutoCompleteTextView) rootView
					.findViewById(R.id.et_vehicle_search_ppl_birt);
			((WidgetList.VehicleWidgetList) widgetList).etPplId = (AutoCompleteTextView) rootView
					.findViewById(R.id.et_vehicle_search_ppl_id);
			((WidgetList.VehicleWidgetList) widgetList).etPplAdd = (AutoCompleteTextView) rootView
					.findViewById(R.id.et_vehicle_search_ppl_add);
//			((WidgetList.VehicleWidgetList) widgetList).btnSearch = (ButtonFlat) rootView
//					.findViewById(R.id.btn_vehicle_search_search);
//
//			((WidgetList.VehicleWidgetList) widgetList).btnSearch.setRippleSpeed(30);
//			((WidgetList.VehicleWidgetList) widgetList).btnSearch.setRippleColor(Color.parseColor("#8dbfe7"));

			((WidgetList.VehicleWidgetList) widgetList).etVehLpn.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, historyRecord.vehicleSearchRecord.vehLpn));
			((WidgetList.VehicleWidgetList) widgetList).sLpnType.setAdapter(
					new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, lpnTypes));
			((WidgetList.VehicleWidgetList) widgetList).etVehEn.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, historyRecord.vehicleSearchRecord.vehEn));
			((WidgetList.VehicleWidgetList) widgetList).etVehVin.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, historyRecord.vehicleSearchRecord.vehVin));
			((WidgetList.VehicleWidgetList) widgetList).etVehColor.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, historyRecord.vehicleSearchRecord.vehColor));
			((WidgetList.VehicleWidgetList) widgetList).etPplName.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, historyRecord.vehicleSearchRecord.pplName));
			((WidgetList.VehicleWidgetList) widgetList).etPplSex.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, historyRecord.vehicleSearchRecord.pplSex));
			((WidgetList.VehicleWidgetList) widgetList).etPplBirt.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, historyRecord.vehicleSearchRecord.pplBirt));
			((WidgetList.VehicleWidgetList) widgetList).etPplId.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, historyRecord.vehicleSearchRecord.pplId));
			((WidgetList.VehicleWidgetList) widgetList).etPplAdd.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, historyRecord.vehicleSearchRecord.pplAdd));
			((WidgetList.VehicleWidgetList) widgetList).etVehLpn.setThreshold(0);
			((WidgetList.VehicleWidgetList) widgetList).sLpnType
					.setOnItemSelectedListener(new OnItemSelectedListener() {
						@Override
						public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
							selectedLpnType = lpnTypes[position];
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {

						}
					});
			setDropEditListener(((WidgetList.VehicleWidgetList) widgetList).det);
			((WidgetList.VehicleWidgetList) widgetList).etVehEn.setThreshold(0);
			((WidgetList.VehicleWidgetList) widgetList).etVehVin.setThreshold(0);
			((WidgetList.VehicleWidgetList) widgetList).etVehColor.setThreshold(0);
			((WidgetList.VehicleWidgetList) widgetList).etPplName.setThreshold(0);
			((WidgetList.VehicleWidgetList) widgetList).etPplSex.setThreshold(0);
			((WidgetList.VehicleWidgetList) widgetList).etPplBirt.setThreshold(0);
			((WidgetList.VehicleWidgetList) widgetList).etPplId.setThreshold(0);
			((WidgetList.VehicleWidgetList) widgetList).etPplAdd.setThreshold(0);
		} else if (ApiConstants.GOODS_RESEARCH.equals(searchType)) {
			widgetList = new WidgetList.GoodsWidgetList();
			((WidgetList.GoodsWidgetList) widgetList).etIoiName = (AutoCompleteTextView) rootView
					.findViewById(R.id.et_goods_search_ioi_name);
			((WidgetList.GoodsWidgetList) widgetList).etIoiType = (AutoCompleteTextView) rootView
					.findViewById(R.id.et_goods_search_ioi_type);
			((WidgetList.GoodsWidgetList) widgetList).etPplName = (AutoCompleteTextView) rootView
					.findViewById(R.id.et_goods_search_ppl_name);
			((WidgetList.GoodsWidgetList) widgetList).etPplSex = (AutoCompleteTextView) rootView
					.findViewById(R.id.et_goods_search_ppl_sex);
			((WidgetList.GoodsWidgetList) widgetList).etPplBirt = (AutoCompleteTextView) rootView
					.findViewById(R.id.et_goods_search_ppl_birt);
			((WidgetList.GoodsWidgetList) widgetList).etPplId = (AutoCompleteTextView) rootView
					.findViewById(R.id.et_goods_search_ppl_id);
			((WidgetList.GoodsWidgetList) widgetList).etPplAdd = (AutoCompleteTextView) rootView
					.findViewById(R.id.et_goods_search_ppl_add);
//			((WidgetList.GoodsWidgetList) widgetList).btnSearch = (ButtonFlat) rootView
//					.findViewById(R.id.btn_goods_search_search);
//
//			((WidgetList.GoodsWidgetList) widgetList).btnSearch.setRippleSpeed(30);
//			((WidgetList.GoodsWidgetList) widgetList).btnSearch.setRippleColor(Color.parseColor("#8dbfe7"));

			((WidgetList.GoodsWidgetList) widgetList).etIoiName.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, historyRecord.goodsSearchRecord.ioiName));
			((WidgetList.GoodsWidgetList) widgetList).etIoiType.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, historyRecord.goodsSearchRecord.ioiType));
			((WidgetList.GoodsWidgetList) widgetList).etPplName.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, historyRecord.goodsSearchRecord.pplName));
			((WidgetList.GoodsWidgetList) widgetList).etPplSex.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, historyRecord.goodsSearchRecord.pplSex));
			((WidgetList.GoodsWidgetList) widgetList).etPplBirt.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, historyRecord.goodsSearchRecord.pplBirt));
			((WidgetList.GoodsWidgetList) widgetList).etPplId.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, historyRecord.goodsSearchRecord.pplId));
			((WidgetList.GoodsWidgetList) widgetList).etPplAdd.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, historyRecord.goodsSearchRecord.pplAdd));
			((WidgetList.GoodsWidgetList) widgetList).etIoiName.setThreshold(0);
			((WidgetList.GoodsWidgetList) widgetList).etIoiType.setThreshold(0);
			((WidgetList.GoodsWidgetList) widgetList).etPplName.setThreshold(0);
			((WidgetList.GoodsWidgetList) widgetList).etPplSex.setThreshold(0);
			((WidgetList.GoodsWidgetList) widgetList).etPplBirt.setThreshold(0);
			((WidgetList.GoodsWidgetList) widgetList).etPplId.setThreshold(0);
			((WidgetList.GoodsWidgetList) widgetList).etPplAdd.setThreshold(0);
		} else if (ApiConstants.CASE_RESEARCH.equals(searchType)) {
			widgetList = new WidgetList.CaseWidgetList();
			((WidgetList.CaseWidgetList) widgetList).etCaseNumber = (AutoCompleteTextView) rootView
					.findViewById(R.id.et_case_search_case_number);
			((WidgetList.CaseWidgetList) widgetList).etCaseName = (AutoCompleteTextView) rootView
					.findViewById(R.id.et_case_search_case_name);
			((WidgetList.CaseWidgetList) widgetList).etCaseType = (AutoCompleteTextView) rootView
					.findViewById(R.id.et_case_search_case_type);
			((WidgetList.CaseWidgetList) widgetList).etCaseUnit = (AutoCompleteTextView) rootView
					.findViewById(R.id.et_case_search_case_unit);
			((WidgetList.CaseWidgetList) widgetList).etPplName = (AutoCompleteTextView) rootView
					.findViewById(R.id.et_case_search_ppl_name);
			((WidgetList.CaseWidgetList) widgetList).etPoliceNumber = (AutoCompleteTextView) rootView
					.findViewById(R.id.et_case_search_police_number);
			((WidgetList.CaseWidgetList) widgetList).etSuspectPplName = (AutoCompleteTextView) rootView
					.findViewById(R.id.et_case_search_suspect_ppl_name);
			((WidgetList.CaseWidgetList) widgetList).etSuspectPplSex = (AutoCompleteTextView) rootView
					.findViewById(R.id.et_case_search_suspect_ppl_sex);
			((WidgetList.CaseWidgetList) widgetList).etSuspectPplBirt = (AutoCompleteTextView) rootView
					.findViewById(R.id.et_case_search_suspect_ppl_birt);
			((WidgetList.CaseWidgetList) widgetList).etSuspectPplId = (AutoCompleteTextView) rootView
					.findViewById(R.id.et_case_search_suspect_ppl_id);
			((WidgetList.CaseWidgetList) widgetList).etSuspectPplAdd = (AutoCompleteTextView) rootView
					.findViewById(R.id.et_case_search_suspect_ppl_add);
			((WidgetList.CaseWidgetList) widgetList).etVictimPplName = (AutoCompleteTextView) rootView
					.findViewById(R.id.et_case_search_victim_ppl_name);
			((WidgetList.CaseWidgetList) widgetList).etVictimPplSex = (AutoCompleteTextView) rootView
					.findViewById(R.id.et_case_search_victim_ppl_sex);
			((WidgetList.CaseWidgetList) widgetList).etVictimPplBirt = (AutoCompleteTextView) rootView
					.findViewById(R.id.et_case_search_victim_ppl_birt);
			((WidgetList.CaseWidgetList) widgetList).etVictimPplId = (AutoCompleteTextView) rootView
					.findViewById(R.id.et_case_search_victim_ppl_id);
			((WidgetList.CaseWidgetList) widgetList).etVictimPplAdd = (AutoCompleteTextView) rootView
					.findViewById(R.id.et_case_search_victim_ppl_add);
//			((WidgetList.CaseWidgetList) widgetList).btnSearch = (ButtonFlat) rootView
//					.findViewById(R.id.btn_case_search_search);
//
//			((WidgetList.CaseWidgetList) widgetList).btnSearch.setRippleSpeed(30);
//			((WidgetList.CaseWidgetList) widgetList).btnSearch.setRippleColor(Color.parseColor("#8dbfe7"));

			((WidgetList.CaseWidgetList) widgetList).etCaseNumber.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, historyRecord.caseSearchRecord.caseNumber));
			((WidgetList.CaseWidgetList) widgetList).etCaseName.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, historyRecord.caseSearchRecord.caseName));
			((WidgetList.CaseWidgetList) widgetList).etCaseType.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, historyRecord.caseSearchRecord.caseType));
			((WidgetList.CaseWidgetList) widgetList).etCaseUnit.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, historyRecord.caseSearchRecord.caseUnit));
			((WidgetList.CaseWidgetList) widgetList).etPplName.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, historyRecord.caseSearchRecord.pplName));
			((WidgetList.CaseWidgetList) widgetList).etPoliceNumber.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, historyRecord.caseSearchRecord.policeNumber));
			((WidgetList.CaseWidgetList) widgetList).etSuspectPplName.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, historyRecord.caseSearchRecord.suspectPplName));
			((WidgetList.CaseWidgetList) widgetList).etSuspectPplSex.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, historyRecord.caseSearchRecord.suspectPplSex));
			((WidgetList.CaseWidgetList) widgetList).etSuspectPplBirt.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, historyRecord.caseSearchRecord.suspectPplBirt));
			((WidgetList.CaseWidgetList) widgetList).etSuspectPplId.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, historyRecord.caseSearchRecord.suspectPplId));
			((WidgetList.CaseWidgetList) widgetList).etSuspectPplAdd.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, historyRecord.caseSearchRecord.suspectPplAdd));
			((WidgetList.CaseWidgetList) widgetList).etVictimPplName.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, historyRecord.caseSearchRecord.victimPplName));
			((WidgetList.CaseWidgetList) widgetList).etVictimPplSex.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, historyRecord.caseSearchRecord.victimPplSex));
			((WidgetList.CaseWidgetList) widgetList).etVictimPplBirt.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, historyRecord.caseSearchRecord.victimPplBirt));
			((WidgetList.CaseWidgetList) widgetList).etVictimPplId.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, historyRecord.caseSearchRecord.victimPplId));
			((WidgetList.CaseWidgetList) widgetList).etVictimPplAdd.setAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, historyRecord.caseSearchRecord.victimPplAdd));
			((WidgetList.CaseWidgetList) widgetList).etCaseNumber.setThreshold(0);
			((WidgetList.CaseWidgetList) widgetList).etCaseName.setThreshold(0);
			((WidgetList.CaseWidgetList) widgetList).etCaseType.setThreshold(0);
			((WidgetList.CaseWidgetList) widgetList).etCaseUnit.setThreshold(0);
			((WidgetList.CaseWidgetList) widgetList).etPplName.setThreshold(0);
			((WidgetList.CaseWidgetList) widgetList).etPoliceNumber.setThreshold(0);
			((WidgetList.CaseWidgetList) widgetList).etSuspectPplName.setThreshold(0);
			((WidgetList.CaseWidgetList) widgetList).etSuspectPplSex.setThreshold(0);
			((WidgetList.CaseWidgetList) widgetList).etSuspectPplBirt.setThreshold(0);
			((WidgetList.CaseWidgetList) widgetList).etSuspectPplId.setThreshold(0);
			((WidgetList.CaseWidgetList) widgetList).etSuspectPplAdd.setThreshold(0);
			((WidgetList.CaseWidgetList) widgetList).etVictimPplName.setThreshold(0);
			((WidgetList.CaseWidgetList) widgetList).etVictimPplSex.setThreshold(0);
			((WidgetList.CaseWidgetList) widgetList).etVictimPplBirt.setThreshold(0);
			((WidgetList.CaseWidgetList) widgetList).etVictimPplId.setThreshold(0);
			((WidgetList.CaseWidgetList) widgetList).etVictimPplAdd.setThreshold(0);
		}
	}

	private void initListener() {
//		if (ApiConstants.ONE_KEY_RESEARCH.equals(searchType)) {
//			((WidgetList.OnekeyWidgetList) widgetList).btnSearch.setOnClickListener(this);
//		} else if (ApiConstants.PERSON_RESEARCH.equals(searchType)) {
//			((WidgetList.PeopleWidgetList) widgetList).btnSearch.setOnClickListener(this);
//		} else if (ApiConstants.VEHICLE_RESEARCH.equals(searchType)) {
//			((WidgetList.VehicleWidgetList) widgetList).btnSearch.setOnClickListener(this);
//		} else if (ApiConstants.GOODS_RESEARCH.equals(searchType)) {
//			((WidgetList.GoodsWidgetList) widgetList).btnSearch.setOnClickListener(this);
//		} else if (ApiConstants.CASE_RESEARCH.equals(searchType)) {
//			((WidgetList.CaseWidgetList) widgetList).btnSearch.setOnClickListener(this);
//		}
	}

	private boolean getAllEmpty() {
		if (ApiConstants.ONE_KEY_RESEARCH.equals(searchType)) {
			return !TextUtils.isEmpty(((WidgetList.OnekeyWidgetList) widgetList).etKeyword.getText().toString());
		} else if (ApiConstants.PERSON_RESEARCH.equals(searchType)) {
			return !TextUtils.isEmpty(((WidgetList.PeopleWidgetList) widgetList).etPplName.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.PeopleWidgetList) widgetList).etPplSex.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.PeopleWidgetList) widgetList).etPplBirt.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.PeopleWidgetList) widgetList).etPplId.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.PeopleWidgetList) widgetList).etPplAdd.getText().toString());
		} else if (ApiConstants.VEHICLE_RESEARCH.equals(searchType)) {
			return !TextUtils.isEmpty(((WidgetList.VehicleWidgetList) widgetList).etVehLpn.getText().toString())

					|| !TextUtils.isEmpty(((WidgetList.VehicleWidgetList) widgetList).det.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.VehicleWidgetList) widgetList).etVehEn.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.VehicleWidgetList) widgetList).etVehVin.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.VehicleWidgetList) widgetList).etVehColor.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.VehicleWidgetList) widgetList).etPplName.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.VehicleWidgetList) widgetList).etPplSex.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.VehicleWidgetList) widgetList).etPplBirt.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.VehicleWidgetList) widgetList).etPplId.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.VehicleWidgetList) widgetList).etPplAdd.getText().toString());
		} else if (ApiConstants.GOODS_RESEARCH.equals(searchType)) {
			return !TextUtils.isEmpty(((WidgetList.GoodsWidgetList) widgetList).etIoiName.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.GoodsWidgetList) widgetList).etIoiType.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.GoodsWidgetList) widgetList).etPplName.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.GoodsWidgetList) widgetList).etPplSex.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.GoodsWidgetList) widgetList).etPplBirt.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.GoodsWidgetList) widgetList).etPplId.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.GoodsWidgetList) widgetList).etPplAdd.getText().toString());
		} else if (ApiConstants.CASE_RESEARCH.equals(searchType)) {
			return !TextUtils.isEmpty(((WidgetList.CaseWidgetList) widgetList).etCaseNumber.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.CaseWidgetList) widgetList).etCaseName.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.CaseWidgetList) widgetList).etCaseType.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.CaseWidgetList) widgetList).etCaseUnit.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.CaseWidgetList) widgetList).etPplName.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.CaseWidgetList) widgetList).etPoliceNumber.getText().toString())
					|| !TextUtils
							.isEmpty(((WidgetList.CaseWidgetList) widgetList).etSuspectPplName.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.CaseWidgetList) widgetList).etSuspectPplSex.getText().toString())
					|| !TextUtils
							.isEmpty(((WidgetList.CaseWidgetList) widgetList).etSuspectPplBirt.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.CaseWidgetList) widgetList).etSuspectPplId.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.CaseWidgetList) widgetList).etSuspectPplAdd.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.CaseWidgetList) widgetList).etVictimPplName.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.CaseWidgetList) widgetList).etVictimPplSex.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.CaseWidgetList) widgetList).etVictimPplBirt.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.CaseWidgetList) widgetList).etVictimPplId.getText().toString())
					|| !TextUtils.isEmpty(((WidgetList.CaseWidgetList) widgetList).etVictimPplAdd.getText().toString());
		}

		return false;
	}

	public String getSearchType() {
		return searchType;
	}

	public void search() {
		if (getAllEmpty()) // 如果提交搜索有内容则提交数据
		{
			if (Utils.isNetworkAvailable(getActivity())) {
				
				if (searchType.equals(ApiConstants.ONE_KEY_RESEARCH)) {
					String keyWord = ((WidgetList.OnekeyWidgetList) widgetList).etKeyword.getText().toString();
					if (!historyRecord.oneKeySearchRecord.keyWord.contains(keyWord))
						historyRecord.oneKeySearchRecord.keyWord.add(keyWord);
				} else if (searchType.equals(ApiConstants.PERSON_RESEARCH)) {
					String pplName = ((WidgetList.PeopleWidgetList) widgetList).etPplName.getText().toString();
					String pplId = ((WidgetList.PeopleWidgetList) widgetList).etPplId.getText().toString();
					String pplSex = ((WidgetList.PeopleWidgetList) widgetList).etPplSex.getText().toString();
					String pplBirt = ((WidgetList.PeopleWidgetList) widgetList).etPplBirt.getText().toString();
					String pplAdd = ((WidgetList.PeopleWidgetList) widgetList).etPplAdd.getText().toString();
					if (!historyRecord.peopleSearchRecord.pplName.contains(pplName))
						historyRecord.peopleSearchRecord.pplName.add(pplName);
					if (!historyRecord.peopleSearchRecord.pplId.contains(pplId))
						historyRecord.peopleSearchRecord.pplId.add(pplId);
					if (!historyRecord.peopleSearchRecord.pplSex.contains(pplSex))
						historyRecord.peopleSearchRecord.pplSex.add(pplSex);
					if (!historyRecord.peopleSearchRecord.pplBirt.contains(pplBirt))
						historyRecord.peopleSearchRecord.pplBirt.add(pplBirt);
					if (!historyRecord.peopleSearchRecord.pplAdd.contains(pplAdd))
						historyRecord.peopleSearchRecord.pplAdd.add(pplAdd);
				} else if (searchType.equals(ApiConstants.VEHICLE_RESEARCH)) {

					if (!TextUtils.isEmpty(((WidgetList.VehicleWidgetList) widgetList).det.getText().toString())
							&& TextUtils.isEmpty(
									((WidgetList.VehicleWidgetList) widgetList).etVehLpn.getText().toString())) {
						Toast.makeText(getActivity(), "请填写车牌后五位", Toast.LENGTH_SHORT).show();
						return;
					} else if (TextUtils.isEmpty(((WidgetList.VehicleWidgetList) widgetList).det.getText().toString())
							&& !TextUtils.isEmpty(
									((WidgetList.VehicleWidgetList) widgetList).etVehLpn.getText().toString())) {
						Toast.makeText(getActivity(), "请补全车牌前缀如，南宁，桂A等", Toast.LENGTH_SHORT).show();
						return;
					}

					String vehLpn = ((WidgetList.VehicleWidgetList) widgetList).etVehLpn.getText().toString();
					String vehEn = ((WidgetList.VehicleWidgetList) widgetList).etVehEn.getText().toString();
					String vehVin = ((WidgetList.VehicleWidgetList) widgetList).etVehVin.getText().toString();
					String vehColor = ((WidgetList.VehicleWidgetList) widgetList).etVehColor.getText().toString();
					String pplName = ((WidgetList.VehicleWidgetList) widgetList).etPplName.getText().toString();
					String pplSex = ((WidgetList.VehicleWidgetList) widgetList).etPplSex.getText().toString();
					String pplBirt = ((WidgetList.VehicleWidgetList) widgetList).etPplBirt.getText().toString();
					String pplId = ((WidgetList.VehicleWidgetList) widgetList).etPplId.getText().toString();
					String pplAdd = ((WidgetList.VehicleWidgetList) widgetList).etPplAdd.getText().toString();
					if (!historyRecord.vehicleSearchRecord.vehLpn.contains(vehLpn))
						historyRecord.vehicleSearchRecord.vehLpn.add(vehLpn);
					if (!historyRecord.vehicleSearchRecord.vehEn.contains(vehEn))
						historyRecord.vehicleSearchRecord.vehEn.add(vehEn);
					if (!historyRecord.vehicleSearchRecord.vehVin.contains(vehVin))
						historyRecord.vehicleSearchRecord.vehVin.add(vehVin);
					if (!historyRecord.vehicleSearchRecord.vehColor.contains(vehColor))
						historyRecord.vehicleSearchRecord.vehColor.add(vehColor);
					if (!historyRecord.vehicleSearchRecord.pplName.contains(pplName))
						historyRecord.vehicleSearchRecord.pplName.add(pplName);
					if (!historyRecord.vehicleSearchRecord.pplSex.contains(pplSex))
						historyRecord.vehicleSearchRecord.pplSex.add(pplSex);
					if (!historyRecord.vehicleSearchRecord.pplBirt.contains(pplBirt))
						historyRecord.vehicleSearchRecord.pplBirt.add(pplBirt);
					if (!historyRecord.vehicleSearchRecord.pplId.contains(pplId))
						historyRecord.vehicleSearchRecord.pplId.add(pplId);
					if (!historyRecord.vehicleSearchRecord.pplAdd.contains(pplAdd))
						historyRecord.vehicleSearchRecord.pplAdd.add(pplAdd);
				} else if (searchType.equals(ApiConstants.GOODS_RESEARCH)) {
					String ioiName = ((WidgetList.GoodsWidgetList) widgetList).etIoiName.getText().toString();
					String ioiType = ((WidgetList.GoodsWidgetList) widgetList).etIoiType.getText().toString();
					String pplName = ((WidgetList.GoodsWidgetList) widgetList).etPplName.getText().toString();
					String pplSex = ((WidgetList.GoodsWidgetList) widgetList).etPplSex.getText().toString();
					String pplBirt = ((WidgetList.GoodsWidgetList) widgetList).etPplBirt.getText().toString();
					String pplId = ((WidgetList.GoodsWidgetList) widgetList).etPplId.getText().toString();
					String pplAdd = ((WidgetList.GoodsWidgetList) widgetList).etPplAdd.getText().toString();
					if (!historyRecord.goodsSearchRecord.ioiName.contains(ioiName))
						historyRecord.goodsSearchRecord.ioiName.add(ioiName);
					if (!historyRecord.goodsSearchRecord.ioiType.contains(ioiType))
						historyRecord.goodsSearchRecord.ioiType.add(ioiType);
					if (!historyRecord.goodsSearchRecord.pplName.contains(pplName))
						historyRecord.goodsSearchRecord.pplName.add(pplName);
					if (!historyRecord.goodsSearchRecord.pplSex.contains(pplSex))
						historyRecord.goodsSearchRecord.pplSex.add(pplSex);
					if (!historyRecord.goodsSearchRecord.pplBirt.contains(pplBirt))
						historyRecord.goodsSearchRecord.pplBirt.add(pplBirt);
					if (!historyRecord.goodsSearchRecord.pplId.contains(pplId))
						historyRecord.goodsSearchRecord.pplId.add(pplId);
					if (!historyRecord.goodsSearchRecord.pplAdd.contains(pplAdd))
						historyRecord.goodsSearchRecord.pplAdd.add(pplAdd);
				} else if (searchType.equals(ApiConstants.CASE_RESEARCH)) {
					String caseNumber = ((WidgetList.CaseWidgetList) widgetList).etCaseNumber.getText().toString();
					String caseName = ((WidgetList.CaseWidgetList) widgetList).etCaseName.getText().toString();
					String caseType = ((WidgetList.CaseWidgetList) widgetList).etCaseType.getText().toString();
					String caseUnit = ((WidgetList.CaseWidgetList) widgetList).etCaseUnit.getText().toString();
					String pplName = ((WidgetList.CaseWidgetList) widgetList).etPplName.getText().toString();
					String policeNumber = ((WidgetList.CaseWidgetList) widgetList).etPoliceNumber.getText().toString();
					String suspectPplName = ((WidgetList.CaseWidgetList) widgetList).etSuspectPplName.getText()
							.toString();
					String suspectPplSex = ((WidgetList.CaseWidgetList) widgetList).etSuspectPplSex.getText()
							.toString();
					String suspectPplBirt = ((WidgetList.CaseWidgetList) widgetList).etSuspectPplBirt.getText()
							.toString();
					String suspectPplId = ((WidgetList.CaseWidgetList) widgetList).etSuspectPplId.getText().toString();
					String suspectPplAdd = ((WidgetList.CaseWidgetList) widgetList).etSuspectPplAdd.getText()
							.toString();
					String victimPplName = ((WidgetList.CaseWidgetList) widgetList).etVictimPplName.getText()
							.toString();
					String victimPplSex = ((WidgetList.CaseWidgetList) widgetList).etVictimPplSex.getText().toString();
					String victimPplBirt = ((WidgetList.CaseWidgetList) widgetList).etVictimPplBirt.getText()
							.toString();
					String victimPplId = ((WidgetList.CaseWidgetList) widgetList).etVictimPplId.getText().toString();
					String victimPplAdd = ((WidgetList.CaseWidgetList) widgetList).etVictimPplAdd.getText().toString();
					if (!historyRecord.caseSearchRecord.caseNumber.contains(caseNumber))
						historyRecord.caseSearchRecord.caseNumber.add(caseNumber);
					if (!historyRecord.caseSearchRecord.caseName.contains(caseName))
						historyRecord.caseSearchRecord.caseName.add(caseName);
					if (!historyRecord.caseSearchRecord.caseType.contains(caseType))
						historyRecord.caseSearchRecord.caseType.add(caseType);
					if (!historyRecord.caseSearchRecord.caseUnit.contains(caseUnit))
						historyRecord.caseSearchRecord.caseUnit.add(caseUnit);
					if (!historyRecord.caseSearchRecord.pplName.contains(pplName))
						historyRecord.caseSearchRecord.pplName.add(pplName);
					if (!historyRecord.caseSearchRecord.policeNumber.contains(policeNumber))
						historyRecord.caseSearchRecord.policeNumber.add(policeNumber);
					if (!historyRecord.caseSearchRecord.suspectPplName.contains(suspectPplName))
						historyRecord.caseSearchRecord.suspectPplName.add(suspectPplName);
					if (!historyRecord.caseSearchRecord.suspectPplSex.contains(suspectPplSex))
						historyRecord.caseSearchRecord.suspectPplSex.add(suspectPplSex);
					if (!historyRecord.caseSearchRecord.suspectPplBirt.contains(suspectPplBirt))
						historyRecord.caseSearchRecord.suspectPplBirt.add(suspectPplBirt);
					if (!historyRecord.caseSearchRecord.suspectPplId.contains(suspectPplId))
						historyRecord.caseSearchRecord.suspectPplId.add(suspectPplId);
					if (!historyRecord.caseSearchRecord.suspectPplAdd.contains(suspectPplAdd))
						historyRecord.caseSearchRecord.suspectPplAdd.add(suspectPplAdd);
					if (!historyRecord.caseSearchRecord.victimPplName.contains(victimPplName))
						historyRecord.caseSearchRecord.victimPplName.add(victimPplName);
					if (!historyRecord.caseSearchRecord.victimPplSex.contains(victimPplSex))
						historyRecord.caseSearchRecord.victimPplSex.add(victimPplSex);
					if (!historyRecord.caseSearchRecord.victimPplBirt.contains(victimPplBirt))
						historyRecord.caseSearchRecord.victimPplBirt.add(victimPplBirt);
					if (!historyRecord.caseSearchRecord.victimPplId.contains(victimPplId))
						historyRecord.caseSearchRecord.victimPplId.add(victimPplId);
					if (!historyRecord.caseSearchRecord.victimPplAdd.contains(victimPplAdd))
						historyRecord.caseSearchRecord.victimPplAdd.add(victimPplAdd);
				}
				new AsyncLoader().execute();
				IOUtils.writeObjectToFile(historyRecord, cacheFilePath);
				
			} else {
				Toast.makeText(getActivity(),"网络异常",Toast.LENGTH_SHORT).show();
			}
		} else
		// 如果提交搜索无内容则提示输入检索内容
		{
			Toast.makeText(getActivity(),"请输入至少一项",Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.btn_onekey_search_search:
//		case R.id.btn_person_search_search:
//		case R.id.btn_vehicle_search_search:
//		case R.id.btn_goods_search_search:
//		case R.id.btn_case_search_search: {
//			search();
//		}
//			break;
//		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initVar();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// 根据搜索类别，加载相应的搜索页面以及初始化相应的子组件
		if (ApiConstants.ONE_KEY_RESEARCH.equals(searchType)) {
			rootView = inflater.inflate(R.layout.fragment_onekey_search, container, false);
		} else if (ApiConstants.PERSON_RESEARCH.equals(searchType)) {
			rootView = inflater.inflate(R.layout.fragment_people_search, container, false);
		} else if (ApiConstants.VEHICLE_RESEARCH.equals(searchType)) {
			rootView = inflater.inflate(R.layout.fragment_vehicle_search, container, false);
		} else if (ApiConstants.GOODS_RESEARCH.equals(searchType)) {
			rootView = inflater.inflate(R.layout.fragment_goods_search, container, false);
		} else if (ApiConstants.CASE_RESEARCH.equals(searchType)) {
			rootView = inflater.inflate(R.layout.fragment_case_search, container, false);
		}

		initWidget(); // 根视图加载完毕后，初始化根视图中的组件
		initListener();

		return rootView;
	}

	private class AsyncLoader extends AsyncTask<String, Integer, Integer> {
		@Override
		protected Integer doInBackground(String... params) {
			String table = null;
			HashMap<String, String> map = new HashMap<String, String>();
			String xmlStr = null;

			if (ApiConstants.ONE_KEY_RESEARCH.equals(searchType)) {
				table = "XNGA_ONEKEY_QUERY ";
				map.put("QUERY_ONEKEY", ((WidgetList.OnekeyWidgetList) widgetList).etKeyword.getText().toString());
			} else if (ApiConstants.PERSON_RESEARCH.equals(searchType)) {
				table = "XNGA_PEOPLE_QUERY ";
				map.put("PPL_NAME", ((WidgetList.PeopleWidgetList) widgetList).etPplName.getText().toString());
				map.put("PPL_SEX", ((WidgetList.PeopleWidgetList) widgetList).etPplSex.getText().toString());
				map.put("PPL_BIRT", ((WidgetList.PeopleWidgetList) widgetList).etPplBirt.getText().toString());
				map.put("PPL_ID", ((WidgetList.PeopleWidgetList) widgetList).etPplId.getText().toString());
				map.put("PPL_ADD", ((WidgetList.PeopleWidgetList) widgetList).etPplAdd.getText().toString());
			} else if (ApiConstants.VEHICLE_RESEARCH.equals(searchType)) {
				table = "XNGA_VEH_QUERY ";
				map.put("VEH_LPN", ((WidgetList.VehicleWidgetList) widgetList).det.getText().toString()
						+ ((WidgetList.VehicleWidgetList) widgetList).etVehLpn.getText().toString().toUpperCase());
				map.put("VEH_EN",
						((WidgetList.VehicleWidgetList) widgetList).etVehEn.getText().toString().toUpperCase());
				map.put("VEH_VIN",
						((WidgetList.VehicleWidgetList) widgetList).etVehVin.getText().toString().toUpperCase());
				map.put("VEH_COLOR", ((WidgetList.VehicleWidgetList) widgetList).etVehColor.getText().toString());
				map.put("PPL_NAME", ((WidgetList.VehicleWidgetList) widgetList).etPplName.getText().toString());
				map.put("PPL_SEX", ((WidgetList.VehicleWidgetList) widgetList).etPplSex.getText().toString());
				map.put("PPL_BIRT", ((WidgetList.VehicleWidgetList) widgetList).etPplBirt.getText().toString());
				map.put("PPL_ID", ((WidgetList.VehicleWidgetList) widgetList).etPplId.getText().toString());
				map.put("PPL_ADD", ((WidgetList.VehicleWidgetList) widgetList).etPplAdd.getText().toString());
			} else if (ApiConstants.GOODS_RESEARCH.equals(searchType)) {
				table = "XNGA_IOI_QUERY ";
				map.put("IOI_NAME", ((WidgetList.GoodsWidgetList) widgetList).etIoiName.getText().toString());
				map.put("IOI_TYPE", ((WidgetList.GoodsWidgetList) widgetList).etIoiType.getText().toString());
				map.put("PPL_NAME", ((WidgetList.GoodsWidgetList) widgetList).etPplName.getText().toString());
				map.put("PPL_SEX", ((WidgetList.GoodsWidgetList) widgetList).etPplSex.getText().toString());
				map.put("PPL_BIRT", ((WidgetList.GoodsWidgetList) widgetList).etPplBirt.getText().toString());
				map.put("PPL_ID", ((WidgetList.GoodsWidgetList) widgetList).etPplId.getText().toString());
				map.put("PPL_ADD", ((WidgetList.GoodsWidgetList) widgetList).etPplAdd.getText().toString());
			} else if (ApiConstants.CASE_RESEARCH.equals(searchType)) {
				table = "XNGA_CASE_QUERY";
				map.put("CASE_NUMBER", ((WidgetList.CaseWidgetList) widgetList).etCaseNumber.getText().toString());
				map.put("CASE_NAME", ((WidgetList.CaseWidgetList) widgetList).etCaseName.getText().toString());
				map.put("CASE_TYPE", ((WidgetList.CaseWidgetList) widgetList).etCaseType.getText().toString());
				map.put("CASE_UNIT", ((WidgetList.CaseWidgetList) widgetList).etCaseUnit.getText().toString());
				map.put("CASE_PPL_NAME", ((WidgetList.CaseWidgetList) widgetList).etPplName.getText().toString());
				map.put("CASE_POLICE_NUMBER",
						((WidgetList.CaseWidgetList) widgetList).etPoliceNumber.getText().toString());
				map.put("SUSPECT_PPL_NAME",
						((WidgetList.CaseWidgetList) widgetList).etSuspectPplName.getText().toString());
				map.put("SUSPECT_PPL_SEX",
						((WidgetList.CaseWidgetList) widgetList).etSuspectPplSex.getText().toString());
				map.put("SUSPECT_PPL_BIRT",
						((WidgetList.CaseWidgetList) widgetList).etSuspectPplBirt.getText().toString());
				map.put("SUSPECT_PPL_ID", ((WidgetList.CaseWidgetList) widgetList).etSuspectPplId.getText().toString());
				map.put("SUSPECT_PPL_ADD",
						((WidgetList.CaseWidgetList) widgetList).etSuspectPplAdd.getText().toString());
				map.put("VICTIM_PPL_NAME",
						((WidgetList.CaseWidgetList) widgetList).etVictimPplName.getText().toString());
				map.put("VICTIM_PPL_SEX", ((WidgetList.CaseWidgetList) widgetList).etVictimPplSex.getText().toString());
				map.put("VICTIM_PPL_BIRT",
						((WidgetList.CaseWidgetList) widgetList).etVictimPplBirt.getText().toString());
				map.put("VICTIM_PPL_ID", ((WidgetList.CaseWidgetList) widgetList).etVictimPplId.getText().toString());
				map.put("VICTIM_PPL_ADD", ((WidgetList.CaseWidgetList) widgetList).etVictimPplAdd.getText().toString());
			}
			map.put("Switch", "0");
			xmlStr = XmlPackage.packageForSaveOrUpdate(map, new DocInfor("", table), false);
			try {
				resultData = PostHttp.PostXML(xmlStr);
				//System.out.println("search:"+resultData);
			} catch (Exception e) {
				e.printStackTrace();
				return -1;
			}

			return 0;
		}

		protected void onPostExecute(Integer result) {
			if (result == -1) {
				Toast.makeText(getActivity(),"服务异常",Toast.LENGTH_SHORT).show();
			} else {
				if (ApiConstants.REQUESTSUCCESS.equals(resultData.SUCCESS)) {
					String contentId = resultData.CONTENTID.get(0);
					// System.out.println("CONTENTID " + contentId);
					// 进入结果列表
					ResultFragment resultFragment = ResultFragment.newInstance(contentId, searchType, selectedLpnType);
					getActivity().getSupportFragmentManager().beginTransaction()
							.replace(R.id.content_frame, resultFragment).addToBackStack(null).commit();
				}
			}
		}
	}

	/**
	 * 标记接口；表示实现该接口的类是一个WidgetList类
	 */
	private interface WidgetList {
		public class OnekeyWidgetList implements WidgetList // 一键查询页面包含的所有组件
		{
			public AutoCompleteTextView etKeyword;
			//public ButtonFlat btnSearch;
		}

		public class PeopleWidgetList implements WidgetList // 人员查询页面包含的所有组件
		{
			// 人员信息输入框
			public AutoCompleteTextView etPplName;
			public AutoCompleteTextView etPplId;
			public AutoCompleteTextView etPplSex;
			public AutoCompleteTextView etPplBirt;
			public AutoCompleteTextView etPplAdd;
			//public ButtonFlat btnSearch;
		}

		public class VehicleWidgetList implements WidgetList // 车辆查询页面包含的所有组件
		{
			// 车身信息输入框
			public AutoCompleteTextView etVehLpn;
			public Spinner sLpnType;
			public DropEditText det;
			public AutoCompleteTextView etVehEn;
			public AutoCompleteTextView etVehVin;
			public AutoCompleteTextView etVehColor;

			// 车主信息输入框
			public AutoCompleteTextView etPplName;
			public AutoCompleteTextView etPplSex;
			public AutoCompleteTextView etPplBirt;
			public AutoCompleteTextView etPplId;
			public AutoCompleteTextView etPplAdd;

			// 搜索按钮
			//public ButtonFlat btnSearch;
		}

		public class GoodsWidgetList implements WidgetList // 物品查询页面包含的所有组件
		{
			// 物品信息输入框
			public AutoCompleteTextView etIoiName;
			public AutoCompleteTextView etIoiType;

			// 物品持有人信息输入框
			public AutoCompleteTextView etPplName;
			public AutoCompleteTextView etPplSex;
			public AutoCompleteTextView etPplBirt;
			public AutoCompleteTextView etPplId;
			public AutoCompleteTextView etPplAdd;

			//public ButtonFlat btnSearch;
		}

		public class CaseWidgetList implements WidgetList // 案件查询页面包含的所有组件
		{
			// 案件信息输入框
			public AutoCompleteTextView etCaseNumber;
			public AutoCompleteTextView etCaseName;
			public AutoCompleteTextView etCaseType;
			public AutoCompleteTextView etCaseUnit;
			public AutoCompleteTextView etPplName;
			public AutoCompleteTextView etPoliceNumber;

			// 嫌疑人信息输入框
			public AutoCompleteTextView etSuspectPplName;
			public AutoCompleteTextView etSuspectPplSex;
			public AutoCompleteTextView etSuspectPplBirt;
			public AutoCompleteTextView etSuspectPplId;
			public AutoCompleteTextView etSuspectPplAdd;

			// 被害人/报警人信息输入框
			public AutoCompleteTextView etVictimPplName;
			public AutoCompleteTextView etVictimPplSex;
			public AutoCompleteTextView etVictimPplBirt;
			public AutoCompleteTextView etVictimPplId;
			public AutoCompleteTextView etVictimPplAdd;

			//public ButtonFlat btnSearch;
		}
	}

	private void setDropEditListener(DropEditText et) {
		et.setAdapter(new BaseAdapter() {

			@SuppressWarnings("serial")
			private List<String> list = new ArrayList<String>() {
				{
					add("南宁");
					add("桂A");
					add("桂B");
					add("桂C");
					add("桂D");
					add("桂E");
					add("桂F");
					add("桂G");
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

}
