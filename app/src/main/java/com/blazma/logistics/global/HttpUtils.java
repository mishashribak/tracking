package com.blazma.logistics.global;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpUtils {
private static final int MAX_TRY_COUNT = 3;


public static Retrofit getRetrofit(String baseUrl) {
    return new Retrofit.Builder()
                   .baseUrl(baseUrl)
                   .client(okHttpClientBuilder())
                   .addConverterFactory(GsonConverterFactory.create())
                   .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                   .build();
}

private static OkHttpClient okHttpClientBuilder() {
    final int CONNECT_TIMEOUT_SECOND = 60;
    final int READ_TIMEOUT_SECONDS = 60;
    final int WRITE_TIMEOUT_SECONDS = 60;
    final OkHttpClient.Builder client = new OkHttpClient.Builder()
                                                .connectTimeout(CONNECT_TIMEOUT_SECOND, TimeUnit.SECONDS)
                                                .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                                                .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                                                .retryOnConnectionFailure(true);
    
    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    client.addInterceptor(loggingInterceptor);
    return client.build();
}
}
