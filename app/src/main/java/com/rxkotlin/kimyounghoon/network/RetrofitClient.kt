package com.rxkotlin.kimyounghoon.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {
    companion object {
        fun getInstance(): Retrofit {
            return Retrofit.Builder()
                    .baseUrl(ApiConstants.API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(getOkHttpClient())
                    .build()
        }

        private fun getOkHttpClient(): OkHttpClient {
            return OkHttpClient.Builder()
                    .addInterceptor(getHeaderInterceptor())
                    .build()
        }

        private fun getHeaderInterceptor(): Interceptor {
            return Interceptor {
                val builder = it.request().newBuilder()
                builder.addHeader(ApiConstants.API_HEADER_AUTHORIZATION, ApiConstants.API_KEY)
                it.proceed(builder.build())
            }
        }

    }
}