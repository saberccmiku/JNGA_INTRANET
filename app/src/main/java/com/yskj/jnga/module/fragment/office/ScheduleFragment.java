package com.yskj.jnga.module.fragment.office;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yskj.jnga.R;
import com.yskj.jnga.module.activity.office.HolidaysActivity;


public class ScheduleFragment extends Fragment {
	
	public static Context c;
	private View mContentView;
	private HolidaysActivity mActivity;
	
	public static ScheduleFragment newInstance(Context c) {
		ScheduleFragment.c = c;
		ScheduleFragment fragment = new ScheduleFragment();
		return fragment;
	}

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.fragment_schedule, null);
		mActivity = (HolidaysActivity) getActivity();
	
		TextView tv_sqrq = (TextView) mContentView.findViewById(R.id.tv_sqrq);
		tv_sqrq.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		return mContentView;
	}
}
