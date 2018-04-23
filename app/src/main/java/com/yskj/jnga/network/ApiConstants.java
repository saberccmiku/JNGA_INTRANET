package com.yskj.jnga.network;

import okhttp3.MediaType;

/**
 * Created by saber on 2018/1/4
 * API常量
 */

public class ApiConstants {

    // 登录系统
    public static String USERID = "docadmin";
    public static String PSWID = "passw0rd";
    public static final String CONNIP = "192.168.0.106:9080";//lmc本地测试用服务器
    public static final String FILE_PATH = "/IDOC/service/file/";
    public static final String PATH = "/IDOC/WebService";// 服务器路径
    public static final String IMGPATH = "/IDOC/service/file/";
    public static final String imageCachePath = "/hzhb/ImageCache/"; // 图片缓存路径
    public static final String fileCachePath = "/hzhb/FileCache/"; // 文件缓存路径
    public static String REQUEST_RESULT_SUCCESS = "SUCCESS"; // 请求返回成功码

    //json api
    public static final String APP_BASE_URL = "http://192.168.0.106:9080/AppInterface/rest/";
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");// 这个需要和服务端保持一致
    public static final String SEARCH = "search";
    public static final String UPDATE = "update";
}
