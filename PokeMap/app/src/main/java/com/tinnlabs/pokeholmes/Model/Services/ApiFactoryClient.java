package com.tinnlabs.pokeholmes.Model.Services;



import android.util.Log;

import com.tinnlabs.pokeholmes.Security.PokeCipher;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by carlviar on 2016/07/26.
 */
public class ApiFactoryClient {

    static final String BASE_URL = "http://50.116.54.176:9000/";
    private static IApiContract endPoints = null;
    private static final String LOG_APIFACTORY_STATE = "com.tinnlabs.pokemap.Model.Services.ApiFactoryClient";

    public static IApiContract getClient(Class<IApiContract> endPoints){

        if(ApiFactoryClient.endPoints == null){
            OkHttpClient.Builder okHttpClient = new OkHttpClient().newBuilder();
            okHttpClient.connectTimeout(10, TimeUnit.SECONDS);
            okHttpClient.readTimeout(10, TimeUnit.SECONDS);
            okHttpClient.addInterceptor(chain -> {
                Buffer buffer = new Buffer();
                chain.request().body().writeTo(buffer);
                String contentValue = buffer.readUtf8();

                String encriptedValue = "Anonymous";
                try {
                    encriptedValue = PokeCipher.encrypt(contentValue);
                } catch (Exception e) {
                    Log.e(LOG_APIFACTORY_STATE, e.getMessage());
                }


                Request request = chain.request().newBuilder().addHeader("AUTH-TOKEN", encriptedValue).build();
                return chain.proceed(request);
            });

            ApiFactoryClient.endPoints = new Retrofit.Builder()
                                   .baseUrl(BASE_URL)
                                   .addConverterFactory(GsonConverterFactory.create())
                                   .client(okHttpClient.build())
                                   .build()
                                   .create(endPoints);
        }

        return ApiFactoryClient.endPoints;
    }

}
