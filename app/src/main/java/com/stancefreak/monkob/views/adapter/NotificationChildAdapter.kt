package com.stancefreak.monkob.views.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.stancefreak.monkob.databinding.ItemListNotificationChildBinding
import com.stancefreak.monkob.remote.model.response.ServerNotifRecord
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

class NotificationChildAdapter: RecyclerView.Adapter<NotificationChildAdapter.RecyclerViewHolder>() {

    private val parentList = ArrayList<ServerNotifRecord>()
    inner class RecyclerViewHolder(private val binding: ItemListNotificationChildBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ServerNotifRecord) {
            binding.apply {
                val formatDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                val retrieveTime = formatDate.parse(item.createdAt)
                val currentTime = Date()
                val timeDiff = currentTime.time - retrieveTime?.time!!

                val second = TimeUnit.MILLISECONDS.toSeconds(timeDiff)
                val minute = TimeUnit.MILLISECONDS.toMinutes(timeDiff)
                val hour = TimeUnit.MILLISECONDS.toHours(timeDiff)
                val day = TimeUnit.MILLISECONDS.toDays(timeDiff)

                when {
                    second <= 10 -> {
                        tvNotifSubgroupTime.text = "Just now"
                    }
                    second < 60 -> {
                        tvNotifSubgroupTime.text = "${second}s ago"
                    }
                    minute < 60 -> {
                        tvNotifSubgroupTime.text = "${minute}m ago"
                    }
                    hour < 24 -> {
                        tvNotifSubgroupTime.text = "${hour}h ago"
                    }
                    day < 7 -> {
                        tvNotifSubgroupTime.text = "${day}d ago"
                    }
                    day >= 7 -> {
                        tvNotifSubgroupTime.text = "${day/7}w ago"
                    }
                }

                tvNotifSubgroupTitle.text = item.title
                tvNotifSubgroupDesc.text = item.body
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val itemBinding = ItemListNotificationChildBinding.inflate(
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

    fun setData(dataList: List<ServerNotifRecord>) {
        this.parentList.apply {
            clear()
            addAll(dataList)
            sortByDescending {
                it.createdAt
            }
        }
        notifyDataSetChanged()
    }
}