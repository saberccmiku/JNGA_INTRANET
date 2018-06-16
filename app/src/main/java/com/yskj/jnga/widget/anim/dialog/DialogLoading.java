package com.yskj.jnga.widget.anim.dialog;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.yskj.jnga.R;

/**
 * @author wufan
 * @category loading对话框
 * 
 */
public class DialogLoading
{
	/**
	 * loading对话框
	 */
	public static AlertDialog getProgressDialog(Activity activity, String loadText)
	{
		AlertDialog ad = new ProgressDialog(activity);
		ad.setCanceledOnTouchOutside(false);
		ad.setCancelable(true);
		ad.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
		((ProgressDialog)ad).setInfo(loadText);
		//ad.show();
		ad.getWindow().setLayout(dpToPx(200, activity), dpToPx(125, activity));
		return ad;
	}
	
	public static class ProgressDialog extends AlertDialog
	{
		View view;
		Context context;
		
		protected ProgressDialog(Context context)
		{
			super(context);
			this.context = context;
			init();
		}

		protected ProgressDialog(Context context, int theme)
		{
			super(context, theme);
			this.context = context;
			init();
		}
		
		private void init()
		{
			view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
			View img1 = view.findViewById(R.id.pd_circle1);
			View img2 = view.findViewById(R.id.pd_circle2);
			View img3 = view.findViewById(R.id.pd_circle3);
			int ANIMATION_DURATION = 500;
			Animator anim1 = setRepeatableAnim((Activity)context, img1, ANIMATION_DURATION, R.animator.growndisappear);
			Animator anim2 = setRepeatableAnim((Activity)context, img2, ANIMATION_DURATION, R.animator.growndisappear);
			Animator anim3 = setRepeatableAnim((Activity)context, img3, ANIMATION_DURATION, R.animator.growndisappear);
			setListeners(img1, anim1, anim2, ANIMATION_DURATION);
			setListeners(img2, anim2, anim3, ANIMATION_DURATION);
			setListeners(img3, anim3, anim1, ANIMATION_DURATION);
			anim1.start();
			
			this.setView(view);
		}
		
		public void setInfo(String info)
		{
			((TextView) view.findViewById(R.id.tv_load_text)).setText(info);
		}
	}

	/**
	 * Convert dp to px
	 * 
	 * @author Sachin
	 * @param i
	 * @param mContext
	 * @return
	 */
	public static int dpToPx(int i, Context mContext)
	{
		DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
		return (int) ((i * displayMetrics.density) + 0.5);
	}

	public static Animator setRepeatableAnim(Activity activity, View target, final int duration, int animRes)
	{
		final Animator anim = AnimatorInflater.loadAnimator(activity, animRes);
		anim.setDuration(duration);
		anim.setTarget(target);
		return anim;
	}

	public static void setListeners(final View target, Animator anim, final Animator animator, final int duration)
	{
		anim.addListener(new Animator.AnimatorListener()
		{
			@Override
			public void onAnimationStart(Animator animat)
			{
				if (target.getVisibility() == View.INVISIBLE)
				{
					target.setVisibility(View.VISIBLE);
				}
				new Handler().postDelayed(new Runnable()
				{
					@Override
					public void run()
					{
						animator.start();
					}
				}, duration - 100);
			}

			@Override
			public void onAnimationEnd(Animator animator)
			{

			}

			@Override
			public void onAnimationCancel(Animator animator)
			{

			}

			@Override
			public void onAnimationRepeat(Animator animator)
			{

			}
		});
	}

}
