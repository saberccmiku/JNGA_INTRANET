package com.yskj.jnga.network.json;

import com.yskj.jnga.entity.User;
import com.yskj.jnga.entity.push.StatisticInfo;
import com.yskj.jnga.network.ApiConstants;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * 商品相关的接口
 * Created by saber on 2018/1/4
 */

public interface AppService {

    /**
     * 获取用户信息
     */
    @POST(ApiConstants.SEARCH)
    Observable<User> getUserInfo(@Body RequestBody body);

    /**
     * 获取首界面信息推送
     */
    @POST(ApiConstants.SEARCH)
    Observable<StatisticInfo> getStatisticInfo(@Body RequestBody body);

}
