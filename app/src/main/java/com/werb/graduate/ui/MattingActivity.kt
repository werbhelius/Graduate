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
    private var mPhotoEditor:PhotoEditor? = null
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
        mPhotoEditor = PhotoEditor.Builder(this, binding.photoEditorView)
            .setPinchTextScalable(true)
            .build()
        mPhotoEditor?.setBrushDrawingMode(true)
        mPhotoEditor?.brushColor = resources.getColor(R.color.color_FFFFFF)
        mPhotoEditor?.brushSize = binding.sbSize.progress.toFloat()

        binding.returnBtn.setOnClickListener {
            finish()
        }

        binding.saveBtn.setOnClickListener {
            saveToFinish()
        }

        binding.sbSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mPhotoEditor?.brushSize = progress.toFloat()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        binding.undoImg.setOnClickListener {
            mPhotoEditor?.also {
                val ss = it.undo()
                if (!ss) {
                    if (bitmaps.size > 1) {
                        bitmaps.removeAt(bitmaps.size - 1)
                    }
                    if (bitmaps.size > 0) {
                        binding.photoEditorView.source.setImageBitmap(bitmaps.last())
                    }
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
        binding.photoEditorView.source.setImageBitmap(bitmap)
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
        mPhotoEditor?.saveAsFile(path, object : PhotoEditor.OnSaveListener {

            override fun onSuccess(imagePath: String) {
                matting(imagePath, block)
            }

            override fun onFailure(exception: Exception) {

            }

        })
    }

    @SuppressLint("MissingPermission")
    private fun saveToFinish() {
        val dir = filesDir.absolutePath + "/avatars"
        if (!File(dir).exists()) {
            File(dir).mkdirs()
        }
        val path = filesDir.absolutePath + "/avatars/${UUID.randomUUID()}+.png}"
        mPhotoEditor?.saveAsFile(path, object : PhotoEditor.OnSaveListener {

            override fun onSuccess(imagePath: String) {
                StickersManager.addAvatar(File(path).toUri()){
                    EventBus.getDefault().post(AddNewAvatarEvent())
                    finish()
                }
            }

            override fun onFailure(exception: Exception) {
                Toast.makeText(this@MattingActivity, "保持失败，请重试。", Toast.LENGTH_SHORT).show()
            }

        })
    }

}