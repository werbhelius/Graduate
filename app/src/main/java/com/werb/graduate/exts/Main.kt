package com.werb.graduate.exts

import android.os.Handler
import android.os.Looper
import androidx.room.RoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Created by wanbo on 2020/6/4.
 */
inline fun syncAction(crossinline block: () -> Unit, delay: Long = 0L) {
    Handler(Looper.getMainLooper()).postDelayed({
        block()
    }, delay)
}

inline fun <T : RoomDatabase> T.asyncAction(crossinline block: (T) -> Unit): Job {
    return GlobalScope.launch(Dispatchers.Default) {
        runInTransaction {
            block(this@asyncAction)
        }
    }
}