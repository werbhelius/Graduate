package com.werb.graduate.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.werb.graduate.R
import com.werb.graduate.databinding.ActivityMattingBinding
import com.werb.graduate.events.AddNewAvatarEvent
import com.werb.graduate.exts.saveBitmap
import com.werb.graduate.exts.saveBitmapToPng
import com.werb.graduate.exts.syncAction
import com.werb.graduate.model.StickersManager
import com.werb.graduate.network.ApiClient
import com.werb.graduate.view.LoadingFragment
import ja.burhanrashid52.photoeditor.PhotoEditor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
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
    private val bitmaps = mutableListOf<Bitmap>()

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
        binding.imageBg.setBrushDrawingMode(true)
        binding.imageBg.setBrushSize(binding.sbSize.progress.toFloat())

        binding.returnBtn.setOnClickListener {
            finish()
        }

        binding.saveBtn.setOnClickListener {
            saveToFinish()
        }

        binding.sbSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.imageBg.setBrushSize(progress.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        binding.undoImg.setOnClickListener {
            val ss = binding.imageBg.undo()
            if (!ss) {
                if (bitmaps.size > 1) {
                    bitmaps.removeAt(bitmaps.size - 1)
                }
                if (bitmaps.size > 0) {
                    binding.imageBg.setImageBitmap(bitmaps.last())
                }
            }
        }

        binding.matting.setOnClickListener {
            loading(true)
            mattingCurrent {
                syncAction({
                    it?.also {
                        setBitmap(it)
                        loading(false)
                    } ?: run {
                        loading(false)
                    }
                })
            }
        }
    }

    private fun setImg() {
        if (originalBitmapPath.isNotBlank()) {
            val bmp = BitmapFactory.decodeFile(originalBitmapPath)
            setBitmap(bmp)
            loading(true)
            matting(originalBitmapPath) {
                it?.also {
                    setBitmap(it)
                    loading(false)
                } ?: run {
                    loading(false)
                }
            }
        }
    }

    private fun setBitmap(bitmap: Bitmap) {
        bitmaps.add(bitmap)
        binding.imageBg.setImageBitmap(bitmap)
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
            ApiClient.mattingImage(path) { result ->
                val bitmap = BitmapFactory.decodeStream(result)
                block(bitmap)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun mattingCurrent(block:(Bitmap?) -> Unit) {
        val path = cacheDir.absolutePath + "/cache_${UUID.randomUUID()}.png"
        binding.imageBg.isDrawingCacheEnabled = true
        val bmp = binding.imageBg.drawingCache
        saveBitmapToPng(bmp, path) {
            matting(path, block)
        }
    }

    @SuppressLint("MissingPermission")
    private fun saveToFinish() {
        val dir = filesDir.absolutePath + "/avatars"
        if (!File(dir).exists()) {
            File(dir).mkdirs()
        }
        val path = filesDir.absolutePath + "/avatars/${UUID.randomUUID()}.png"
        binding.imageBg.isDrawingCacheEnabled = true
        val bmp = binding.imageBg.drawingCache
        saveBitmapToPng(bmp, path) {
            StickersManager.addAvatar(File(path).toUri()){
                EventBus.getDefault().post(AddNewAvatarEvent())
                finish()
            }
        }
    }

}