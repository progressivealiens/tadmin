package com.techsalt.tadmin.webapi;

import android.content.Context;
import com.techsalt.tadmin.helper.Utils;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {
    private static OkHttpClient client,clientWeather;
    private static Retrofit retrofit = null,retrofitWeather=null;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);


            client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(logging)
                    .addInterceptor(new ConnectivityInterceptor(context))
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(Utils.BASE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getClientWeather(Context context) {
        if (retrofitWeather == null) {

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);


            clientWeather = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(logging)
                    .addInterceptor(new ConnectivityInterceptor(context))
                    .build();

            retrofitWeather = new Retrofit.Builder()
                    .baseUrl(Utils.BASE_WEATHER)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(clientWeather)
                    .build();
        }
        return retrofitWeather;
    }
}
