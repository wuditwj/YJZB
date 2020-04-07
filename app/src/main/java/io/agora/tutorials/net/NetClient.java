package io.agora.tutorials.net;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 网络客户端的单例类
 */

public class NetClient {

    public static final String BASE_URL = "https://fc.zqtycn.com/";
    private static NetClient mNeyClient;
    private final Retrofit retrofit;
    private RequestApi requestApi;

    private NetClient(){
        //创建一个日志拦截器
        HttpLoggingInterceptor interceptor=new HttpLoggingInterceptor();
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
//            @Override
//            public void log(String message) {
//                //打印retrofit日志
//                Log.i("Retrofit--==>>","retrofitBack = "+message);
//            }
//        });
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //创建一个OKHttpClient
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        //创建Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();
    }

    public RequestApi getTreatrueApi(){
        if (requestApi==null){
            //实例化接口对象
            requestApi = retrofit.create(RequestApi.class);
        }
        return requestApi;
    }

    public static synchronized NetClient getInstance(){
        if (mNeyClient==null){
            mNeyClient=new NetClient();
        }
        return mNeyClient;
    }
}
