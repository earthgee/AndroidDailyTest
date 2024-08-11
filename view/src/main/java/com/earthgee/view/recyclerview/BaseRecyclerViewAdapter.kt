package com.earthgee.view.recyclerview

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<T> : RecyclerView.Adapter<BaseViewHolder>() {

    companion object {
        const val TYPE_EMPTY_VIEW = -1
        const val TYPE_CONTENT = -2
    }

    protected var datas: MutableList<T>? = null
    private lateinit var emptyView: View
    private var currentType = 0

    init {
        registerAdapterDataObserver(DataObserver())
    }

    private inner class DataObserver: RecyclerView.AdapterDataObserver() {

        override fun onChanged() {
            currentType = if(datas != null && datas!!.size != 0) {
                TYPE_CONTENT
            } else {
                TYPE_EMPTY_VIEW
            }
        }

    }

    fun setData(datas: MutableList<T>) {
        this.datas = datas
        notifyDataSetChanged()
    }

    fun setEmptyView(epmtyView: View) {
        this.emptyView = emptyView
    }

    protected abstract fun createHolder(parent: ViewGroup, viewType: Int): BaseViewHolder
    protected abstract fun bindHolder(holder: BaseViewHolder, position: Int)
    protected abstract val count: Int
    protected abstract fun getViewType(position: Int): Int

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val viewHolder: BaseViewHolder
        when(viewType) {
            TYPE_EMPTY_VIEW -> viewHolder = BaseViewHolder(emptyView)
            else -> viewHolder = createHolder(parent, viewType)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val itemViewType = getItemViewType(position)
        when(itemViewType) {
            TYPE_EMPTY_VIEW -> {}
            else -> bindHolder(holder, position)
        }
    }

    override fun getItemCount(): Int =
        if(currentType == TYPE_EMPTY_VIEW) 1 else count

    override fun getItemViewType(position: Int) =
        if(datas == null || datas!!.size == 0) {
            TYPE_EMPTY_VIEW
        } else {
            getViewType(position)
        }

}

class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)







