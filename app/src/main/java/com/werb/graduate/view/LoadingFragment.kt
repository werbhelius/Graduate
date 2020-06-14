package com.werb.graduate.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.werb.graduate.R

/**
 * Created by wanbo on 2020/6/14.
 */
class LoadingFragment: DialogFragment() {

    private var loadingView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_loading, container, false)
        dialog?.window?.setLayout(92, 92)
        loadingView = view
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        loadingView?.clearAnimation()
        loadingView = null
    }

}