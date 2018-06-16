package com.yskj.jnga.module.activity.office;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.Toast;

import com.yskj.jnga.App;
import com.yskj.jnga.R;
import com.yskj.jnga.module.base.RxBaseActivity;
import com.yskj.jnga.module.fragment.office.HolidayResultListFragment;
import com.yskj.jnga.module.fragment.office.LeaveFormFragment;
import com.yskj.jnga.module.fragment.office.MenuListFragment;
import com.yskj.jnga.module.fragment.office.ScheduleFragment;
import com.yskj.jnga.network.ApiConstants;
import com.yskj.jnga.task.QueryPoliceInfoTask;
import com.yskj.jnga.utils.SpUtil;

import java.lang.ref.WeakReference;

public class HolidaysActivity extends RxBaseActivity implements OnClickListener {

	private LeaveFormFragment mLeaveFormFragment;
	private MenuListFragment mMenuListFragment;
	private HolidayResultListFragment mHolidayResultListFragment;
	private ScheduleFragment mScheduleFragment;
	private ImageButton backBtn;
	private ImageView btn_lmjclb_add;
	public int status = 2;// 0是请假报告单 1是审核 2是菜单
	public SpUtil mSpUtil;
	public int position = 200;
	public ImageView img;
	public MyHandler mHandler;


	@Override
	public int getLayoutId() {
		return R.layout.activity_holiday;
	}

	@Override
	public void initViews(Bundle savedInstanceState) {
		findView();
		mHandler = new MyHandler(this);
		Intent intent = getIntent();
		if (intent.getAction() != null) {
			if (intent.getAction().equals("qjsp_message")) {
				status = intent.getIntExtra("qjsp_message", 0);
			} else if (intent.getAction().equals("HolidayResultList")) {
				status = 0;
				position = intent.getIntExtra("HolidayResultList", 200);
			}
		}

		initWedgit(status);
	}

	@Override
	public void initToolBar() {

	}

	@Override
	public void onAttachFragment(android.support.v4.app.Fragment fragment) {
		// TODO Auto-generated method stub
		super.onAttachFragment(fragment);

		if (mLeaveFormFragment == null && fragment instanceof LeaveFormFragment) {
			mLeaveFormFragment = (LeaveFormFragment) fragment;
		} else if (mMenuListFragment == null && fragment instanceof MenuListFragment) {
			mMenuListFragment = (MenuListFragment) fragment;
		} else if (mHolidayResultListFragment == null && fragment instanceof HolidayResultListFragment) {
			mHolidayResultListFragment = (HolidayResultListFragment) fragment;
		} else if (mScheduleFragment == null && fragment instanceof ScheduleFragment) {
			mScheduleFragment = (ScheduleFragment) fragment;
		}
	}

	public void initWedgit(int status) {

		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();

		hideAllFragments(transaction);

		switch (status) {
		case 0:
			if (mLeaveFormFragment == null) {
				mLeaveFormFragment = LeaveFormFragment.newInstance(this);
			}
			transaction.replace(R.id.content_frame, mLeaveFormFragment).commit();
			break;
		case 1:
			if (mHolidayResultListFragment == null) {
				mHolidayResultListFragment = HolidayResultListFragment.newInstance(this);
			}
			transaction.replace(R.id.content_frame, mHolidayResultListFragment).commit();
			break;

		case 2:
			if (mMenuListFragment == null) {
				mMenuListFragment = MenuListFragment.newInstance(this);
			}
			transaction.replace(R.id.content_frame, mMenuListFragment).commit();
			break;
		case 3:
			if (mScheduleFragment == null) {
				mScheduleFragment = ScheduleFragment.newInstance(this);
			}
			transaction.replace(R.id.content_frame, mScheduleFragment).commit();
			break;

		default:
			break;
		}

	}

	private void findView() {
		mSpUtil = App.getInstance().getSpUtil();
		Intent intent = getIntent();

		if (intent.getAction() != null) {
			if (intent.getAction().equals("HolidayResultList")) {
				position = intent.getIntExtra("HolidayResultList", 100);
			}

		}

		img = (ImageView) findViewById(R.id.qjbg_title);
		img.setImageResource(R.drawable.qjbg_title);
		backBtn = (ImageButton) findViewById(R.id.btn_lmjclb_back);
		backBtn.setOnClickListener(this);
		btn_lmjclb_add = (ImageView) findViewById(R.id.btn_lmjclb_add);
		btn_lmjclb_add.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_lmjclb_back:

			// 回退到上级activity界面
			if (this.getSupportFragmentManager().getBackStackEntryCount() <= 0) {
				this.finish();
			} else {
				// 返回上级fragment
				this.getSupportFragmentManager().popBackStack();

			}

			break;
		case R.id.btn_lmjclb_add:
			showPopUp(findViewById(R.id.btn_lmjclb_add));
			break;
		case R.id.vg_qjsq_spcx:
			if (mSpUtil.getPoliceInfo() == null) {
			    Toast.makeText(this,"正在加载数据请稍后再试",Toast.LENGTH_SHORT).show();
				new QueryPoliceInfoTask().setPoliceInfo(ApiConstants.USER_ID);
			} else {
				String policeType = mSpUtil.getPoliceInfo().getField("RESULT_POLICE_DISTRICT");
				if (policeType.equals("民警") || policeType.equals("协警")) {
                    Toast.makeText(this,"对不起,您没有相关权限",Toast.LENGTH_SHORT).show();
				} else {
					status = 1;
					initWedgit(status);
				}
			}

			break;
		case R.id.vg_qjsq_jdcx:
			status = 5;
			initWedgit(1);
			break;
		default:
			break;
		}

	}

	private void showPopUp(View v) {
		LayoutInflater inflater = LayoutInflater.from(this);
		View v_rootView = inflater.inflate(R.layout.dialog_rcbg, null);
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		// PopupWindow popWindow = new PopupWindow(v_rootView, size.x / 3,
		// LayoutParams.WRAP_CONTENT, true);
		PopupWindow popWindow = new PopupWindow(v_rootView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		popWindow.setBackgroundDrawable(new ColorDrawable());
		popWindow.setOutsideTouchable(true);
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.alpha = 0.8f;
		getWindow().setAttributes(params);
		popWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				WindowManager.LayoutParams params = getWindow().getAttributes();
				params.alpha = 1.0f;
				getWindow().setAttributes(params);
			}
		});
		popWindow.showAsDropDown(v);

		ViewGroup vg_qjsq_spcx = (ViewGroup) v_rootView.findViewById(R.id.vg_qjsq_spcx);
		ViewGroup vg_qjsq_jdcx = (ViewGroup) v_rootView.findViewById(R.id.vg_qjsq_jdcx);

		vg_qjsq_spcx.setOnClickListener(this);
		vg_qjsq_jdcx.setOnClickListener(this);

	}

	private void hideAllFragments(FragmentTransaction ft) {
		if (mLeaveFormFragment != null) {
			ft.hide(mLeaveFormFragment);
		}

		if (mMenuListFragment != null) {
			ft.hide(mMenuListFragment);
		}

		if (mHolidayResultListFragment != null) {
			ft.hide(mHolidayResultListFragment);
		}

		if (mScheduleFragment != null) {
			ft.hide(mScheduleFragment);
		}
	}

	/**
	 * 消息队列
	 */

	public static class MyHandler extends Handler {
		WeakReference<HolidaysActivity> mActivityReference;

		MyHandler(HolidaysActivity activity) {
			mActivityReference = new WeakReference<HolidaysActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {

			HolidaysActivity activity = mActivityReference.get();
			switch (msg.what) {
			case 1:
				break;
			case 2:
				if (activity.mHolidayResultListFragment.pd != null
						&& activity.mHolidayResultListFragment.pd.isShowing()) {
					activity.mHolidayResultListFragment.pd.cancel();
				}

				Toast.makeText(activity, "访问服务失败", Toast.LENGTH_SHORT).show();
				break;

			case 3:

				if (activity.mHolidayResultListFragment.pd != null
						&& activity.mHolidayResultListFragment.pd.isShowing()) {

					activity.mHolidayResultListFragment.pd.cancel();
				}
				activity.mHolidayResultListFragment.initWedgit();
				break;
			case 4:
				if (activity.mHolidayResultListFragment.pd != null
						&& activity.mHolidayResultListFragment.pd.isShowing()) {
					activity.mHolidayResultListFragment.pd.cancel();
				}

				Toast.makeText(activity, "未查到有效的数据", Toast.LENGTH_SHORT).show();

				break;

			default:
				break;
			}
		}
	}
	


}
