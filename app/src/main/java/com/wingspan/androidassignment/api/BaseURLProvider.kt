package com.wingspan.androidassignment.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.util.concurrent.TimeUnit

object BaseURLProvider {

    const val BASE_URL="https://fssservices.bookxpert.co/api/"

    fun create(): EndPointUrlProvider
    {
        val retrofit=Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(
            GsonConverterFactory.create()).build()
        return retrofit.create(EndPointUrlProvider::class.java)
    }
}