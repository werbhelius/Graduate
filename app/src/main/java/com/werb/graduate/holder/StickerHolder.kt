package com.werb.graduate.holder

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.werb.graduate.R
import com.werb.graduate.exts.getImage
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
            displayImage.setImageResource(R.drawable.ic_add_24px)
            displayImage.background = null
            displayImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
        } else {
            displayImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
            data.localImageUri?.also {
                Glide.with(containerView.context)
                    .load(it).into(displayImage)
            } ?: kotlin.run {
                Glide.with(containerView.context)
                    .load(containerView.context.getImage(data.localImageName)).into(displayImage)
            }
        }

        //click
        displayImage.tag = data
        addOnClickListener(displayImage)
        addOnLongClickListener(displayImage)
    }
}