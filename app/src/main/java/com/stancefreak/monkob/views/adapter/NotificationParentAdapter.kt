package com.stancefreak.monkob.views.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stancefreak.monkob.databinding.ItemListNotificationParentBinding
import com.stancefreak.monkob.remote.model.response.NotificationParentResponse
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

class NotificationParentAdapter: RecyclerView.Adapter<NotificationParentAdapter.RecyclerViewHolder>() {

    private val parentList = ArrayList<NotificationParentResponse>()
    inner class RecyclerViewHolder(private val binding: ItemListNotificationParentBinding): RecyclerView.ViewHolder(binding.root) {

        private var notifChildAdapter = NotificationChildAdapter()
        fun bind(item: NotificationParentResponse) {
            binding.apply {
                val formatDate = SimpleDateFormat("dd/MM/yyyy")
                val retrieveTime = formatDate.parse(item.header)
                val currentTime = Date()
                val timeDiff = currentTime.time - retrieveTime?.time!!

                when (TimeUnit.MILLISECONDS.toDays(timeDiff)) {
                    0L -> {
                        tvNotifGroupTitle.text = "Today"
                    }
                    1L -> {
                        tvNotifGroupTitle.text = "Yesterday"
                    }
                    else -> {
                        tvNotifGroupTitle.text = item.header
                    }
                }
                rvNotifSubgroupList.apply {
                    layoutManager = LinearLayoutManager(itemView.context)
                    adapter = notifChildAdapter
                }
                notifChildAdapter.setData(item.data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val itemBinding = ItemListNotificationParentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecyclerViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return this.parentList.size
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val data = this.parentList[position]
        holder.bind(data)
    }

    fun setData(dataList: ArrayList<NotificationParentResponse>) {
        dataList.sortByDescending {
            it.id
        }
        this.parentList.apply {
            clear()
            addAll(dataList)
        }
        notifyDataSetChanged()
    }
}