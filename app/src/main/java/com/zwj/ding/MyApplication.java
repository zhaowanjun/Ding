package com.zwj.ding;

import android.app.Application;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

public class MyApplication extends Application {

    public static OkHttpClient okHttpClient;
    public static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    @Override
    public void onCreate() {
        super.onCreate();
        initHttpClient();
    }

    private void initHttpClient() {
        okHttpClient = new OkHttpClient();
    }

}
