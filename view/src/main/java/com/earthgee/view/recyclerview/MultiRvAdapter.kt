package com.earthgee.view.recyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import java.lang.reflect.ParameterizedType

class MultiRvAdapter(
    private var proxyList: MutableList<RVProxy<*, *>> = mutableListOf(),
    var dataList: MutableList<Any> = mutableListOf()
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun <T, VH: ViewHolder> addProxy(proxy: RVProxy<T, VH>) {
        proxyList.add(proxy)
    }

    fun <T, VH: ViewHolder> removeProxy(proxy: RVProxy<T, VH>) {
        proxyList.remove(proxy)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return proxyList[viewType].onCreateViewHolder(parent, viewType)
    }

    override fun getItemCount() = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        return (proxyList[getItemViewType(position)] as RVProxy<Any, ViewHolder>)
            .onBindViewHolder(holder, dataList[position], position)
    }

    override fun getItemViewType(position: Int): Int {
        return getProxyIndex(dataList[position])
    }

    private fun getProxyIndex(data: Any): Int = proxyList.indexOfFirst {
        (it.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0].toString() ==
                data.javaClass.toString()
    }

}


