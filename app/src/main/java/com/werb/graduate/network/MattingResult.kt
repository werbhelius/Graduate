package com.werb.graduate.network

/**
 * Created by wanbo on 2020/6/12.
 */
data class MattingResult(var code: String = "",
                         var msg: String = "",
                         var time: Double = 0.0,
                         var data: Data? = null) {



    data class Data(var imageBase64: String = "")

}