package com.werb.graduate

/**
 * Created by wanbo on 2020/6/3.
 */
class EditManager {

    var imageRatio = ImageRatio.ONE_ONE
    set(value) {
        field = value
        onRatioChange?.invoke(field)
    }

    var onRatioChange: ((ImageRatio) -> Unit)? = null

}

enum class ImageRatio(val str: String) {

    ONE_ONE("1:1"),
    FOUR_THREE("4:3"),
    THREE_TWO("3:2"),
    SIXTEEN_NINE("16:9")

}