package com.earthgee.kotlin.jetpack.car

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.reflect.ParameterizedType

/**
 *  Created by zhaoruixuan1 on 2023/12/13
 *  CopyRight (c) haodf.com
 *  功能：
 */
abstract class BaseMvvmFragment<Vm : BaseViewModel<*>, V : ViewDataBinding> :
    BaseDatabindFragment<V>() {

    lateinit var mViewModel: Vm

    abstract fun getViewModelFactory(): Any

    abstract fun initObserable(viewModel: Vm)

    abstract fun getViewModelVariable(): Int

    abstract fun loadData(viewModel: Vm)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        createViewModel()
        val view = super.onCreateView(inflater, container, savedInstanceState)
        if(getViewModelVariable() != 0) {
            mBinding.setVariable(getViewModelVariable(), mViewModel)
        }
        initObserable(mViewModel)
        return view
    }

    override fun onStart() {
        super.onStart()
        loadData(mViewModel)
    }

    private fun createViewModel() {
        val modelClass: Class<Vm>
        val type = javaClass.genericSuperclass
        if (type is ParameterizedType) {
            modelClass = type.actualTypeArguments[0] as Class<Vm>
        } else {
            modelClass = BaseViewModel::class.java as Class<Vm>
        }

        val factory = getViewModelFactory()
        if (factory is BaseViewModel<*>) {
            mViewModel = factory as Vm
        } else if (factory is ViewModelProvider.Factory) {
            mViewModel = ViewModelProvider(this@BaseMvvmFragment, factory).get(modelClass)
        } else {
            mViewModel = ViewModelProvider(
                this@BaseMvvmFragment,
                ViewModelProvider.NewInstanceFactory()
            ).get(modelClass)
        }
    }

}

















