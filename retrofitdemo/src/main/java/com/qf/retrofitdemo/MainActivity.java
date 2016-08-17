package com.qf.retrofitdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.List;
import java.util.StringTokenizer;

import MyContext.MyApplication;
import POJO.NewsData;
import POJO.Zhuanti;
import Util.HttpUrl;
import Util.HttpUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "print";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //getNewData();

        Call<Zhuanti> call = MyApplication.utils.getZhuanti();

        call.enqueue(new Callback<Zhuanti>() {
            @Override
            public void onResponse(Call<Zhuanti> call, Response<Zhuanti> response) {
                List<Zhuanti.DataBean.ItemsBean> data = response.body().getData().getItems();
                for (int i = 0; i < data.size(); i++) {
                    Log.e(TAG, "onResponse: "+ data.get(i).getComponent().getTitle());

                }
            }

            @Override
            public void onFailure(Call<Zhuanti> call, Throwable t) {

            }
        });

    }

    private void getNewData() {
        Call<NewsData> call = MyApplication.utils.getNewData("260443");

        //异步请求并转化为bean;
        call.enqueue(new Callback<NewsData>() {
            @Override
            public void onResponse(Call<NewsData> call, Response<NewsData> response) {
               List< NewsData.DataBean.ItemsBean> data =  response.body().getData().getItems();
                for (int i = 0; i < data.size(); i++){
                    Log.e(TAG,"Response :" + data.get(i).getComponent().getPicUrl());

                }
            }

            @Override
            public void onFailure(Call<NewsData> call, Throwable t) {

            }
        });
    }
}
