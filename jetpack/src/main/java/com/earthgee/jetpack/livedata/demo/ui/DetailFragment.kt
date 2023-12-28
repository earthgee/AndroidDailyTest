package com.earthgee.jetpack.livedata.demo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.earthgee.jetpack.R
import com.earthgee.jetpack.databinding.JetpackFragmentDetailBinding
import com.earthgee.jetpack.livedata.demo.data.Moment
import com.earthgee.jetpack.livedata.demo.domain.message.PageMessenger
import com.earthgee.jetpack.livedata.demo.ui.base.BaseFragment

/**
 *  Created by zhaoruixuan1 on 2023/12/19
 *  CopyRight (c) haodf.com
 *  功能：
 */
class DetailFragment : BaseFragment(){

    private val mState by lazy {
        getFragmentViewModel<DetailViewModel>()
    }

    private val mMessenger by lazy {
        getActivityViewModel<PageMessenger>()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.jetpack_fragment_detail, container, false)
        val binding = JetpackFragmentDetailBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = mState
        binding.click = ClickProxy()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mState.setMoment(arguments?.getParcelable(Moment.MOMENT))

        mMessenger.momentResult.observe(viewLifecycleOwner) {
            it?.let {
                mState.setMoment(it)
            }
        }
    }

    inner class ClickProxy {

        fun back() {
            //none
        }

        fun edit() {
            val bundle = Bundle().apply {
                putParcelable(Moment.MOMENT, mState.moment.value)
            }
            nav.navigate(R.id.action_detailFragment_to_editorFragment, bundle)
        }

    }

    class DetailViewModel: ViewModel() {
        private val _moment: MutableLiveData<Moment> = MutableLiveData()
        val moment: LiveData<Moment> = _moment

        fun setMoment(curMoment: Moment?) {
            curMoment?.let {
                _moment.value = it
            }
        }
    }

}