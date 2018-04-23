package com.yskj.jnga.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.yskj.jnga.network.ApiConstants;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
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
}
