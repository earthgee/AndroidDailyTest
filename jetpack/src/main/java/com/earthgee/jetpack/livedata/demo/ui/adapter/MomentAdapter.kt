package com.earthgee.jetpack.livedata.demo.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.earthgee.jetpack.R
import com.earthgee.jetpack.databinding.JetpackAdapterMomentBinding
import com.earthgee.jetpack.livedata.demo.data.Moment
import com.earthgee.jetpack.livedata.demo.ui.base.adapter.SimpleBindingAdapter

/**
 *  Created by zhaoruixuan1 on 2023/12/20
 *  CopyRight (c) haodf.com
 *  功能：
 */
class MomentAdapter(
    layoutId: Int = R.layout.jetpack_adapter_moment,
    itemCallback: DiffUtil.ItemCallback<Moment> = object : DiffUtil.ItemCallback<Moment>() {
        override fun areItemsTheSame(oldItem: Moment, newItem: Moment) =
            oldItem.uuid == newItem.uuid

        override fun areContentsTheSame(oldItem: Moment, newItem: Moment) =
            oldItem.content == newItem.content
    }
) : SimpleBindingAdapter<Moment, JetpackAdapterMomentBinding>(layoutId, itemCallback) {
    override fun onBindItem(
        binding: JetpackAdapterMomentBinding,
        item: Moment,
        holder: RecyclerView.ViewHolder
    ) {
        binding.moment = item
    }
}