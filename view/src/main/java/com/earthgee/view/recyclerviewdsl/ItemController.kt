package com.earthgee.view.recyclerviewdsl

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

interface ItemController {

    fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
    fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item)

}

interface Item {
    val controller: ItemController

    fun areItemTheSame(newItem: Item): Boolean = false

    fun areContentsTheSame(newItem: Item): Boolean = false

}
