package com.werb.graduate.network

import android.net.Uri
import com.google.gson.Gson
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.InputStream


/**
 * Created by wanbo on 2020/6/12.
 */
object ApiClient {

    const val host = "http://www.picup.shop/api/v1/"

    private var okHttpClient: OkHttpClient? = null

    private fun provideOkHttpClient(): OkHttpClient {
        if (okHttpClient == null) {
            val client = OkHttpClient.Builder()
            okHttpClient = client.build()
        }
        return okHttpClient!!
    }

    fun getApi(): API {

        return Retrofit.Builder()
            .client(provideOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(host)
            .build()
            .create(API::class.java)
    }

    fun mattingImage(fileUri: Uri, block:(InputStream?) -> Unit) {

        val file = File(fileUri.path!!)

        val client = provideOkHttpClient()

        val body: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(
                "file", file.name,
                RequestBody.create(
                    MediaType.parse("application/octet-stream"),
                    file
                )
            )
            .build()

        val request: Request = Request.Builder()
            .url("http://www.picup.shop/api/v1/matting?mattingType=3")
            .method("POST", body)
            .addHeader("APIKEY", "2ace3672269d4db0b8e5f7f7311babee")
            .addHeader("Content-Type", "multipart/form-data")
            .build()

        val response: Response = client.newCall(request).execute()
        block(response.body()?.byteStream())
    }

}