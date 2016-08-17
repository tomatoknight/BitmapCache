package MyContext;

import android.app.Application;

import Util.HttpUrl;
import Util.HttpUtils;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jitana on 2016/08/11.
 */
public class MyApplication extends Application {
    public static HttpUtils utils;

    @Override
    public void onCreate() {
        super.onCreate();
        utils = initRetrofit();
    }

    public  HttpUtils initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpUrl.http_root) //把http的url给对象
                .addConverterFactory(GsonConverterFactory.create())//用Gson解析
                .build();

        //得到接口对象
        return retrofit.create(HttpUtils.class);

    }
}
