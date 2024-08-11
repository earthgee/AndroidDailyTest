package com.earthgee.view.recyclerviewdsl

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class ItemManager(private val delegated: MutableList<Item> = mutableListOf())
    : MutableList<Item> by delegated {

    var observer: RecyclerView.Adapter<RecyclerView.ViewHolder>? = null

    init {
        ensureControllers(this)
    }

    companion object {

        private var viewType = 0
        //controller to viewType
        private val c2vt = mutableMapOf<ItemController, Int>()
        //viewType to controller
        private val vt2c = mutableMapOf<Int, ItemController>()

        private fun ensureController(item: Item) {
            val controller = item.controller
            if(!c2vt.containsKey(controller)) {
                c2vt[controller] = viewType
                vt2c[viewType] = controller
                viewType++
            }
        }

        private fun ensureControllers(item: Collection<Item>) {
            item.distinctBy(Item::controller).forEach(::ensureController)
        }

        fun getViewType(controller: ItemController): Int =
            c2vt[controller] ?: throw IllegalStateException("noInit")

        fun getController(viewType: Int): ItemController =
            vt2c[viewType] ?: throw IllegalStateException("noInit")

    }

    override fun add(element: Item) =
        delegated.add(element).also {
            ensureController(element)
            if(it) {
                observer?.notifyItemInserted(size)
            }
        }

    override fun add(index: Int, element: Item) =
        delegated.add(index, element).also {
            ensureController(element)
            observer?.notifyItemInserted(index)
        }

    override fun addAll(index: Int, elements: Collection<Item>): Boolean =
        delegated.addAll(index, elements).also {
            ensureControllers(elements)
            if(it) {
                observer?.notifyItemRangeChanged(index, elements.size)
            }
        }

    override fun addAll(elements: Collection<Item>): Boolean =
        delegated.addAll(elements).also {
            ensureControllers(elements)
            if(it) {
                observer?.notifyItemRangeChanged(size, elements.size)
            }
        }

    override fun clear() {
        delegated.clear().also {
            observer?.notifyItemRangeRemoved(0, size)
        }
    }

    override fun remove(element: Item): Boolean =
        delegated.remove(element).also {
            observer?.notifyDataSetChanged()
        }

    override fun removeAll(elements: Collection<Item>): Boolean =
        delegated.removeAll(elements).also {
            observer?.notifyDataSetChanged()
        }

    override fun removeAt(index: Int): Item =
        delegated.removeAt(index).also {
            observer?.notifyItemRemoved(index)
        }

    fun refreshAll(elements: List<Item>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize() = size

            override fun getNewListSize() = elements.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = delegated[oldItemPosition]
                val newItem = elements[newItemPosition]
                return oldItem.areItemTheSame(newItem)
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = delegated[oldItemPosition]
                val newItem = elements[newItemPosition]
                return oldItem.areContentsTheSame(newItem)
            }
        }
        val result = DiffUtil.calculateDiff(diffCallback, true)
        delegated.clear()
        delegated.addAll(elements)
        ensureControllers(elements)
        result.dispatchUpdatesTo(observer?:return)
    }

    fun refreshAll(init: MutableList<Item>.() -> Unit) =
        refreshAll(mutableListOf<Item>().apply(init))

    fun autoRefresh(init: MutableList<Item>.() -> Unit) {
        val origin = toMutableList().apply(init)
        refreshAll(origin)
    }

}






