package com.yskj.jnga.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import com.yskj.jnga.network.ApiConstants;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 63987 on 2018/4/17
 * 一般工具类
 */

public class Utils {

    /**
     * 针对三星手机适配图片
     */

    public static BitmapDrawable setSamsungBg(Context context, int resId) {
        InputStream is;
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        opt.inSampleSize = 2;
        is = context.getResources().openRawResource(resId);
        Bitmap bm = BitmapFactory.decodeStream(is, null, opt);
        return new BitmapDrawable(context.getResources(), bm);
    }

    // MD5加密
    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    // 网络是否可用
    public static boolean isNetworkAvailable(Context context) {
        boolean isNetConnected;
        // 获得网络连接服务
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();
        // info != null &&
        // info.isAvailable()&&info.getType()!=ConnectivityManager.TYPE_WIFI
        if (info != null && info.isAvailable()) {
            // String name = info.getTypeName();
            // L.i("当前网络名称：" + name);
            isNetConnected = true;
        } else {
            Toast.makeText(context, "请确认网络连接并且非wifi环境", Toast.LENGTH_SHORT).show();
            isNetConnected = false;
        }
        return isNetConnected;
    }

    	/*
	 * 获取网络服务时间
	 */

    public static String getNetTime(String urlStr) {
        URL url = null;// 取得资源对象
        URLConnection uc = null;
        String timeStr = null;
        try {
            url = new URL(urlStr);
            uc = url.openConnection();
            uc.connect();
            long d = uc.getDate();
            timeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA).format(new Date(d));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return timeStr;
    }

    	/*
	 * 获取网络服务时间指定格式
	 */

    public static String getNetTime(String urlStr, String type) {
        URL url = null;// 取得资源对象
        URLConnection uc = null;
        String timeStr = null;
        try {
            url = new URL(urlStr);
            uc = url.openConnection();
            uc.connect();
            long d = uc.getDate();
            timeStr = new SimpleDateFormat(type,Locale.CANADA).format(new Date(d));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return timeStr;
    }


    /*
 * 获取客户端对应服务器的时间 默认yyyy-MM-dd HH:mm:ss
 */
    public static String getNetTime() {
        return Utils.getNetTime("http://" + ApiConstants.CONNIP + ApiConstants.PATH);
    }

    /*
 * 获取客户端对应服务器的时间 指定格式
 */
    public static String getNetTimeByType(String type) {
        return Utils.getNetTime("http://" + ApiConstants.CONNIP + ApiConstants.PATH, type);
    }

    // 按格式返回当前时间
    public static String getNowTime(String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 0);
        long date = cal.getTimeInMillis();
        return sdf.format(new Date(date));
    }


    /**
     * 将yyyy-MM-dd HH:mm格式转成yyyy/MM/dd HH:mm
     *
     * @param time
     * @return
     */
    public static String getFormatTime(String time) {
        SimpleDateFormat oldFormat;
        SimpleDateFormat newFormat;
        Date date = new Date();

        oldFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            date = oldFormat.parse(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        newFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");

        return newFormat.format(date);
    }

    // 设置通知栏状态
    @TargetApi(19)
    public static void setTranslucentStatus(Activity activity, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);

    }

    // 返回小于规定长度的字符串，超出部分用…代替
    public static String formatText(String text, int length) {
        if (text.length() > length) {
            text = text.substring(0, length - 1);
            text += "...";
        }
        return text;
    }

    /**
     * scrollView与listView合用会出现listView只显示一行多点。此方法是为了定死listView的高度就不会出现以上状况
     * 算出listView的高度
     */
    public static void changeListViewHeight(ListView listView) {
        if (listView.getAdapter() == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listView.getAdapter().getCount(); i++) {
            View listItem = listView.getAdapter().getView(i, null, listView);
            listItem.measure(1, 1);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listView.getAdapter().getCount() - 1))
                + listView.getPaddingTop() + listView.getPaddingBottom();
        listView.setLayoutParams(params);
    }


    /**
     * 更新主页底部消息提示数 负数减少 正数增加
     */
    public static int updateMessageCount(SpUtil spUtil, int count) {
        int messageCount = spUtil.getInt(ApiConstants.MESSAGE_COUNT, 0);
        if (messageCount != 0) {
            // 提交审核之后减少本地信息条数
            spUtil.putInt(ApiConstants.MESSAGE_COUNT, messageCount + count);

        }

        return spUtil.getInt(ApiConstants.MESSAGE_COUNT, 0);
    }

}
