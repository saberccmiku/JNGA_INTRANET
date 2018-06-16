package com.yskj.jnga.module.fragment.office;

import java.util.ArrayList;
import java.util.Arrays;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yskj.jnga.R;
import com.yskj.jnga.adapter.CommonAdapter;
import com.yskj.jnga.adapter.ViewHolder;
import com.yskj.jnga.module.activity.business.PoliceStatusActivity;
import com.yskj.jnga.module.activity.office.HolidaysActivity;
import com.yskj.jnga.widget.search.SearchAdapter;
import com.yskj.jnga.widget.search.SharedPreference;
import com.yskj.jnga.widget.search.Utils;

public class MenuListFragment extends Fragment {

	public static Context c;
	private View mContentView;
	private ListView lv_menu_list;
	private ArrayList<Integer> menuIds;
	private HolidaysActivity ha;

	public static MenuListFragment newInstance(Context c) {
		MenuListFragment.c = c;
		MenuListFragment fragment = new MenuListFragment();
		return fragment;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.ha = (HolidaysActivity) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.fragment_menu_list, null);
		findView();
		initWidget();
		return mContentView;
	}

	private void findView() {
		lv_menu_list = (ListView) mContentView.findViewById(R.id.lv_menu_list);

	}

	private void initWidget() {
		menuIds = new ArrayList<>();
		int[] HolidayMenuList = new int[] { R.drawable.ldsp, R.drawable.wdsq, R.drawable.ryqx };
		for (int resourceId : HolidayMenuList) {
			menuIds.add(resourceId);
		}

		CommonAdapter<Integer> adapter = new CommonAdapter<Integer>(c, menuIds,R.layout.item_menu_list) {

			@Override
			public void convert(ViewHolder viewHolder, Integer bean) {
				viewHolder.setBackgroundResource(R.id.item_iv_menu_list, bean);
			}
		};

		lv_menu_list.setAdapter(adapter);
		lv_menu_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				switch (menuIds.get(position)) {
				case R.drawable.ldsp:
					ha.status = 1;
					ha.initWedgit(1);
					break;
				case R.drawable.wdsq:
					ha.status = 0;
					position = 200;
					ha.initWedgit(0);
					break;
				case R.drawable.ryqx:
					loadToolBarSearch(getActivity(), view);
					break;
				default:
					break;
				}

			}
		});

	}

	/**
	 * 加载搜索栏目
	 * 
	 * @param activity
	 */
	public void loadToolBarSearch(final Activity activity, final View positionView) {

		ArrayList<String> countryStored = SharedPreference.loadList(activity, Utils.PREFS_NAME, Utils.KEY_PEOPLE);
		View view = activity.getLayoutInflater().inflate(R.layout.view_toolbar_search, null);
		LinearLayout parentToolbarSearch = (LinearLayout) view.findViewById(R.id.parent_toolbar_search);
		ImageView imgToolBack = (ImageView) view.findViewById(R.id.img_tool_back);
		final EditText edtToolSearch = (EditText) view.findViewById(R.id.edt_tool_search);
		ImageView img_tool_alarm = (ImageView) view.findViewById(R.id.img_tool_alarm);
		img_tool_alarm.setVisibility(View.GONE);
		ImageView imgToolMic = (ImageView) view.findViewById(R.id.img_tool_mic);
		final ListView listSearch = (ListView) view.findViewById(R.id.list_search);
		final TextView txtEmpty = (TextView) view.findViewById(R.id.txt_empty);

		Utils.setListViewHeightBasedOnChildren(listSearch);

		edtToolSearch.setHint("输入警号查警员询最近信息");

		final Dialog toolbarSearchDialog = new Dialog(activity, R.style.MaterialSearch);
		toolbarSearchDialog.setContentView(view);
		toolbarSearchDialog.setCancelable(false);
		toolbarSearchDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		toolbarSearchDialog.getWindow().setGravity(Gravity.BOTTOM);
		toolbarSearchDialog.show();

		toolbarSearchDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

		countryStored = (countryStored != null && countryStored.size() > 0) ? countryStored : new ArrayList<String>();
		final SearchAdapter searchAdapter = new SearchAdapter(activity, countryStored, false);

		listSearch.setVisibility(View.VISIBLE);
		listSearch.setAdapter(searchAdapter);

		listSearch.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

				String country = String.valueOf(adapterView.getItemAtPosition(position));
				SharedPreference.addList(activity, Utils.PREFS_NAME, Utils.KEY_PEOPLE, country);
				edtToolSearch.setText(country);
				listSearch.setVisibility(View.GONE);

			}
		});
		edtToolSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

				if (!TextUtils.isEmpty(edtToolSearch.getText().toString().trim())) {

					if (actionId == EditorInfo.IME_ACTION_SEARCH) {
						// 先隐藏键盘
						((InputMethodManager) edtToolSearch.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
								.hideSoftInputFromWindow(positionView.getWindowToken(),
										InputMethodManager.HIDE_NOT_ALWAYS);

						// enter AlarmSearchActivity information

						if (Utils.isConnectedNetwork(activity)) {

							activity.startActivity(new Intent(activity, PoliceStatusActivity.class)
									.putExtra("PoliceStatusActivity", v.getText().toString()));
							return true;
						}
					}
				} else {
					Toast.makeText(activity, "搜索内容不能为空", Toast.LENGTH_SHORT).show();
					return false;
				}

				return false;
			}

		});
		edtToolSearch.addTextChangedListener(new TextWatcher() {

			private ArrayList<String> mCountries;

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

				String[] country = activity.getResources().getStringArray(R.array.people_array);
				mCountries = new ArrayList<>(Arrays.asList(country));
				listSearch.setVisibility(View.VISIBLE);
				searchAdapter.updateList(mCountries, true);

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				ArrayList<String> filterList = new ArrayList<>();
				boolean isNoData = false;
				if (s.length() > 0) {
					for (int i = 0; i < mCountries.size(); i++) {
						if (mCountries.get(i).toLowerCase().startsWith(s.toString().trim().toLowerCase())) {
							filterList.add(mCountries.get(i));
							listSearch.setVisibility(View.VISIBLE);
							searchAdapter.updateList(filterList, true);
							isNoData = true;
						}
					}
					if (!isNoData) {
						listSearch.setVisibility(View.GONE);
						txtEmpty.setVisibility(View.VISIBLE);
						txtEmpty.setText("No data found");
					}
				} else {
					listSearch.setVisibility(View.GONE);
					txtEmpty.setVisibility(View.GONE);
				}

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		imgToolBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				toolbarSearchDialog.dismiss();
			}
		});

		imgToolMic.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				edtToolSearch.setText("");

			}
		});

	}

}
