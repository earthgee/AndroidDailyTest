package com.earthgee.jetpack.livedata.demo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.earthgee.jetpack.R
import com.earthgee.jetpack.databinding.JetpackFragmentListBinding
import com.earthgee.jetpack.livedata.demo.data.DataRepository
import com.earthgee.jetpack.livedata.demo.data.Moment
import com.earthgee.jetpack.livedata.demo.domain.message.PageMessenger
import com.earthgee.jetpack.livedata.demo.ui.adapter.MomentAdapter
import com.earthgee.jetpack.livedata.demo.ui.base.BaseFragment

/**
 *  Created by zhaoruixuan1 on 2023/12/19
 *  CopyRight (c) haodf.com
 *  功能：
 */
class ListFragment : BaseFragment() {

    private val mState by lazy {
        getFragmentViewModel<ListViewModel>()
    }

    private val mMessenger by lazy {
        getActivityViewModel<PageMessenger>()
    }

    private var adapter: MomentAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.jetpack_fragment_list, container, false)
        val binding = DataBindingUtil.bind<JetpackFragmentListBinding>(view)
        binding?.lifecycleOwner = viewLifecycleOwner
        binding?.vm = mState
        binding?.click = ClickProxy()

        adapter = MomentAdapter().apply {
            setItemClickCallback {
                onItemClick = { item: Moment, position: Int ->
                    val bundle = Bundle().apply {
                        putParcelable(Moment.MOMENT, item)
                    }
                    nav.navigate(R.id.action_listFragment_to_detailFragment, bundle)
                }
                onItemLongClick = { _: Moment, _: Int ->
                    //none
                }
            }
        }
        binding?.adapter = adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mMessenger.momentResult.observe(viewLifecycleOwner) {
            mState.notifyGlobalChange(it)
        }

        mMessenger.testDelayMsgResult.observe(viewLifecycleOwner) {
            showLongToast(it)
        }

        mState.list.observe(viewLifecycleOwner) { items ->
            adapter?.submitList(items)
        }

        mState.requestList()

    }

    inner class ClickProxy {

        fun fabClick() {

        }

    }

    class ListViewModel : ViewModel() {
        private val _list: MutableLiveData<List<Moment>> = MutableLiveData<List<Moment>>()
        val list: LiveData<List<Moment>> = _list

        private val _autoScrollToTopWhenInsert: MutableLiveData<Boolean> = MutableLiveData()
        val autoScrollToTopWhenInsert: LiveData<Boolean> = _autoScrollToTopWhenInsert

        fun requestList() {
            if(_list.value == null || _list.value?.isEmpty() == true) {
                DataRepository.requestList(_list)
            }
        }

        fun notifyGlobalChange(moment: Moment) {
            val dataList = ArrayList(_list.value ?: return)

            var modify = false
            dataList.forEachIndexed { index, it ->
                if(it.uuid == moment.uuid) {
                    dataList[index] = moment
                    modify = true
                    return@forEachIndexed
                }
            }

            if(!modify) {
                dataList.add(0, moment)
            }
            _list.value = dataList
        }

    }

}