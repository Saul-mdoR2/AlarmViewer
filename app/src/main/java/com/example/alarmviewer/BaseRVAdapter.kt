package com.example.alarmviewer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

abstract class BaseRVAdapter<IT : Any, IVM : ViewModel, VDB : ViewDataBinding>(private val clickListener: (IT) -> Unit) :
    RecyclerView.Adapter<BaseRVAdapter<IT, IVM, VDB>.ViewHolder>() {

    lateinit var viewModel: IVM
    private lateinit var layout: VDB

    var itemList: List<IVM> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class ViewHolder(val itemBinding: VDB) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(item: IT, clickListener: (IT) -> Unit) {
            itemView.setOnClickListener { clickListener(item) }
        }
    }

    protected abstract fun inflateDataBinding(inflater: LayoutInflater, container: ViewGroup?): VDB

    protected abstract fun getBindItem(itemViewModel: IVM): IT?

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        inflateDataBinding(LayoutInflater.from(parent.context), parent).run {
            layout = this
            ViewHolder(layout)
        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val currentItem = itemList[position]
            //holder.itemBinding.setVariable(BR.viewModel, currentItem)
            holder.itemBinding.setVariable(BR.viewModel, currentItem)
            getBindItem(currentItem)?.let { item ->
                holder.bind(item, clickListener)
            }
            holder.itemBinding.executePendingBindings()
        } catch (e: Exception) {
            Timber.d("BaseRVAdapter_TAG: onBindViewHolder: exception: $e")
        }
    }

    override fun getItemCount(): Int = itemList.size
}