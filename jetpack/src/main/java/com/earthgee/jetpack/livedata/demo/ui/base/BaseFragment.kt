package com.earthgee.jetpack.livedata.demo.ui.base

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.earthgee.jetpack.JetpackApp

/**
 *  Created by zhaoruixuan1 on 2023/12/19
 *  CopyRight (c) haodf.com
 *  功能：
 */
open class BaseFragment : Fragment() {

    val mFragmentProvider: ViewModelProvider by lazy {
        ViewModelProvider(this)
    }

    val mActivityProvider: ViewModelProvider by lazy {
        ViewModelProvider(requireActivity())
    }

    val mApplicationProvider: ViewModelProvider by lazy {
        ViewModelProvider(JetpackApp)
    }

    val nav:NavController
        get() = NavHostFragment.findNavController(this)

    fun showLongToast(text: String) {
        Toast.makeText(requireActivity(), text, Toast.LENGTH_LONG).show()
    }

    fun showShortToast(text: String) {
        Toast.makeText(requireActivity(), text, Toast.LENGTH_SHORT).show()
    }

    inline fun <reified T : ViewModel> getFragmentViewModel(): T {
        return mFragmentProvider[T::class.java]
    }

    inline fun <reified T : ViewModel> getActivityViewModel(): T {
        return mActivityProvider[T::class.java]
    }

    inline fun <reified T : ViewModel> getApplicationViewModel(): T {
        return mApplicationProvider[T::class.java]
    }

}