package com.yskj.jnga.module.fragment.office;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.yskj.jnga.R;
import com.yskj.jnga.adapter.CommonAdapter;
import com.yskj.jnga.adapter.ViewHolder;
import com.yskj.jnga.module.activity.office.OfficialVehiclesActivity;

public class OfficialVehiclesMenuFragment extends Fragment {

	private View mContentView;
	private ListView lv_menu_list;
	private ArrayList<Integer> menuIds;
	private OfficialVehiclesActivity ova;

	public static OfficialVehiclesMenuFragment newInstance() {
		return new OfficialVehiclesMenuFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.fragment_menu_list, null);
		findView();
		initWidget();
		return mContentView;
	}

	@Override
	public void onAttach(Activity activity) {
		ova = (OfficialVehiclesActivity) activity;
		super.onAttach(activity);
	}

	private void findView() {
		lv_menu_list = (ListView) mContentView.findViewById(R.id.lv_menu_list);

	}

	private void initWidget() {
		menuIds = new ArrayList<>();
		int[] HolidayMenuList = new int[] { R.drawable.ldsp, R.drawable.wdsq, R.drawable.jcdj };
		for (int resourceId : HolidayMenuList) {
			menuIds.add(resourceId);
		}

		CommonAdapter<Integer> adapter = new CommonAdapter<Integer>(getActivity(), menuIds, R.layout.item_menu_list) {

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
					ova.status = 1;
					ova.initWedgit(1);
					break;
				case R.drawable.wdsq:
					ova.status = 2;
					ova.initWedgit(2);
					break;
				case R.drawable.jcdj:
					ova.status = 3;
					ova.initWedgit(1);
					break;
				default:
					break;
				}

			}
		});

	}

}
