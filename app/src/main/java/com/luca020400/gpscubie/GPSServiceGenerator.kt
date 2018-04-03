package com.luca020400.gpscubie

import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class GPSServiceGenerator {
    private val httpClient = OkHttpClient.Builder()

    private val builder = Retrofit.Builder()
            .baseUrl("http://luca020400.duckdns.org:3333")
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

    private var retrofit = builder.build()

    fun <S> createService(serviceClass: Class<S>, username: String, password: String): S {
        val authToken = Credentials.basic(username, password)
        return createService(serviceClass, authToken)
    }

    private fun <S> createService(serviceClass: Class<S>, authToken: String): S {
        val interceptor = AuthenticationInterceptor(authToken)

        if (!httpClient.interceptors().contains(interceptor)) {
            httpClient.addInterceptor(interceptor)

            builder.client(httpClient.build())
            retrofit = builder.build()
        }

        return retrofit.create(serviceClass)
    }
}