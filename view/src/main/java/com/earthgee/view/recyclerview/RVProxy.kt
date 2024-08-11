package com.earthgee.view.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.earthgee.view.R

abstract class RVProxy<T, VH: RecyclerView.ViewHolder> {

    abstract fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH
    abstract fun onBindViewHolder(holder: VH, data: T, position: Int)
    //局部刷新
    open fun onBindViewHolder(holder: VH, data: T, position: Int,
                                  payloads: MutableList<Any>) {
        onBindViewHolder(holder, data, position)
    }

}

//几种示例
class TextProxy: RVProxy<Text, TextViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_text, parent, false)
        return TextViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: TextViewHolder,
        data: Text,
        position: Int
    ) {
        holder.tvText.text = data.text
    }

}

data class Text(var text: String)
class TextViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val tvText = itemView.findViewById<TextView>(R.id.tv_text)
}
