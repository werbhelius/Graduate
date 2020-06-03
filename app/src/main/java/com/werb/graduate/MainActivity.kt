package com.werb.graduate

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.TransitionManager
import androidx.viewpager2.widget.ViewPager2
import com.werb.graduate.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val editManager = EditManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        setupViewPager()
        setupTab()
        setupEditManager()
    }

    private fun setupUI() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun setupTab() {
        binding.backgroundBtn.setOnClickListener {
            binding.viewPager.setCurrentItem(0, true)
        }
        binding.charactersBtn.setOnClickListener {
            binding.viewPager.setCurrentItem(1, true)
        }
    }

    private fun setupViewPager() {
        binding.viewPager.adapter = MainPagerAdapter(this)
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when(position) {
                    0 -> {
                        binding.backgroundBtn.alpha = 1f
                        binding.charactersBtn.alpha = 0.4f
                    }
                    1 -> {
                        binding.charactersBtn.alpha = 1f
                        binding.backgroundBtn.alpha = 0.4f
                    }
                }
            }
        })
    }

    private fun setupEditManager() {
        editManager.onRatioChange = {
            TransitionManager.beginDelayedTransition(binding.root)
            val set = ConstraintSet()
            set.clone(binding.root)
            set.setDimensionRatio(binding.photoEditorView.id, it.str)
            set.applyTo(binding.root)
            updateLayoutRatioColor(it)
        }
        editManager.imageRatio = ImageRatio.ONE_ONE
        binding.layout11.setOnClickListener {
            editManager.imageRatio = ImageRatio.ONE_ONE
        }
        binding.layout43.setOnClickListener {
            editManager.imageRatio = ImageRatio.FOUR_THREE
        }
        binding.layout32.setOnClickListener {
            editManager.imageRatio = ImageRatio.THREE_TWO
        }
        binding.layout169.setOnClickListener {
            editManager.imageRatio = ImageRatio.SIXTEEN_NINE
        }
    }

    private fun updateLayoutRatioColor(imageRatio: ImageRatio) {
        when(imageRatio) {
            ImageRatio.ONE_ONE -> {
                binding.layout11Image.colorFilter = PorterDuffColorFilter(resources.getColor(R.color.color_F15631), PorterDuff.Mode.SRC_IN)
                binding.layout11Text.setTextColor(resources.getColor(R.color.color_F15631))
                binding.layout43Image.colorFilter = PorterDuffColorFilter(resources.getColor(R.color.color_000000), PorterDuff.Mode.SRC_IN)
                binding.layout43Text.setTextColor(resources.getColor(R.color.color_000000))
                binding.layout32Image.colorFilter = PorterDuffColorFilter(resources.getColor(R.color.color_000000), PorterDuff.Mode.SRC_IN)
                binding.layout32Text.setTextColor(resources.getColor(R.color.color_000000))
                binding.layout169Image.colorFilter = PorterDuffColorFilter(resources.getColor(R.color.color_000000), PorterDuff.Mode.SRC_IN)
                binding.layout169Text.setTextColor(resources.getColor(R.color.color_000000))
            }
            ImageRatio.FOUR_THREE -> {
                binding.layout43Image.colorFilter = PorterDuffColorFilter(resources.getColor(R.color.color_F15631), PorterDuff.Mode.SRC_IN)
                binding.layout43Text.setTextColor(resources.getColor(R.color.color_F15631))
                binding.layout11Image.colorFilter = PorterDuffColorFilter(resources.getColor(R.color.color_000000), PorterDuff.Mode.SRC_IN)
                binding.layout11Text.setTextColor(resources.getColor(R.color.color_000000))
                binding.layout32Image.colorFilter = PorterDuffColorFilter(resources.getColor(R.color.color_000000), PorterDuff.Mode.SRC_IN)
                binding.layout32Text.setTextColor(resources.getColor(R.color.color_000000))
                binding.layout169Image.colorFilter = PorterDuffColorFilter(resources.getColor(R.color.color_000000), PorterDuff.Mode.SRC_IN)
                binding.layout169Text.setTextColor(resources.getColor(R.color.color_000000))
            }
            ImageRatio.THREE_TWO -> {
                binding.layout32Image.colorFilter = PorterDuffColorFilter(resources.getColor(R.color.color_F15631), PorterDuff.Mode.SRC_IN)
                binding.layout32Text.setTextColor(resources.getColor(R.color.color_F15631))
                binding.layout43Image.colorFilter = PorterDuffColorFilter(resources.getColor(R.color.color_000000), PorterDuff.Mode.SRC_IN)
                binding.layout43Text.setTextColor(resources.getColor(R.color.color_000000))
                binding.layout11Image.colorFilter = PorterDuffColorFilter(resources.getColor(R.color.color_000000), PorterDuff.Mode.SRC_IN)
                binding.layout11Text.setTextColor(resources.getColor(R.color.color_000000))
                binding.layout169Image.colorFilter = PorterDuffColorFilter(resources.getColor(R.color.color_000000), PorterDuff.Mode.SRC_IN)
                binding.layout169Text.setTextColor(resources.getColor(R.color.color_000000))
            }
            ImageRatio.SIXTEEN_NINE -> {
                binding.layout169Image.colorFilter = PorterDuffColorFilter(resources.getColor(R.color.color_F15631), PorterDuff.Mode.SRC_IN)
                binding.layout169Text.setTextColor(resources.getColor(R.color.color_F15631))
                binding.layout32Image.colorFilter = PorterDuffColorFilter(resources.getColor(R.color.color_000000), PorterDuff.Mode.SRC_IN)
                binding.layout32Text.setTextColor(resources.getColor(R.color.color_000000))
                binding.layout43Image.colorFilter = PorterDuffColorFilter(resources.getColor(R.color.color_000000), PorterDuff.Mode.SRC_IN)
                binding.layout43Text.setTextColor(resources.getColor(R.color.color_000000))
                binding.layout11Image.colorFilter = PorterDuffColorFilter(resources.getColor(R.color.color_000000), PorterDuff.Mode.SRC_IN)
                binding.layout11Text.setTextColor(resources.getColor(R.color.color_000000))
            }
        }
    }

}