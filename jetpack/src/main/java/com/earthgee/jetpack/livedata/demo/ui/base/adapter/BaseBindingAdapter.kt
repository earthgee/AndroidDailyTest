package com.earthgee.jetpack.livedata.demo.ui.base.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 *  Created by zhaoruixuan1 on 2023/12/20
 *  CopyRight (c) haodf.com
 *  功能：
 */
abstract class BaseBindingAdapter<M, B : ViewDataBinding>(
    itemCallback: DiffUtil.ItemCallback<M>
) : ListAdapter<M, RecyclerView.ViewHolder>(itemCallback) {

    private var onItemClickCallback: OnItemClickCallback<M>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<B>(
            LayoutInflater.from(parent.context),
            getLayoutResId(viewType),
            parent,
            false
        )
        val holder = BaseBindingViewHolder(binding.root)
        holder.itemView.setOnClickListener {
            val position = holder.bindingAdapterPosition
            onItemClickCallback?.onItemClick?.invoke(getItem(position), position)
        }
        holder.itemView.setOnLongClickListener {
            val position = holder.bindingAdapterPosition
            onItemClickCallback?.onItemLongClick?.invoke(getItem(position), position)
            true
        }
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = DataBindingUtil.getBinding<B>(holder.itemView) ?: return
        onBindItem(binding, getItem(position), holder)
        binding.executePendingBindings()
    }

    fun setItemClickCallback(callbackBuilder: OnItemClickCallback<M>.() -> Unit) {
        onItemClickCallback = OnItemClickCallback<M>().also { it.callbackBuilder() }
    }

    abstract fun getLayoutResId(viewType: Int): Int

    abstract fun onBindItem(binding: B, item: M, holder: RecyclerView.ViewHolder)

    class BaseBindingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class OnItemClickCallback<M> {
        var onItemClick: (item: M, position: Int) -> Unit = { _, _ -> }
        var onItemLongClick: (item: M, position: Int) -> Unit = { _, _ -> }
    }


}