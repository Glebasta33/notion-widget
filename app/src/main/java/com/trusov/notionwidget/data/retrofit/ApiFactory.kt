package com.trusov.notionwidget.data.retrofit

import android.app.Application
import com.trusov.notionwidget.R
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class ApiFactory @Inject constructor(
    private val application: Application
) {

    val client = OkHttpClient.Builder().addInterceptor { chain ->
        val newRequest: Request = chain.request().newBuilder()
            .addHeader(
                "Authorization",
                "Bearer ${application.resources.getString(R.string.api_token)}"
            )
            .addHeader("Notion-Version", "2022-02-22")
            .addHeader("Content-Type", "application/json")
            .build()
        chain.proceed(newRequest)
    }.build()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(client)
        .build()

    val service = retrofit.create(ApiService::class.java)

    companion object {
        private const val BASE_URL = "https://api.notion.com/v1/"
    }
}