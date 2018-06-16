package com.yskj.jnga.module.fragment.office;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yskj.jnga.App;
import com.yskj.jnga.R;
import com.yskj.jnga.adapter.CommonAdapter;
import com.yskj.jnga.adapter.ViewHolder;
import com.yskj.jnga.module.activity.office.HolidaysActivity;
import com.yskj.jnga.network.xml.BaseTable;
import com.yskj.jnga.network.xml.DataOperation;
import com.yskj.jnga.utils.Utils;
import com.yskj.jnga.widget.AutoGridView;
import com.yskj.jnga.widget.anim.dialog.DialogLoading;
import com.yskj.jnga.widget.perpetual.CalendarAdapter;
import com.yskj.jnga.widget.picker.CustomDatePicker;

public class LeaveFormFragment extends Fragment implements OnClickListener {

	public static Context c;
	private HolidaysActivity mActivity;
	private View mContentView;
	private EditText et_unit_id, et_name_id, et_contact_name, et_contact_tel, et_relationship, et_contact_unit,
			et_destination, et_reason;
	private AutoGridView gv;
	private ListView lv;
	private TextView tv_work_date_id;
	private RadioGroup sex_type_layout, is_married_layout;
	private TextView btn_confirm;
	private BaseTable policeInfo;
	private AlertDialog loadingDialog;
	private AlertDialog ad, calendarAd;
	private ArrayList<String> typeList;
	private ArrayList<HolidayType> selectTypeList;
	private CommonAdapter<HolidayType> typeDetailAdapter;
	private LinearLayout include_type;

	// calendar
	private GestureDetector gestureDetector = null;
	private CalendarAdapter calV = null;
	private GridView gridView = null;
	private TextView topText = null;
	private static int jumpMonth = 0; // 每次滑动，增加或减去默认（即显示当前月）
	private static int jumpYear = 0; // 滑动跨越，则增加或减去,默认(即当前年)
	private int year_c = 0;
	private int month_c = 0;
	private int day_c = 0;
	private String currentDate = "";
	private TextView nextMonth; // 下一月文本框
	private TextView preMonth; // 上一月文本框

	public static LeaveFormFragment newInstance(Context c) {
		LeaveFormFragment.c = c;
		LeaveFormFragment fragment = new LeaveFormFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (HolidaysActivity) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.fragment_leave_form, null);
		findView();
		initWidget();
		return mContentView;
	}

	private void findView() {

		et_unit_id = (EditText) mContentView.findViewById(R.id.et_unit_id);
		et_name_id = (EditText) mContentView.findViewById(R.id.et_name_id);
		// male_type_id female_type_id none_type_id
		sex_type_layout = (RadioGroup) mContentView.findViewById(R.id.sex_type_layout);
		tv_work_date_id = (TextView) mContentView.findViewById(R.id.tv_work_date_id);
		tv_work_date_id.setOnClickListener(this);
		is_married_layout = (RadioGroup) mContentView.findViewById(R.id.is_married_layout);
		et_contact_name = (EditText) mContentView.findViewById(R.id.et_contact_name);
		et_contact_tel = (EditText) mContentView.findViewById(R.id.et_contact_tel);
		et_relationship = (EditText) mContentView.findViewById(R.id.et_relationship);
		et_contact_unit = (EditText) mContentView.findViewById(R.id.et_contact_unit);
		et_destination = (EditText) mContentView.findViewById(R.id.et_destination);
		gv = (AutoGridView) mContentView.findViewById(R.id.gv);
		lv = (ListView) mContentView.findViewById(R.id.lv);
		et_reason = (EditText) mContentView.findViewById(R.id.et_reason);
		btn_confirm = (TextView) mContentView.findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);

		include_type = (LinearLayout) mContentView.findViewById(R.id.include_type);

	}

	public void initWidget() {

		policeInfo = App.getInstance().getSpUtil().getPoliceInfo();
		et_unit_id.setText(policeInfo.getField("RESULT_POLICE_UINT"));
		et_name_id.setText(policeInfo.getField("RESULT_POLICE_NAME"));
		setRadioButtonChecked(new int[] { R.id.male_type_id, R.id.no_married_id });
		// 设置假期类型选择
		initHolidayTypeData();
		// 设置假期种类对应的时间段与法定日
		// initHolidayTypeDetailData();
		initHolidayTypeDetail();

	}

	/**
	 * 设置假期种类对应的时间段与法定日
	 */

	private void initHolidayTypeDetailData() {

		selectTypeList = new ArrayList<>();

		typeDetailAdapter = new CommonAdapter<HolidayType>(getActivity(), selectTypeList,R.layout.item_holiday_type_list) {

			@Override
			public void convert(ViewHolder viewHolder, final HolidayType bean) {
				viewHolder.setText(R.id.tv_id, String.valueOf(viewHolder.getPosition() + 1));
				viewHolder.setText(R.id.tv_type, Utils.formatText(bean.getTypeName(), 4));
				final TextView tv_holiday_start = viewHolder.getView(R.id.tv_holiday_start);
				final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				tv_holiday_start.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// initDatePicker(tv_holiday_start, bean,
						// 1).show(tv_holiday_start.getText().toString());
						showPerpetualCalendar(tv_holiday_start, bean, 1);
					}
				});
				final TextView tv_holiday_end = viewHolder.getView(R.id.tv_holiday_end);
				tv_holiday_end.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (TextUtils.isEmpty(tv_holiday_start.getText().toString())) {
							Toast.makeText(getActivity(), "请选择开始时间", Toast.LENGTH_SHORT).show();
						} else {
							// initDatePicker(tv_holiday_end, bean,
							// 2).show(tv_holiday_end.getText().toString());
							showPerpetualCalendar(tv_holiday_end, bean, 2);
						}

					}
				});
				final TextView tv_legal = viewHolder.getView(R.id.tv_legal);
				tv_legal.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (TextUtils.isEmpty(tv_holiday_start.getText().toString())) {
							Toast.makeText(getActivity(), "请选择开始时间", Toast.LENGTH_SHORT).show();
						} else if (TextUtils.isEmpty(tv_holiday_end.getText().toString())) {
							Toast.makeText(getActivity(), "请选择结束时间", Toast.LENGTH_SHORT).show();
						} else
							try {
								if (sdf.parse(tv_holiday_start.getText().toString())
										.after(sdf.parse(tv_holiday_end.getText().toString()))) {

									Toast.makeText(getActivity(), "结束时间应该晚于开始时间", Toast.LENGTH_SHORT).show();

								} else {
									showDialog(bean, tv_legal);
								}
							} catch (ParseException e) {

								e.printStackTrace();
							}
					}

				});

			}
		};

		lv.setAdapter(typeDetailAdapter);
	}

	/**
	 * 补全假期类型信息
	 */
	private void initHolidayTypeDetail() {
		selectTypeList = new ArrayList<>();
		typeDetailAdapter = new CommonAdapter<HolidayType>(getActivity(), selectTypeList,R.layout.item_holiday_type_detail) {

			@Override
			public void convert(ViewHolder viewHolder, final HolidayType bean) {
				viewHolder.setText(R.id.tv_holiday_type,
						String.valueOf(viewHolder.getPosition() + 1) + "." + bean.getTypeName() + ":");
				final TextView tv_holiday_start = viewHolder.getView(R.id.et_holiday_start);
				final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				tv_holiday_start.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// initDatePicker(tv_holiday_start, bean,
						// 1).show(tv_holiday_start.getText().toString());
						showPerpetualCalendar(tv_holiday_start, bean, 1);
					}
				});
				final TextView tv_holiday_end = viewHolder.getView(R.id.et_holiday_end);
				tv_holiday_end.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (TextUtils.isEmpty(tv_holiday_start.getText().toString())) {
							Toast.makeText(getActivity(), "请选择开始时间", Toast.LENGTH_SHORT).show();
						} else {
							// initDatePicker(tv_holiday_end, bean,
							// 2).show(tv_holiday_end.getText().toString());
							showPerpetualCalendar(tv_holiday_end, bean, 2);
						}

					}
				});
				final TextView tv_legal = viewHolder.getView(R.id.et_holiday_legal);
				tv_legal.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (TextUtils.isEmpty(tv_holiday_start.getText().toString())) {
							Toast.makeText(getActivity(), "请选择开始时间", Toast.LENGTH_SHORT).show();
						} else if (TextUtils.isEmpty(tv_holiday_end.getText().toString())) {
							Toast.makeText(getActivity(), "请选择结束时间", Toast.LENGTH_SHORT).show();
						} else
							try {
								if (sdf.parse(tv_holiday_start.getText().toString())
										.after(sdf.parse(tv_holiday_end.getText().toString()))) {

									Toast.makeText(getActivity(), "结束时间应该晚于开始时间", Toast.LENGTH_SHORT).show();

								} else {
									showDialog(bean, tv_legal);
								}
							} catch (ParseException e) {

								e.printStackTrace();
							}
					}

				});

			}
		};

		lv.setAdapter(typeDetailAdapter);
	}

	/**
	 * 设置假期类型选择
	 */
	@SuppressWarnings("serial")
	private void initHolidayTypeData() {

		typeList = new ArrayList<String>() {
			{
				add("公休假");
				add("公休补假");
				add("一年度探亲假");
				add("婚假");
				add("产假");
				add("事假");
				add("护理假");
				add("病假");
				add("四年度探亲假");
				add("丧假");
				add("轮休");
				add("其他");
			}
		};

		CommonAdapter<String> typeAdapter = new CommonAdapter<String>(getActivity(), typeList,R.layout.item_check_box_grid) {

			@Override
			public void convert(ViewHolder viewHolder, final String bean) {
				viewHolder.setText(R.id.cb_title, bean);
				CheckBox cb = viewHolder.getView(R.id.cb_title);
				cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

						if (buttonView.getText().equals("公休补假")) {
							if (Integer.valueOf(Utils.getNowTime("MM")) > 4) {
								buttonView.setChecked(false);
								Toast.makeText(mActivity, "4月份之后不允许公休补假", Toast.LENGTH_SHORT).show();
								return;// 超过4月份不允许公休补假
							}
						}

						if (isChecked) {
							HolidayType holidayType = new HolidayType();
							holidayType.setTypeName(bean);
							selectTypeList.add(holidayType);
						} else {

							// 解决remove闪退的问题
							Iterator<HolidayType> iterator = selectTypeList.iterator();
							while (iterator.hasNext()) {
								HolidayType holidayType = iterator.next();
								if (holidayType.getTypeName().equals(bean)) {
									iterator.remove();
								}
							}
							// for (HolidayType holidayType : selectTypeList) {
							//
							// if (holidayType.getTypeName().equals(bean)) {
							// selectTypeList.remove(holidayType);
							// }
							// }
						}
						typeDetailAdapter.notifyDataSetChanged();
						Utils.changeListViewHeight(lv);

						if (selectTypeList.size() != 0) {
							include_type.setVisibility(View.VISIBLE);
						} else {
							include_type.setVisibility(View.GONE);
						}

					}
				});

			}
		};

		gv.setAdapter(typeAdapter);
	}

	public class HolidayTask extends AsyncTask<String, Integer, Integer> {

		@Override
		protected void onPreExecute() {
			loadingDialog = DialogLoading.getProgressDialog(mActivity, "正在上传");
			loadingDialog.show();
		}

		@Override
		protected Integer doInBackground(String... params) {

			for (HolidayType holidayType : selectTypeList) {

				BaseTable holidayTable = new BaseTable();
				holidayTable.setTableName("JNGA_QJ_INFO_CKS");
				holidayTable.putField("QJ_PoliceNum", policeInfo.getField("RESULT_POLICE_NUM"));
				holidayTable.putField("QJ_PoliceName", policeInfo.getField("RESULT_POLICE_NAME"));
				holidayTable.putField("QJ_Punit", policeInfo.getField("RESULT_POLICE_UINT"));
				holidayTable.putField("QJ_CreateTime", Utils.getNetTimeByType("yyyy-MM-dd"));
				holidayTable.putField("QJ_PUnit_Use", holidayType.getLegalTime());
				holidayTable.putField("QJ_Sex", getRadioGroupVaule(sex_type_layout));
				holidayTable.putField("QJ_WTime", tv_work_date_id.getText().toString());
				holidayTable.putField("QJ_Marrage", getRadioGroupVaule(is_married_layout));
				holidayTable.putField("QJ_Address", et_destination.getText().toString());
				holidayTable.putField("QJ_STime", holidayType.getStartTime());
				holidayTable.putField("QJ_Etime", holidayType.getEndTime());
				holidayTable.putField("QJ_LName", et_contact_name.getText().toString());
				holidayTable.putField("QJ_LTel", et_contact_tel.getText().toString());
				holidayTable.putField("QJ_LRelation", et_relationship.getText().toString());
				holidayTable.putField("QJ_LUnit", et_contact_unit.getText().toString());
				holidayTable.putField("QJ_JType", holidayType.getTypeName());
				holidayTable.putField("QJ_Reason", et_reason.getText().toString());
				holidayTable.putField("QJ_ID", Utils.getNetTimeByType("yyyyMMddHHmm") + "SJ");
				if (!DataOperation.insertOrUpdateTable(holidayTable)) {
					return -1;
				}

			}

			return 2;

		}

		@Override
		protected void onPostExecute(Integer result) {
			if (loadingDialog.isShowing()) {
				loadingDialog.dismiss();
			}
			switch (result) {
			case -1:
			    Toast.makeText(getActivity(),"提交失败",Toast.LENGTH_SHORT).show();
				break;

			case 1:
				break;

			case 2:

				mActivity.initWedgit(3);

				break;

			default:
				break;
			}

		}

	}

	// 选择RadioButton的值
	public void setRadioButtonChecked(int rbs[]) {
		for (int rb : rbs) {
			((RadioButton) mContentView.findViewById(rb)).setChecked(true);
		}

	}

	// 选择RadioButton的值
	public String setRadioButtonChecked(int rb) {
		RadioButton rbv = (RadioButton) mContentView.findViewById(rb);
		rbv.setChecked(true);
		return rbv.getText().toString();

	}

	public String getRadioGroupVaule(RadioGroup rg) {
		RadioButton rb = (RadioButton) rg.findViewById(rg.getCheckedRadioButtonId());
		return rb.getText().toString();

	}

	private CustomDatePicker initDatePicker(final TextView tv, final HolidayType holidayType, final int flag) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, +30);
		calendar.add(Calendar.YEAR, -50);
		date = calendar.getTime();
		String now = sdf.format(date);
		calendar.add(Calendar.YEAR, +51);
		date = calendar.getTime();
		String future = sdf.format(date);
		tv.setText(Utils.getNowTime("yyyy-MM-dd"));

		CustomDatePicker customDatePicker = new CustomDatePicker(mActivity, new CustomDatePicker.ResultHandler() {
			@Override
			public void handle(String time) { // 回调接口，获得选中的时间
				tv.setText(time.substring(0, 10));
				if (flag == 1) {
					holidayType.setStartTime(time.substring(0, 10));
				} else if (flag == 2) {
					holidayType.setEndTime(time.substring(0, 10));
				} else if (flag == 3) {

				}
			}
		}, now, future); // 初始化日期格式请用：yyyy-MM-dd
							// HH:mm，否则不能正常运行
		customDatePicker.showSpecificTime(true); // 显示时分
		customDatePicker.setIsLoop(false); // 不允许循环滚动

		return customDatePicker;

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.tv_work_date_id:
			initDatePicker(tv_work_date_id, null, 0).show(tv_work_date_id.getText().toString());
			break;
		case R.id.btn_confirm:
			if (Utils.isNetworkAvailable(mActivity)) {
				if (TextUtils.isEmpty(et_unit_id.getText().toString())) {
					Toast.makeText(mActivity, "请填写申请人单位", Toast.LENGTH_SHORT).show();
				} else if (TextUtils.isEmpty(et_name_id.getText().toString())) {
					Toast.makeText(mActivity, "请填写申请人姓名", Toast.LENGTH_SHORT).show();
				} else if (TextUtils.isEmpty(tv_work_date_id.getText().toString())) {
					Toast.makeText(mActivity, "请填写工作时间", Toast.LENGTH_SHORT).show();
				} else if (TextUtils.isEmpty(et_contact_name.getText().toString())) {
					Toast.makeText(mActivity, "请填写联系人姓名", Toast.LENGTH_SHORT).show();
				} else if (TextUtils.isEmpty(et_contact_tel.getText().toString())) {
					Toast.makeText(mActivity, "请填写联系人电话", Toast.LENGTH_SHORT).show();
				} else if (TextUtils.isEmpty(et_relationship.getText().toString())) {
					Toast.makeText(mActivity, "请填写与联系人的关系", Toast.LENGTH_SHORT).show();
				} else if (TextUtils.isEmpty(et_contact_unit.getText().toString())) {
					Toast.makeText(mActivity, "请填写联系人所在单位", Toast.LENGTH_SHORT).show();
				} else if (TextUtils.isEmpty(et_destination.getText().toString())) {
					Toast.makeText(mActivity, "请填写前往地点", Toast.LENGTH_SHORT).show();
				} else if (selectTypeList.size() == 0) {
					Toast.makeText(mActivity, "请填写请假类别", Toast.LENGTH_SHORT).show();
				} else if (TextUtils.isEmpty(et_reason.getText().toString())) {
					Toast.makeText(mActivity, "请填写请假事由", Toast.LENGTH_SHORT).show();
				} else {
					boolean isComplete = true;
					for (HolidayType holidayType : selectTypeList) {
						if (TextUtils.isEmpty(holidayType.getStartTime())) {
							isComplete = false;
						} else if (TextUtils.isEmpty(holidayType.getEndTime())) {
							isComplete = false;
						}
					}
					if (isComplete) {
						new HolidayTask().execute();
					} else {
						Toast.makeText(mActivity, "请补全请假时间", Toast.LENGTH_SHORT).show();
					}

				}

			}
			break;

		default:
			break;
		}

	}

	/**
	 * 关于假期类型对应的属性信息
	 * 
	 * @author saber
	 *
	 */
	private class HolidayType {
		private String id;
		private String typeName;
		private String startTime;
		private String endTime;
		private String legalTime;

		public String getTypeName() {
			return typeName;
		}

		public void setTypeName(String typeName) {
			this.typeName = typeName;
		}

		public String getStartTime() {
			return startTime;
		}

		public void setStartTime(String startTime) {
			this.startTime = startTime;
		}

		public String getEndTime() {
			return endTime;
		}

		public void setEndTime(String endTime) {
			this.endTime = endTime;
		}

		public String getLegalTime() {
			return legalTime;
		}

		public void setLegalTime(String legalTime) {
			this.legalTime = legalTime;
		}

		/**
		 * 对于选择的日期进入赋值处理
		 * 
		 * @param time
		 * @return
		 */
		public boolean updateLegal(String time) {

			boolean isAdd = false;

			StringBuffer sb = new StringBuffer();

			if (!TextUtils.isEmpty(this.legalTime)) {
				String legalArray[] = this.legalTime.split(",");
				List<String> legalList = new ArrayList<>();
				legalList.addAll(Arrays.asList(legalArray));
				if (legalList.contains(time)) {
					legalList.remove(time);
					isAdd = false;
				} else {
					legalList.add(time);
					isAdd = true;
				}

				for (String legal : legalList) {
					sb.append(legal);
					sb.append(",");
				}

			} else {
				sb.append(time);
				sb.append(",");
				isAdd = true;
			}

			this.setLegalTime(sb.toString());
			return isAdd;

		}

		/**
		 * 验证当前时间段内是否有选择的法定节日
		 * 
		 * @param time
		 * @return
		 */
		public boolean isContainsLegal(String time) {

			if (TextUtils.isEmpty(this.legalTime)) {
				return false;
			} else {
				return this.legalTime.contains(time) ? true : false;
			}

		}

	}

	/**
	 * 根据开始时间以及结束时间选定法定节日
	 * 
	 * @param holidayType
	 * @param tv
	 */
	private void showDialog(final HolidayType holidayType, final TextView tv) {

		View container = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_select_grid, null);
		ad = new AlertDialog.Builder(getActivity()).setView(container).show();
		TextView tv_title_01 = (TextView) container.findViewById(R.id.tv_title_01);
		tv_title_01.setText("法定节假日可选区间");
		TextView tv_count = (TextView) container.findViewById(R.id.tv_count);
		ImageButton ib_close = (ImageButton) container.findViewById(R.id.ib_close);
		ib_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (ad != null && ad.isShowing()) {
					ad.cancel();
				}

			}
		});
		final ArrayList<String> legalList = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {

			legalList.add(holidayType.getStartTime());
			Date startDate = sdf.parse(holidayType.getStartTime());
			Date endDate = sdf.parse(holidayType.getEndTime());
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startDate);
			boolean bContinue = true;
			while (bContinue) {
				calendar.add(Calendar.DAY_OF_MONTH, 1);
				if (endDate.after(calendar.getTime())) {
					legalList.add(sdf.format(calendar.getTime()));
				} else {
					break;
				}
			}

			legalList.add(holidayType.getEndTime());
			tv_count.setText(String.valueOf(legalList.size()));

		} catch (ParseException e) {

			e.printStackTrace();
		}

		GridView gv_window = (GridView) container.findViewById(R.id.gv_window);
		CommonAdapter<String> legalAdapter = new CommonAdapter<String>(getActivity(), legalList,R.layout.item_window_feedback) {

			@Override
			public void convert(ViewHolder viewHolder, final String bean) {

				viewHolder.setText(R.id.tv_01, bean.substring(5));
				TextView tv_01 = viewHolder.getView(R.id.tv_01);

				LinearLayout ll_window_item = viewHolder.getView(R.id.ll_window_item);
				GradientDrawable gd = (GradientDrawable) ll_window_item.getBackground();
				if (!holidayType.isContainsLegal(bean.substring(5))) {
					gd.setColor(Color.parseColor("#f3f4f5"));
					tv_01.setTextColor(Color.parseColor("#999999"));

				} else {
					gd.setColor(Color.parseColor("#42a5bf"));// 蓝色
					tv_01.setTextColor(Color.parseColor("#FCFCFC"));

				}

			}
		};

		gv_window.setAdapter(legalAdapter);
		gv_window.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				GradientDrawable gd = (GradientDrawable) view.getBackground();
				TextView tv_01 = (TextView) view.findViewById(R.id.tv_01);

				if (holidayType.updateLegal(legalList.get(position).substring(5))) {
					gd.setColor(Color.parseColor("#42a5bf"));
					tv_01.setTextColor(Color.parseColor("#FCFCFC"));

				} else {
					gd.setColor(Color.parseColor("#f3f4f5"));
					tv_01.setTextColor(Color.parseColor("#999999"));

				}

			}
		});

		ad.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				//
				tv.setText(TextUtils.isEmpty(holidayType.getLegalTime()) ? "" : holidayType.getLegalTime().toString());

			}
		});

	}

	/**
	 * 入职时间未满一年不得请公休假和公休补假 5年<=工作时间<10年 请假不超过5天 10年<=工作时间<20年 请假不超过10天 20年<=工作时间
	 * 请假不超过15天
	 * 
	 * @param workTime
	 *            工作时间
	 * @param startTime
	 *            请假开始时间
	 * @param endTime
	 *            请假结束时间
	 */

	public void getHolidayCount(String workTime, String startTime, String endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			long millis1 = System.currentTimeMillis() - sdf.parse(workTime).getTime();
			int yearCount = (int) (millis1 / (1000 * 60 * 60 * 24 * 365));
			long millis2 = sdf.parse(startTime).getTime() - sdf.parse(endTime).getTime();
			int dayCount = (int) (millis2 / (1000 * 60 * 60 * 24));
			if (yearCount < 5) {

			} else if (5 <= yearCount && yearCount < 10) {

			} else if (10 <= yearCount && yearCount < 20) {

			} else if (yearCount >= 20) {

			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 显示万年历
	 */
	private void showPerpetualCalendar(final TextView tv, final HolidayType holidayType, final int flag) {

		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d", Locale.CHINA);
		currentDate = sdf.format(date); // 当期日期
		year_c = Integer.parseInt(currentDate.split("-")[0]);
		month_c = Integer.parseInt(currentDate.split("-")[1]);
		day_c = Integer.parseInt(currentDate.split("-")[2]);

		final View container = LayoutInflater.from(getActivity()).inflate(R.layout.calendar, null);
		calendarAd = new AlertDialog.Builder(getActivity()).setView(container).show();

		gestureDetector = new GestureDetector(getActivity(), new MyGestureListener());
		calV = new CalendarAdapter(getActivity(), getResources(), new Date(), jumpMonth, jumpYear, year_c, month_c,
				day_c);
		addGridView(container, tv, holidayType, flag);
		gridView.setAdapter(calV);

		topText = (TextView) container.findViewById(R.id.tv_month);
		addTextToTopTextView(topText);
		nextMonth = (TextView) container.findViewById(R.id.right_img);
		preMonth = (TextView) container.findViewById(R.id.left_img);
		Button confirmBtn = (Button) container.findViewById(R.id.confirmBtn);
		nextMonth.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addGridView(container, tv, holidayType, flag); // 添加gridView
				jumpMonth++; // 下一个月

				calV = new CalendarAdapter(mActivity, getResources(), new Date(), jumpMonth, jumpYear, year_c, month_c,
						day_c);
				gridView.setAdapter(calV);
				addTextToTopTextView(topText);
			}
		});

		preMonth.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addGridView(container, tv, holidayType, flag); // 添加gridView
				jumpMonth--; // 上一个月

				calV = new CalendarAdapter(mActivity, getResources(), new Date(), jumpMonth, jumpYear, year_c, month_c,
						day_c);
				gridView.setAdapter(calV);
				// gvFlag++;
				addTextToTopTextView(topText);
			}
		});

		confirmBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (calendarAd != null && calendarAd.isShowing()) {
					calendarAd.cancel();
				}

			}
		});

	}

	// 添加头部的年月闰哪月等信息
	public void addTextToTopTextView(TextView view) {
		StringBuffer textDate = new StringBuffer();
		textDate.append(calV.getShowYear()).append("年").append(calV.getShowMonth()).append("月").append("\t");
		view.setText(textDate);
		view.setTextColor(Color.WHITE);
		view.setTypeface(Typeface.DEFAULT_BOLD);
	}

	// 添加GridView
	private void addGridView(View view, final TextView tv, final HolidayType holidayType, final int flag) {

		gridView = (GridView) view.findViewById(R.id.gridview);
		gridView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.performClick();
				// 将GridView中的触摸事件回传给gestureDetector
				return gestureDetector.onTouchEvent(event);
			}
		});

		gridView.setOnItemClickListener(new OnItemClickListener() {
			// gridView中的每一个item的点击事件
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				// 点击任何item，得到这个item的日排除点击的是周日到周点击不响应
				// int startPosition = calV.getStartPositon();
				// int endPosition = calV.getEndPosition();
				// if (startPosition <= position + 7 && position <= endPosition
				// - 7) {
				String scheduleDay = calV.getDateByClickItem(position).split("\\.")[0]; // 这一天的阳历
				// String scheduleLunarDay =
				// calV.getDateByClickItem(position).split("\\.")[1];
				// //这一天的阴历
				String scheduleYear = calV.getShowYear();
				String scheduleMonth = calV.getShowMonth();
				String time = scheduleYear + "-" + scheduleMonth + "-" + scheduleDay;
				tv.setText(time);
				// 处理选择的日期
				if (flag == 1) {
					holidayType.setStartTime(time);
				} else if (flag == 2) {
					holidayType.setEndTime(time);
				} else if (flag == 3) {

				}
				// }

				if (calendarAd != null && calendarAd.isShowing()) {
					calendarAd.cancel();
				}
			}
		});
	}

	private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
		// 用户（轻触触摸屏后）松开，由1个MotionEvent ACTION_UP触发
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return super.onSingleTapUp(e);
		}

		// 用户长按触摸屏，由多个MotionEvent ACTION_DOWN触发
		@Override
		public void onLongPress(MotionEvent e) {
			super.onLongPress(e);
		}

		// 用户按下触摸屏，并拖动，MotionEvent ACTION_DOWN,
		// 多个ACTION_MOVE触发
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			return super.onScroll(e1, e2, distanceX, distanceY);
		}

		// 用户按下触摸屏快速移动后松开个MotionEvent ACTION_DOWN,
		// 多个ACTION_MOVE, 1个ACTION_UP触发
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			// int gvFlag = 0; //每次添加GridView到viewFlipper中时给的标记
			if (e1.getX() - e2.getX() > 120) {
				// 向左滑动
				nextMonth.performClick();
				return true;
			} else if (e1.getX() - e2.getX() < -120) {
				// 向右滑动
				preMonth.performClick();
				return true;
			}
			return false;
		}

		// 用户轻触触摸屏，尚未松开或拖动，由个MotionEvent ACTION_DOWN触发
		// 注意和onDown()的区别，强调的是没有松开或拖动的状 @Override
		public void onShowPress(MotionEvent e) {
			super.onShowPress(e);
		}

		// 用户轻触触摸屏，个MotionEvent ACTION_DOWN触发
		@Override
		public boolean onDown(MotionEvent e) {
			return super.onDown(e);
		}

		// 双击的第二下Touch down时触
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			return super.onDoubleTap(e);
		}

		// 双击的第二下Touch down和up都会触发，可用e.getAction()区分
		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			return super.onDoubleTapEvent(e);
		}

		// 点击稍微慢点不滑Touch Up:
		// onDown->onShowPress->onSingleTapUp->onSingleTapConfirmed
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			return super.onSingleTapConfirmed(e);
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (loadingDialog != null && loadingDialog.isShowing()) {
			loadingDialog.cancel();
		}
		if (ad != null && ad.isShowing()) {
			ad.cancel();
		}
		if (calendarAd != null && calendarAd.isShowing()) {
			calendarAd.cancel();
		}
	}

}
