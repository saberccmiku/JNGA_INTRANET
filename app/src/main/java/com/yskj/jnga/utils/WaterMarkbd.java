package com.yskj.jnga.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;

public class WaterMarkbd {

	/*
	 * 第一步:创建带有文字的bitmap
	 */

	public static Bitmap getBitmap(String str) {

		Bitmap bitmap = Bitmap.createBitmap(240, 240, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawColor(Color.WHITE);
		Paint paint = new Paint();
		paint.setColor(Color.GRAY);
		paint.setAlpha(80);
		paint.setAntiAlias(true);
		paint.setTextAlign(Paint.Align.LEFT);
		paint.setTextSize(40);
		Path path = new Path();
		path.moveTo(30, 150);
		path.lineTo(300, 0);
		canvas.drawTextOnPath(str, path, 0, 30, paint);

		return bitmap;

	}
	
	/**
	 * 自身定义背景水印
	 * @param context 上下文
	 * @param str 水印文字
	 * @param resId 水印背景颜色ID
	 * @param size 文字大小
	 * @return
	 */
	public static Bitmap getBitmap(Context context,String str,int resId,int size) {
		Bitmap bitmap = Bitmap.createBitmap(240, 240, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawColor(resId);
		Paint paint = new Paint();
		paint.setColor(Color.GRAY);
		paint.setAlpha(80);
		paint.setAntiAlias(true);
		paint.setTextAlign(Paint.Align.LEFT);
		paint.setTextSize(size);
		Path path = new Path();
		path.moveTo(30, 150);
		path.lineTo(300, 0);
		canvas.drawTextOnPath(str, path, 0, 30, paint);
		return bitmap;

	}
	
	
	public static Bitmap getBitmap(Context context,String str) {
		Bitmap bitmap = Bitmap.createBitmap(240, 240, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint();
		paint.setColor(Color.GRAY);
		paint.setAlpha(120);
		paint.setAntiAlias(true);
		paint.setTextAlign(Paint.Align.LEFT);
		paint.setTextSize(40);
		Path path = new Path();
		path.moveTo(30, 150);
		path.lineTo(300, 0);
		canvas.drawTextOnPath(str, path, 0, 30, paint);
		return bitmap;

	}

	/*
	 * 第二步:创建bitmapDrawable,并使其填充方式为重复填充
	 */

	public static BitmapDrawable getBitmapDrawable(Context context, Bitmap bitmap) {

		BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);
		bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		bitmapDrawable.setDither(true);

		return bitmapDrawable;

	}

	/*
	 * 第三步:为view设置水印背景 view.setBackgroundDrawable(bitmapDrawable);
	 */

	//自定义文字水印
	@SuppressLint("NewApi")
	public static void setWaterMarkbd(Context context, View view, String str) {

		view.setBackground(getBitmapDrawable(context, getBitmap(str)));
	}
	
	//透明背景水印
	@SuppressLint("NewApi")
	public static void setWaterMark(Context context, View view, String str) {

		view.setBackground(getBitmapDrawable(context, getBitmap(context,str)));
	}
	
	//自身定义背景水印
	@SuppressLint("NewApi")
	public static void setWaterMarkbd(Context context, View view, String str,int resId,int size) {

		view.setBackground(getBitmapDrawable(context, getBitmap(context,str,resId,size)));
	}

}
