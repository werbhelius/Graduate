package com.werb.graduate.model

import java.io.Serializable

/**
 * Created by wanbo on 2020/6/11.
 */
class AddPeopleMode: Serializable {

    var xingbie = Xingbie.NAN
    var xuewei = Xuewei.XUE_SHI
    var xueke = Xueke.WEN

    fun setXingbie(position: Int) {
        xingbie = when(position) {
            0 -> {
                Xingbie.NAN
            }
            1 -> {
                Xingbie.NV
            }
            else -> {
                Xingbie.NAN
            }
        }
    }

    fun setXuewei(position: Int) {
        xuewei = when(position) {
            0 -> {
                Xuewei.XUE_SHI
            }
            1 -> {
                Xuewei.SHUO_SHI
            }
            2 -> {
                Xuewei.BO_SHI
            }
            else -> {
                Xuewei.XUE_SHI
            }
        }
    }

    fun setXueke(position: Int) {
        xueke = when(position) {
            0 -> {
                Xueke.WEN
            }
            1 -> {
                Xueke.LI
            }
            2 -> {
                Xueke.GONG
            }
            3 -> {
                Xueke.GONG
            }
            4 -> {
                Xueke.JUN
            }
            5 -> {
                Xueke.YI
            }
            else -> {
                Xueke.WEN
            }
        }
    }

}

enum class Xingbie(val position: Int) {
    NAN(position = 0),
    NV(position = 1)
}

enum class Xuewei(val position: Int) {
    XUE_SHI(position = 0),
    SHUO_SHI(position =1),
    BO_SHI(position = 2)
}

enum class Xueke(val position: Int) {
    WEN(position = 0),
    LI(position = 1),
    GONG(position = 2),
    JUN(position = 3),
    YI(position = 4),
    NONG(position = 5)
}