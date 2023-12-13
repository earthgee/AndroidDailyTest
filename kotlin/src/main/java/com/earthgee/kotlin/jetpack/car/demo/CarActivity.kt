package com.earthgee.kotlin.jetpack.car.demo

import androidx.appcompat.app.AppCompatActivity
import com.earthgee.kotlin.BR
import com.earthgee.kotlin.R
import com.earthgee.kotlin.databinding.ActivityCarJetpackBinding
import com.earthgee.kotlin.jetpack.car.BaseMvvmActivity

/**
 *  Created by zhaoruixuan1 on 2023/12/13
 *  CopyRight (c) haodf.com
 *  功能：
 */
class CarActivity: BaseMvvmActivity<CarViewModel, ActivityCarJetpackBinding>(){

    override fun getViewModelFactory(): Any {
        return CarViewModelFactory
    }

    override fun getViewModelVariable(): Int {
        return 0
    }

    override fun loadData(viewModel: CarViewModel) {
        viewModel.requestTemperature()
    }

    override fun initObserable(viewModel: CarViewModel) {
        viewModel.result.observe(this) {
            mBinding.etTemperature.setText(it)
        }
    }

    override fun initView() {
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_car_jetpack
    }


}