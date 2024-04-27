package com.dicoding.asclepius.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.data.local.entity.ResultHistoryEntity
import com.dicoding.asclepius.databinding.HistoryItemRowBinding
import kotlin.properties.Delegates

class ResultHistoryAdapter : RecyclerView.Adapter<ResultHistoryAdapter.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    inner class ListViewHolder(private val binding: HistoryItemRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(history: ResultHistoryEntity) {
            with(binding) {
                tvTimestamp.text = history.date

                root.setOnClickListener {
                    onItemClickCallback.onItemClicked(history)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ResultHistoryAdapter.ListViewHolder {
        val binding =
            HistoryItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResultHistoryAdapter.ListViewHolder, position: Int) {
        val data = listHistory[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int = listHistory.size

    private fun notifyChanges(
        oldList: List<ResultHistoryEntity>,
        newList: List<ResultHistoryEntity>
    ) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition].id == newList[newItemPosition].id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition] == newList[newItemPosition]
            }

            override fun getOldListSize() = oldList.size

            override fun getNewListSize() = newList.size
        })

        diff.dispatchUpdatesTo(this)
    }

    var listHistory: List<ResultHistoryEntity> by Delegates.observable(emptyList()) { _, oldList, newList ->
        notifyChanges(oldList, newList)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(history: ResultHistoryEntity)
    }
}