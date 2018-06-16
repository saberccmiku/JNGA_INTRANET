package com.yskj.jnga.module.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yskj.jnga.App;
import com.yskj.jnga.R;
import com.yskj.jnga.module.activity.business.SearchInfoActivity;
import com.yskj.jnga.network.ApiConstants;
import com.yskj.jnga.network.xml.BaseTable;
import com.yskj.jnga.network.xml.DataParser;
import com.yskj.jnga.network.xml.DataSetList;
import com.yskj.jnga.network.xml.DocInfor;
import com.yskj.jnga.network.xml.PostHttp;
import com.yskj.jnga.network.xml.XmlPackage;
import com.yskj.jnga.utils.WaterMarkbd;
import com.yskj.jnga.widget.anim.dialog.DialogLoading;


/**
 * 
 * 查询结果页面； 根据上一个页面传入的flagId参数进行数据的查询，并将查询到的结果显示到视图列表中；
 * 
 */
public class ResultFragment extends Fragment {
	private View rootView; // Fragment的总视图
	private AlertDialog loadingDialog; // 提示对话框
	private ListView resultList; // Fragment中的ListView

	private ResultListAdapter resultListAdapter; // Fragment中的ListView使用的adapter
	private Timer updateDataTimer; // 用于更新数据刷新UI的Timer
	private TimerTask updateDataTask; // 用于更新数据刷新UI的TimerTask
	// private SreenStateReceiver sreenStateReceiver;

	private DataSetList resultData; // 查询的结果集
	private long searchTime; // Timer已执行的时间
	private String flagId; // 当前页面用以查询数据的关键值
	private String searchType; // 要查询数据的类型(人员/车辆/案件/物品...)
	private String selectLpnType; // 选择的车牌类型

	private final long SEARCH_PERIOD = 3; // Timer的执行周期
	private final long MAX_SEARCH_TIME = 90; // Timer的最大可执行时间，超时后默认为任务失败

	private Map<String, Integer> mIconsMap;

	public static ResultFragment newInstance(String flagId, String searchType, String selectLpnType) {
		ResultFragment fragment = new ResultFragment();
		fragment.flagId = flagId;
		fragment.searchType = searchType;
		fragment.selectLpnType = selectLpnType;
		return fragment;
	}

	private void initVar() {
		resultListAdapter = new ResultListAdapter();
		updateDataTimer = new Timer();
		// sreenStateReceiver = new SreenStateReceiver();
	}

	private void initSetting() {
		((SearchInfoActivity) getActivity()).isShowSearch(false);

		// registerReceiver();
	}

	private void unInitSetting() {
		if (updateDataTask != null)
			updateDataTask.cancel(); // 销毁Fragment时，结束Task
		if (loadingDialog != null) {
			loadingDialog.hide();
		}

		((SearchInfoActivity) getActivity()).isShowSearch(true);

		// unregisterReceiver();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initVar();
		initSetting();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unInitSetting();
		if (loadingDialog != null) {
			loadingDialog.dismiss();
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_search_result, container, false);

		mIconsMap = new HashMap<>();
		mIconsMap.put("系统通知", R.drawable.result_information);
		mIconsMap.put("人员查询", R.drawable.grade_information);
		mIconsMap.put("车辆查询", R.drawable.grade_information);
		mIconsMap.put("物品查询", R.drawable.grade_information);
		mIconsMap.put("案件查询", R.drawable.grade_information);
		// 添加水印
		LinearLayout ll_serach_result = (LinearLayout) rootView.findViewById(R.id.ll_serach_result);
		WaterMarkbd.setWaterMarkbd(getActivity(), ll_serach_result, App.getInstance().getSpUtil().getAccount());

		resultList = (ListView) rootView.findViewById(R.id.list_vehicle_result);
		resultList.setAdapter(resultListAdapter);
		resultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			// 若点击了结果列表项，则进入该项的详细信息页面Fragment
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// 若当前用户的权限值为99，则不能查看详情页面
//				if (AppData.getInstance().getSpUtil().getAuth().equals("99")) {
//					OverlayDisplayToast.globalShowShort(getActivity(), "你没有权限查看详情");
//					return;
//				}

				//L.i("resultListAdapter.resultListData.get(position)=" + resultListAdapter.resultListData.get(position));

				ResultDetailFragment resultDetailFragment =  ResultDetailFragment.newInstance(searchType,
						resultListAdapter.resultListData.get(position));
				getActivity().getSupportFragmentManager().beginTransaction()
						.replace(R.id.content_frame, resultDetailFragment).addToBackStack(null).commit();
			}
		});

		// 如果任务(更新数据刷新UI)还未开始执行，则执行任务(仅在所有View加载完毕后才执行，整个生命周期中只执行一次，获取到数据刷新UI后该Task就完成了它的使命，无需重复执行)
		if (updateDataTask == null) {
			updateDataTask = new UpdateDataTask();
			updateDataTimer.schedule(updateDataTask, 0, SEARCH_PERIOD * 1000);
			loadingDialog = DialogLoading.getProgressDialog(getActivity(), "查询中...\n(返回键取消)"); // 显示提示对话框
			loadingDialog.show();
			loadingDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					updateDataTask.cancel();
					// onBackPressed();
					getActivity().onBackPressed();
				}
			});
		}

		return rootView;
	}

	/**
	 * 用于为Fragment中的ListView填充数据的Adapter
	 */
	private class ResultListAdapter extends BaseAdapter {
		ArrayList<BaseTable> resultListData; // 当前Adapter使用的数据源

		public ResultListAdapter() {
			resultListData = new ArrayList<>(); // 初始化数据源
		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();

			// 更新adapter数据源时做一些额外操作
			if (ApiConstants.PERSON_RESEARCH.equals(searchType)) // 如果数据源是人员类型，则判断人员数据中，是否存在"在逃人员"，若存在，则给出1次提示
			{
				for (int i = 0; i < resultListData.size(); i++) {
					final BaseTable table = resultListData.get(i);
					String peopleRecord = table.getField("PPL_RECORD");
					if (peopleRecord.equals("在逃人员")) {
						AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
						dialog.setCancelable(false);
						dialog.setTitle("提示");
						dialog.setMessage(
								"发现在逃人员：" + table.getField("PPL_NAME") + "\n" + table.getField("PPL_ID"));
						dialog.setNegativeButton("确定", null);
						dialog.show();

						break;
					}
				}
			} else if (ApiConstants.VEHICLE_RESEARCH.equals(searchType)) {
				for (int i = 0; i < resultListData.size(); i++) {
					final BaseTable table = resultListData.get(i);
					String vehRecord = table.getField("VEH_RECORD");
					if (vehRecord.equals("疑似被盗")) {
						AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
						dialog.setCancelable(false);
						dialog.setTitle("提示");
						dialog.setMessage("发现可关联被盗车辆：" + table.getField("VEH_LPN"));
						dialog.setMessage(String.format("车牌号为：%s的" + table.getField("VEH_TYPE") + "有疑似被盗记录。",
								table.getField("VEH_LPN")));
						dialog.setNegativeButton("查看", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								ResultDetailFragment resultDetailFragment =  ResultDetailFragment.newInstance(searchType, table);
								getActivity().getSupportFragmentManager().beginTransaction()
										.replace(R.id.content_frame, resultDetailFragment).addToBackStack(null)
										.commit();
							}
						});
						dialog.setPositiveButton("关闭", null);
						dialog.show();

						break;
					}
				}
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (ApiConstants.ONE_KEY_RESEARCH.equals(searchType)) {
				VehicleViewHolder vvh;
				if (convertView == null) {
					convertView = LayoutInflater.from(getActivity()).inflate(R.layout.listitem_vehicle_result, parent,
							false);
					vvh = new VehicleViewHolder();
					vvh.carId = (TextView) convertView.findViewById(R.id.tv_vehicle_result_listitem_car_id);
					vvh.carIdType = (TextView) convertView.findViewById(R.id.tv_vehicle_result_listitem_caridtype);
					vvh.carType = (TextView) convertView.findViewById(R.id.tv_vehicle_result_listitem_cartype);
					vvh.ownerName = (TextView) convertView.findViewById(R.id.tv_vehicle_result_listitem_owner_name);
					vvh.ownerId = (TextView) convertView.findViewById(R.id.tv_vehicle_result_listitem_owner_id);
					vvh.vehRecord = (TextView) convertView.findViewById(R.id.tv_vehicle_result_listitem_veh_record);
					convertView.setTag(vvh);
				} else {
					vvh = (VehicleViewHolder) convertView.getTag();
				}

				String vehType = resultListData.get(position).getField("VEH_TYPE");
				String type = "";
				String carType;
				if (vehType.contains("临时")) {
					type = "临时";
					carType = "电单车";
				} else if (vehType.contains("正式")) {
					type = "正式";
					carType = "电单车";
				} else {
					carType = vehType;
				}

				vvh.carId.setText(resultListData.get(position).getField("VEH_LPN"));
				vvh.carIdType.setText(type);
				vvh.carType.setText(carType);
				vvh.ownerName.setText(resultListData.get(position).getField("PPL_NAME"));
				vvh.ownerId.setText(resultListData.get(position).getField("PPL_ID"));

				String vehRecord = resultListData.get(position).getField("VEH_RECORD");
				
				// 若vehRecord数据为"曾被盗"，手动强行修改为"被盗"
				if (vehRecord.equals("曾被盗")) {
					vvh.vehRecord.setText("被盗");
					vvh.vehRecord.setTextColor(0xFFFF0000);
				} else if (vehRecord.equals("疑似被盗")) {
					vvh.vehRecord.setText("疑似被盗");
					vvh.vehRecord.setTextColor(Color.parseColor("#FF00FF"));
				} else {
					vvh.vehRecord.setText(vehRecord);
					vvh.vehRecord.setTextColor(0xFF000000);
				}
			} else if (ApiConstants.PERSON_RESEARCH.equals(searchType)) {
				PersonViewHolder pvh;
				if (convertView == null) {
					convertView = LayoutInflater.from(getActivity()).inflate(R.layout.listitem_people_result, parent,
							false);
					pvh = new PersonViewHolder();
					pvh.name = (TextView) convertView.findViewById(R.id.tv_people_result_listitem_ppl_name);
					pvh.sex = (TextView) convertView.findViewById(R.id.tv_people_result_listitem_ppl_sex);
					pvh.id = (TextView) convertView.findViewById(R.id.tv_people_result_listitem_ppl_id);
					pvh.address = (TextView) convertView.findViewById(R.id.tv_people_result_listitem_ppl_address);
					pvh.record = (TextView) convertView.findViewById(R.id.tv_people_result_listitem_ppl_record);
					convertView.setTag(pvh);
				} else {
					pvh = (PersonViewHolder) convertView.getTag();
				}

				pvh.name.setText(resultListData.get(position).getField("PPL_NAME"));
				pvh.sex.setText(resultListData.get(position).getField("PPL_SEX"));
				pvh.id.setText(resultListData.get(position).getField("PPL_ID"));
				pvh.address.setText(resultListData.get(position).getField("PPL_ADD"));

				String record = resultListData.get(position).getField("PPL_RECORD");
				pvh.record.setText(record);
				// 如果非普通人员，则将身份显示为警告红色
				if (!record.equals("普通人员"))
					pvh.record.setTextColor(Color.RED);
				else
					pvh.record.setTextColor(Color.BLACK);
				// 服务器端有一个不正常数据",吸毒人员"，这里手动强行去掉逗号
				if (record.equals(",吸毒人员"))
					pvh.record.setText("吸毒人员");
			} else if (ApiConstants.VEHICLE_RESEARCH.equals(searchType)) {
				VehicleViewHolder vvh;
				if (convertView == null) {
					convertView = LayoutInflater.from(getActivity()).inflate(R.layout.listitem_vehicle_result, parent,
							false);
					vvh = new VehicleViewHolder();
					vvh.carId = (TextView) convertView.findViewById(R.id.tv_vehicle_result_listitem_car_id);
					vvh.carIdType = (TextView) convertView.findViewById(R.id.tv_vehicle_result_listitem_caridtype);
					vvh.carType = (TextView) convertView.findViewById(R.id.tv_vehicle_result_listitem_cartype);
					vvh.ownerName = (TextView) convertView.findViewById(R.id.tv_vehicle_result_listitem_owner_name);
					vvh.ownerId = (TextView) convertView.findViewById(R.id.tv_vehicle_result_listitem_owner_id);
					vvh.vehRecord = (TextView) convertView.findViewById(R.id.tv_vehicle_result_listitem_veh_record);
					convertView.setTag(vvh);
				} else {
					vvh = (VehicleViewHolder) convertView.getTag();
				}

				String vehType = resultListData.get(position).getField("VEH_TYPE");
				String type = "";
				String carType;
				if (vehType.contains("临时")) {
					type = "临时";
					carType = "电单车";
				} else if (vehType.contains("正式")) {
					type = "正式";
					carType = "电单车";
				} else {
					carType = vehType;
				}

				vvh.carId.setText(resultListData.get(position).getField("VEH_LPN"));
				vvh.carIdType.setText(type);
				vvh.carType.setText(carType);
				vvh.ownerName.setText(resultListData.get(position).getField("PPL_NAME"));
				vvh.ownerId.setText(resultListData.get(position).getField("PPL_ID"));

				String vehRecord = resultListData.get(position).getField("VEH_RECORD");
				// 若vehRecord数据为"曾被盗"，手动强行修改为"被盗"
				if (vehRecord.equals("曾被盗")) {
					vvh.vehRecord.setText("被盗");
					vvh.vehRecord.setTextColor(0xFFFF0000);
				}else if (vehRecord.equals("疑似被盗")) {
					vvh.vehRecord.setText("疑似被盗");
					vvh.vehRecord.setTextColor(Color.parseColor("#FF00FF"));
				}  else {
					vvh.vehRecord.setText(vehRecord);
					vvh.vehRecord.setTextColor(0xFF000000);
				}
			} else if (ApiConstants.GOODS_RESEARCH.equals(searchType)) {

			} else if (ApiConstants.CASE_RESEARCH.equals(searchType)) {
				CaseViewHolder cvh;
				if (convertView == null) {
					convertView = LayoutInflater.from(getActivity()).inflate(R.layout.listitem_case_result, parent,
							false);
					cvh = new CaseViewHolder();
					cvh.caseid = (TextView) convertView.findViewById(R.id.tv_case_result_listitem_caseid);
					cvh.ajstate = (TextView) convertView.findViewById(R.id.tv_case_result_listitem_ajstate);
					cvh.ajname = (TextView) convertView.findViewById(R.id.tv_case_result_listitem_ajname);
					cvh.chengdu = (TextView) convertView.findViewById(R.id.tv_case_result_listitem_chengdu);
					convertView.setTag(cvh);
				} else {
					cvh = (CaseViewHolder) convertView.getTag();
				}

				cvh.caseid.setText(resultListData.get(position).getField("RESULT_CASEID"));
				cvh.ajstate.setText(resultListData.get(position).getField("RESULT_AJSTATE"));
				cvh.ajname.setText(resultListData.get(position).getField("RESULT_AJNAME"));
				cvh.chengdu.setText(resultListData.get(position).getField("RESULT_CHENGDU"));
			}

			return convertView;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			return resultListData.get(position);
		}

		@Override
		public int getCount() {
			return resultListData.size();
		}

		class PersonViewHolder {
			TextView name;
			TextView sex;
			TextView id;
			TextView address;
			TextView record;
		}

		class VehicleViewHolder {
			TextView carId;
			TextView carIdType;
			TextView carType;
			TextView ownerName;
			TextView ownerId;
			TextView vehRecord;
		}

		class CaseViewHolder {
			TextView caseid;
			TextView ajstate;
			TextView ajname;
			TextView chengdu;
		}
	};

	/**
	 * 该Task用于为Fragment中的ListView的Adapter使用的数据源获取数据
	 */
	private class UpdateDataTask extends TimerTask {
		@Override
		public void run() {
			searchTime += SEARCH_PERIOD;
			new AsyncQuery().execute(); // 执行AsyncTask 异步查询数据
		}

		private class AsyncQuery extends AsyncTask<String, Integer, Integer> {

			String contentId = "";
			String table = "";
			String xmlStr = "";

			@Override
			protected void onPreExecute() {

			}

			protected Integer doInBackground(String... params) {
				try {

					// 拼接xml请求字符串
					contentId = flagId;
					if (ApiConstants.ONE_KEY_RESEARCH.equals(searchType)) {
						table = "XNGA_VEH_RESULT";
					} else if (ApiConstants.PERSON_RESEARCH.equals(searchType)) {
						table = "XNGA_PPL_RESULT";
					} else if (ApiConstants.VEHICLE_RESEARCH.equals(searchType)) {
						table = "XNGA_VEH_RESULT";
					} else if (ApiConstants.CASE_RESEARCH.equals(searchType)) {
						table = "XNGA_CASE_RESULT";
					} else if (ApiConstants.GOODS_RESEARCH.equals(searchType)) {

					}

					xmlStr = XmlPackage.packageSelect("from " + table + " where FLAG_ID='" + contentId + "'", "", "",
							"", "SEARCHYOUNGCONTENT", new DocInfor("", table), true, false);

					resultData = PostHttp.PostXML(xmlStr);
				} catch (Exception e) {
					e.printStackTrace();
					return -1;
				}

				return -1;
			}

			@Override
			protected void onPostExecute(Integer result) {
				//System.out.println("Task正在执行");

				if (ApiConstants.REQUESTSUCCESS.equals(resultData.SUCCESS)) {

					resultListAdapter.resultListData = DataParser.getTable(resultData,table);

					if (resultListAdapter.resultListData.size() != 0) // 若搜索到数据了
					{
						resultListAdapter.notifyDataSetChanged();
						updateDataTask.cancel();
						loadingDialog.hide();
						// 将查询的数据保存咋及本地数据库
						// new SaveResultThread(contentId, searchType,
						// selectLpnType,
						// resultListAdapter.resultListData.size()).start();

					} else if (searchTime > MAX_SEARCH_TIME) // 若搜索超时了
					{
						updateDataTask.cancel();
						loadingDialog.hide();
						searchTime = 0;
						Toast.makeText(getActivity(),"没有搜索到相应结果！",Toast.LENGTH_SHORT).show();
						//OverlayDisplayToast.globalShowLong(getActivity(), "没有搜索到相应结果！");

						// onBackPressed();
						getActivity().onBackPressed();

					}
				} else {
					updateDataTask.cancel();
					loadingDialog.hide();
					searchTime = 0;
					Toast.makeText(getActivity(),"请求有误，服务器返回结果失败！",Toast.LENGTH_SHORT).show();
					//OverlayDisplayToast.globalShowLong(getActivity(), "请求有误，服务器返回结果失败！");

					// onBackPressed();
					getActivity().onBackPressed();

				}
			}
		}
	}

}
