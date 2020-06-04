package com.werb.graduate.model

import android.net.Uri
import androidx.room.*

/**
 * Created by wanbo on 2020/6/4.
 */
@Entity
class Background( @PrimaryKey(autoGenerate = true) val uid: Int = 0, @ColumnInfo(name = "image_uri") val imageUri: String)