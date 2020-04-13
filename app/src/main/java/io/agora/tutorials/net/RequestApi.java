package io.agora.tutorials.net;

import io.agora.tutorials.entity.CalledInfo;
import io.agora.tutorials.entity.CallStatus;
import io.agora.tutorials.entity.ClientCommitInfo;
import io.agora.tutorials.entity.ClientFormStatus;
import io.agora.tutorials.entity.FormListInfo;
import io.agora.tutorials.entity.LoginInfo;
import io.agora.tutorials.entity.ClientInfo;
import io.agora.tutorials.entity.MuteInfo;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 请求接口
 */
public interface RequestApi {

    //查询是否有用户请求呼叫
    @GET("Admin/Infor/read")
    Call<CalledInfo> called(@Query("mobile") String mobile, @Query("house_id") int house_id);

    //查询用户信息
    @GET("Admin/Infor/userinfo")
    Call<ClientInfo> getUserInfo(@Query("uid") int uid);

    //关闭通话
    @GET("Admin/Infor/pay_close")
    Call<CallStatus> closeCall(@Query("mobile") String mobile, @Query("house_id") int house_id, @Query("uid") int uid);

    //接通请求
    @GET("Admin/Infor/connect")
    Call<CallStatus> startCall(@Query("mobile") String mobile, @Query("house_id") int house_id, @Query("uid") int uid);

    //登录
    @GET("Admin/Infor/login")
    Call<LoginInfo> login(@Query("mobile") String mobile, @Query("password") String password);

    //获取用户填写的form表单
    @GET("Admin/Index/getyuyue")
    Call<ClientFormStatus> getClientCommitInfo(@Query("admin_id") int admin_id, @Query("user_id") int user_id);

    //销售提交form表单
    @GET("Admin/Index/edityuyue")
    Call<CallStatus> commitClientInfo(@Query("ClientCommitInfo") String clientCommitInfo);

    //勿扰模式
    @GET("Admin/Infor/editrest")
    Call<CallStatus> setMute(@Query("id") int id, @Query("rest") int rest);

    //查询勿扰状态
    @GET("Admin/Infor/gettrest")
    Call<MuteInfo> getMute(@Query("id") int id);

    //获取所有的表单
    @GET("Admin/Index/getinfo")
    Call<FormListInfo> getFormList(@Query("adviser_id") int adviser_id, @Query("page") int page);

    @GET("Admin/Infor/logintype")
    Call<CallStatus> setLogin(@Query("id") int id, @Query("login") int status);
}
