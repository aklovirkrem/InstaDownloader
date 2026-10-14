package com.instagrab.app.data

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Streaming
import java.util.concurrent.TimeUnit

interface InstagramApiService {

    @POST("api/info")
    suspend fun getPostInfo(@Body request: UrlRequest): Response<PostInfoResponse>

    @POST("api/download")
    @Streaming
    suspend fun downloadVideo(@Body request: DownloadRequest): Response<ResponseBody>

    @GET("api/health")
    suspend fun health(): Response<Map<String, String>>
}

object ApiClient {
    private var baseUrl = "http://10.0.2.2:5000/"  // Android emulator → localhost
    // Gerçek cihaz için: "http://192.168.x.x:5000/" (PC'nin yerel IP'si)

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private fun buildClient() = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    fun create(url: String = baseUrl): InstagramApiService {
        return Retrofit.Builder()
            .baseUrl(url)
            .client(buildClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(InstagramApiService::class.java)
    }

    fun updateBaseUrl(newUrl: String): InstagramApiService {
        baseUrl = if (newUrl.endsWith("/")) newUrl else "$newUrl/"
        return create(baseUrl)
    }

    fun currentBaseUrl() = baseUrl
}
