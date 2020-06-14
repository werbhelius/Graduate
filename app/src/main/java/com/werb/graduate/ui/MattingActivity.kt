package com.werb.graduate.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.werb.graduate.databinding.ActivityMattingBinding
import com.werb.graduate.network.ApiClient
import com.werb.graduate.view.LoadingFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

/**
 * Created by wanbo on 2020/6/12.
 */
class MattingActivity: AppCompatActivity() {

    companion object {

        const val originalBitmapPath: String = "originalBitmapPath"

    }

    private var loadingDialog: LoadingFragment = LoadingFragment()
    private lateinit var binding: ActivityMattingBinding
    private var originalBitmapPath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        originalBitmapPath = intent.getStringExtra(MattingActivity.originalBitmapPath) ?: ""
        setupUI()
        setImg()
    }

    private fun setupUI() {
        binding = ActivityMattingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun setImg() {
        if (originalBitmapPath.isNotBlank()) {
            val bmp = BitmapFactory.decodeFile(originalBitmapPath)
            binding.photoEditorView.source.setImageBitmap(bmp)
            loading(true)
            matting(originalBitmapPath) {
                it?.also {
                    binding.photoEditorView.source.setImageBitmap(it)
                    loading(false)
                } ?: run {
                    loading(false)
                }
            }
        }
    }

     private fun loading(show: Boolean) {
        loadingDialog.isCancelable = false
        if (show) {
            Handler(Looper.getMainLooper()).post {
                if (!loadingDialog.isAdded && !loadingDialog.isVisible && !loadingDialog.isRemoving && !isFinishing) {
                    loadingDialog.show(supportFragmentManager, "loadingDialog")
                }
            }
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                if (loadingDialog.isAdded) {
                    loadingDialog.dismissAllowingStateLoss()
                }
            }, 300)
        }
    }

    private fun matting(path: String, block:(Bitmap?) -> Unit) {
        GlobalScope.launch(Dispatchers.Default) {
            ApiClient.mattingImage(originalBitmapPath) { result ->
                val bitmap = BitmapFactory.decodeStream(result)
                block(bitmap)
                bitmap?.also {
                    block(it)
//                    val dir =
//                        filesDir.absolutePath + "/avatars"
//                    if (!File(dir).exists()) {
//                        File(dir).mkdirs()
//                    }
//                    val path = filesDir.absolutePath + "/avatars/${UUID.randomUUID()}+.png}"
                }
            }
        }
    }

}