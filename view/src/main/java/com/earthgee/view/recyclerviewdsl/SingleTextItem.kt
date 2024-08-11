package com.earthgee.view.recyclerviewdsl

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.earthgee.view.R

class SingleTextItem(val content: String): Item {

    companion object Controller: ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.rv_item_text, parent, false)
            val textView = view.findViewById<TextView>(R.id.tv_text)
            return ViewHolder(view, textView)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as ViewHolder
            item as SingleTextItem
            holder.textView.text = item.content
        }

        private class ViewHolder(itemView: View, val textView: TextView): RecyclerView.ViewHolder(itemView)
    }

    override val controller: ItemController
        get() = Controller
}

fun MutableList<Item>.singleText(content: String) = add(SingleTextItem(content))