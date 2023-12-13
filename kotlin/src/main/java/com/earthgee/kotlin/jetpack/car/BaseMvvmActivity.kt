package com.earthgee.kotlin.jetpack.car

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import java.lang.reflect.ParameterizedType

/**
 *  Created by zhaoruixuan1 on 2023/12/13
 *  CopyRight (c) haodf.com
 *  功能：
 */
abstract class BaseMvvmActivity<Vm: BaseViewModel<*>, V: ViewDataBinding> : BaseDatabindActivity<V>(){

    lateinit var mViewModel: Vm

    abstract fun getViewModelFactory(): Any

    abstract fun getViewModelVariable(): Int

    abstract fun initObserable(viewModel: Vm)

    abstract fun loadData(viewModel: Vm)

    override fun onCreate(savedInstanceState: Bundle?) {
        createViewModel()
        super.onCreate(savedInstanceState)
        if(getViewModelVariable() != 0) {
            mBinding.setVariable(getViewModelVariable(), mViewModel)
        }
        initObserable(mViewModel)
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
            mViewModel = ViewModelProvider(this@BaseMvvmActivity, factory).get(modelClass)
        } else {
            mViewModel = ViewModelProvider(
                this@BaseMvvmActivity,
                ViewModelProvider.NewInstanceFactory()
            ).get(modelClass)
        }
    }

}