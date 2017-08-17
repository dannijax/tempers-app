package chapters.network

import android.util.Log
import chapters.database.SharedSetting
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class ServiceGenerator(endpoint: String) {
    val netApi: NetApi

    init {
        val interceptorLog = HttpLoggingInterceptor({
            Log.d("RETROFIT ", it)
            //Log.d("HEADER_",SharedSetting.getUserToken())
        }).apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val intercerHeaders = Interceptor {
            val newUrl = it.request().url().newBuilder()
//                    ?.addEncodedQueryParameter("userID", SharedSetting.getUserId().toString())
                    ?.build()
            val req2 = it.request()
                    .newBuilder()
                    .url(newUrl)
                    .addHeader("TOKEN", SharedSetting.getUserToken())
                    .build()
            it.proceed(req2)
        }
        val httpClient = OkHttpClient.Builder()
                .addInterceptor(interceptorLog)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(intercerHeaders)
                .build()

        httpClient.interceptors()
        val builder = Retrofit.Builder()
                .baseUrl(endpoint)
                .addConverterFactory(GsonConverterFactory.create(GsonUtils.builder))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(httpClient)
                .build()
        netApi = builder.create(NetApi::class.java);

    }
}