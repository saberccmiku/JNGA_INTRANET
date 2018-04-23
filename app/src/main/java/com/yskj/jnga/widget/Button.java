package com.yskj.jnga.widget;

import android.content.Context;
import android.util.AttributeSet;

/**
 * 该Button基于TextView实现。源于系统自带Button定死了一些数据，不够灵活。
 */
public class Button extends android.support.v7.widget.AppCompatTextView
{
	public Button(Context context)
	{
		super(context);
		init();
	}

	public Button(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public Button(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	
	private void init()
	{
		setOnClickListener(v -> {

        });
	}
}
