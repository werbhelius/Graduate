package com.werb.graduate.holder

import android.view.View
import com.werb.graduate.R
import com.werb.graduate.model.Sticker
import com.werb.library.MoreViewHolder
import com.werb.library.link.LayoutID
import kotlinx.android.synthetic.main.layout_sticker.*

/**
 * Created by wanbo on 2020/6/3.
 */
@LayoutID(R.layout.layout_sticker)
class StickerHolder(values: MutableMap<String, Any>, containerView: View) :
    MoreViewHolder<Sticker>(values, containerView) {

    override fun bindData(data: Sticker, payloads: List<Any>) {
        if (data.isAddImage) {
            image.setImageResource(R.drawable.ic_add_24px)
            image.background = null
        } else {
            
        }

    }
}