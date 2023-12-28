package com.earthgee.jetpack.livedata.demo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.earthgee.jetpack.R
import com.earthgee.jetpack.databinding.JetpackFragmentEditorBinding
import com.earthgee.jetpack.livedata.demo.data.DataRepository
import com.earthgee.jetpack.livedata.demo.data.Moment
import com.earthgee.jetpack.livedata.demo.domain.message.PageMessenger
import com.earthgee.jetpack.livedata.demo.ui.base.BaseFragment

/**
 *  Created by zhaoruixuan1 on 2023/12/19
 *  CopyRight (c) haodf.com
 *  功能：
 */
class EditorFragment : BaseFragment() {

    private val mState by lazy {
        getFragmentViewModel<EditorViewModel>()
    }

    private val mMessenger by lazy {
        getActivityViewModel<PageMessenger>()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.jetpack_fragment_editor, container, false)
        val binding = JetpackFragmentEditorBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = mState
        binding.click = ClickProxy()
        return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mState.setMoment(arguments?.getParcelable(Moment.MOMENT))
        mState.moment.observe(viewLifecycleOwner) {
            mState.content.value = it.content
        }
    }

    inner class ClickProxy {

        fun save() {
            val moment = mState.moment.value?.copy(content=mState.content.value?: "")
            moment?.let {
                mMessenger.momentResult.value = it
            } ?: run {
                mState.setMoment(Moment(
                    DataRepository.getUUID(),
                    mState.content.value?: "",
                    "我爱北京天安门123",
                    "",
                    "earthgee",
                    "https://tva1.sinaimg.cn/large/e6c9d24ely1h4exa8m7quj20ju0juaax.jpg"
                ))
                mMessenger.momentResult.value = mState.moment.value
            }
            nav.navigateUp()
        }


    }

    class EditorViewModel : ViewModel() {
        private val _moment: MutableLiveData<Moment> = MutableLiveData()
        val moment: LiveData<Moment> = _moment

        val content: MutableLiveData<String> = MutableLiveData()

        fun setMoment(curMoment: Moment?) {
            curMoment?.let {
                _moment.value = it
            }
        }


    }

}