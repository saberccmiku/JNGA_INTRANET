package com.yskj.jnga.network;

import com.yskj.jnga.App;

import okhttp3.MediaType;

/**
 * Created by saber on 2018/1/4
 * API常量
 */

public class ApiConstants {

    // 登录系统
    public static String USERID = "docadmin";
    public static String PSWID = "passw0rd";
    public static final String CONNIP = "219.159.71.168:9080";//lmc本地测试用服务器
    // 用户名密码
    public static String USER_ID = App.getInstance().getSpUtil().getAccount();
    public static String USER_PWD = App.getInstance().getSpUtil().getPwd();
    public static String USER_AUTH = App.getInstance().getSpUtil().getAuth();

//    public static final String CONNIP = "192.168.20.50:50208";
    public static final String FILE_PATH = "/IDOC/service/file/";
    public static final String PATH = "/IDOC/WebService";// 服务器路径
    public static final String IMGPATH = "/IDOC/service/file/";
    public static final String imageCachePath = "/JNGA/ImageCache/"; // 图片缓存路径
    public static final String fileCachePath = "/JNGA/FileCache/"; // 文件缓存路径
    public static String REQUEST_RESULT_SUCCESS = "SUCCESS"; // 请求返回成功码
    public static String REQUESTSUCCESS = "SUCCESS"; // xml请求返回成功码

    //json api
    public static final String APP_BASE_URL = "http://219.159.71.168:9080/AppInterface/rest/";
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");// 这个需要和服务端保持一致
    public static final String SEARCH = "search";
    public static final String UPDATE = "update";


    /**
     * 业务查询类型
     */
    public static String ONE_KEY_RESEARCH = "一键查询";
    public static String PERSON_RESEARCH = "人员查询";
    public static String VEHICLE_RESEARCH = "车辆查询";
    public static String GOODS_RESEARCH = "物品查询";
    public static String CASE_RESEARCH = "案件查询";
    public static String MAP_RESEARCH = "地图查询";
    public static String ALARM_RESEARCH = "警情查询";
    public static String POLICE_STATUS_RESEARCH = "警力查询";

    /**
     * 主界面消息条数标记
     */

    public static final String MESSAGE_COUNT = "messsage_count";
}
