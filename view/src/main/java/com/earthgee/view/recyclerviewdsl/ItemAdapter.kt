package com.earthgee.view.recyclerviewdsl

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ItemAdapter(val itemManager: ItemManager)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    init {
        itemManager.observer = this
    }

    override fun getItemViewType(position: Int) =
        ItemManager.getViewType(itemManager[position].controller)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ItemManager.getController(viewType).onCreateViewHolder(parent)

    override fun getItemCount() = itemManager.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        itemManager[position].controller.onBindViewHolder(holder, itemManager[position])
    }

}

fun RecyclerView.withItems(items: List<Item>) {
    adapter = ItemAdapter(ItemManager(items.toMutableList()))
}

fun RecyclerView.withItems(init: MutableList<Item>.() -> Unit) =
    withItems(mutableListOf<Item>().apply(init))




