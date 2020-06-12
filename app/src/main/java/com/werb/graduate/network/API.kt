package com.werb.graduate.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


/**
 * Created by wanbo on 2020/6/12.
 */
interface API {

    @Headers("APIKEY: 2ace3672269d4db0b8e5f7f7311babee", "Content-Type: multipart/form-data")
    @Multipart
    @POST("matting?mattingType=3")
    fun matting(
        @Part("file") file: RequestBody
    ): Call<ResponseBody>


}