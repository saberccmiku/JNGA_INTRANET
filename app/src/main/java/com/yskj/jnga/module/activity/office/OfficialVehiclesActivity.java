package com.yskj.jnga.module.activity.office;

import java.lang.ref.WeakReference;
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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.PopupWindow.OnDismissListener;

import com.yskj.jnga.R;
import com.yskj.jnga.entity.OfficialVehForm;
import com.yskj.jnga.module.base.RxBaseActivity;
import com.yskj.jnga.module.fragment.office.OfficialVehApplyFragment;
import com.yskj.jnga.module.fragment.office.OfficialVehApproveFragment;
import com.yskj.jnga.module.fragment.office.OfficialVehListFragment;
import com.yskj.jnga.module.fragment.office.OfficialVehProgressFragment;
import com.yskj.jnga.module.fragment.office.OfficialVehReturnFragment;
import com.yskj.jnga.module.fragment.office.OfficialVehiclesMenuFragment;
import com.yskj.jnga.network.xml.BaseTable;


public class OfficialVehiclesActivity extends RxBaseActivity implements OnClickListener {

	private OfficialVehiclesMenuFragment ovmf;
	private OfficialVehApproveFragment ovaf;
	private OfficialVehApplyFragment ovaf2;
	private OfficialVehListFragment ovlf;
	private OfficialVehReturnFragment ovrf;
	private OfficialVehProgressFragment ovpf;
	public int status = 0;// 当前fragment标记,0菜单按钮 1列表(1审批列表，3接车列表，5进度列表) 2公车申请 3接车列表 4接车详情 5进度列表
	private OfficialVehForm form;
	public MyHandler mHandler;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_holiday);

		if (getActionBar() != null) {
			getActionBar().hide();
		}

		mHandler = new MyHandler(this);

		findView();
		initWedgit(0);// 默认进入菜单列表
	}

	@Override
	public int getLayoutId() {
		return R.layout.activity_holiday;
	}

	@Override
	public void initViews(Bundle savedInstanceState) {

	}

	@Override
	public void initToolBar() {

	}

	public void initWedgit(int status) {

		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		hideAllFragment(transaction);

		switch (status) {
		case 0:
			if (ovmf == null) {// 菜单列表
				ovmf = OfficialVehiclesMenuFragment.newInstance();
			}
			transaction.replace(R.id.content_frame, ovmf).commit();
			break;
		case 1:
			if (ovlf == null) {// 审批列表
				ovlf = OfficialVehListFragment.newInstance();
			}
			transaction.replace(R.id.content_frame, ovlf).commit();
			break;

		case 2:
			if (ovaf2 == null) {// 申请
				ovaf2 = OfficialVehApplyFragment.newInstance();
			}
			transaction.replace(R.id.content_frame, ovaf2).commit();
			break;

		case 3:
			if (ovaf == null) {// 审批
				ovaf = OfficialVehApproveFragment.newInstance();
			}
			transaction.replace(R.id.content_frame, ovaf).commit();
			break;
		case 4:
			if (ovrf == null) {// 还车
				ovrf = OfficialVehReturnFragment.newInstance();
			}
			transaction.replace(R.id.content_frame, ovrf).commit();
			break;
		case 5:
			if (ovpf == null) {// 进度
				ovpf = OfficialVehProgressFragment.newInstance();
			}
			transaction.replace(R.id.content_frame, ovpf).commit();
			break;
		default:
			break;
		}

	}

	private void hideAllFragment(FragmentTransaction transaction) {

	}

	private void findView() {

		ImageView qjbg_title = (ImageView) findViewById(R.id.qjbg_title);
		qjbg_title.setImageResource(R.drawable.official_veh_title);
		ImageButton btn_lmjclb_back = (ImageButton) findViewById(R.id.btn_lmjclb_back);
		btn_lmjclb_back.setOnClickListener(this);
		ImageView btn_lmjclb_add = (ImageView) findViewById(R.id.btn_lmjclb_add);
		btn_lmjclb_add.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_lmjclb_back:
			this.finish();
			break;
		case R.id.btn_lmjclb_add:
			showPopUp(findViewById(R.id.btn_lmjclb_add));
			break;
		case R.id.vg_qjsq_jdcx:
			status = 5;
			initWedgit(1);
			break;
		default:
			break;
		}

	}

	// 设置公务车表单
	public OfficialVehForm getForm() {
		return form;
	}

	// 获取公务车表单
	public void setForm(OfficialVehForm form) {
		this.form = form;
	}

	/**
	 * 消息队列
	 */

	public static class MyHandler extends Handler {
		WeakReference<OfficialVehiclesActivity> mActivityReference;

		MyHandler(OfficialVehiclesActivity activity) {
			mActivityReference = new WeakReference<OfficialVehiclesActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {

			OfficialVehiclesActivity activity = mActivityReference.get();
			switch (msg.what) {
			case 1:
				if (activity.ovrf.pd != null && activity.ovrf.pd.isShowing()) {
					activity.ovrf.pd.cancel();
				}
				BaseTable policeInfo = (BaseTable) msg.obj;
				if (policeInfo.getField("RESULT_POLICE_NAME").equals(activity.ovrf.et_name.getText().toString())) {
					activity.ovrf.type = "upload";
					activity.ovrf.executeTask("upload");
				} else {
					Toast.makeText(activity, "还车人姓名与所填写的警员编号不匹配,请认真核对", Toast.LENGTH_SHORT).show();
				}
				break;
			case 2:
				if (activity.ovrf.pd != null && activity.ovrf.pd.isShowing()) {
					activity.ovrf.pd.cancel();
				}
				Toast.makeText(activity, "未查到有效的警员信息,请认真核对", Toast.LENGTH_SHORT).show();
				break;

			case 3:
				
				if (activity.ovlf.pd != null && activity.ovlf.pd.isShowing()) {

					activity.ovlf.pd.cancel();
				}
				activity.ovlf.initWedgit();
				break;
			case 4:
				if (activity.ovlf.pd != null && activity.ovlf.pd.isShowing()) {
					activity.ovlf.pd.cancel();
				}

				Toast.makeText(activity, "未查到有效的数据", Toast.LENGTH_SHORT).show();

				break;

			default:
				break;
			}
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

		// vg_qjsq_spcx.setOnClickListener(this);
		vg_qjsq_spcx.setVisibility(View.GONE);
		vg_qjsq_jdcx.setOnClickListener(this);

	}

}
