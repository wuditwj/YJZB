package io.agora.tutorials.net;

import io.agora.tutorials.entity.CalledInfo;
import io.agora.tutorials.entity.CallStatus;
import io.agora.tutorials.entity.LoginInfo;
import io.agora.tutorials.entity.ClientInfo;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 请求接口
 */
public interface RequestApi {

    //查询是否有用户请求呼叫
    @GET("read")
    Call<CalledInfo> called(@Query("mobile") String mobile,@Query("house_id") int house_id);

    //查询用户信息
    @GET("userinfo")
    Call<ClientInfo> getUserInfo(@Query("uid") int uid);

    //关闭通话
    @GET("pay_close")
    Call<CallStatus> closeCall(@Query("mobile") String mobile,@Query("house_id") int house_id,@Query("uid") int uid);

    //接通请求
    @GET("connect")
    Call<CallStatus> startCall(@Query("mobile") String mobile,@Query("house_id") int house_id,@Query("uid") int uid);

    @GET("login")
    Call<LoginInfo> login(@Query("mobile") String mobile, @Query("password") String password);


}
