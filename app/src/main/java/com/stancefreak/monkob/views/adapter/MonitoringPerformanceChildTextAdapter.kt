package com.stancefreak.monkob.views.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stancefreak.monkob.R
import com.stancefreak.monkob.databinding.ItemListPerformanceChildGaugeBinding
import com.stancefreak.monkob.databinding.ItemListPerformanceChildTextBinding
import com.stancefreak.monkob.remote.model.response.Util
import java.text.NumberFormat
import kotlin.math.ceil
import kotlin.math.round

class MonitoringPerformanceChildTextAdapter: RecyclerView.Adapter<MonitoringPerformanceChildTextAdapter.RecyclerViewHolder>() {

    private val parentList = ArrayList<Util>()
    private var childId = 0
    inner class RecyclerViewHolder(private val binding: ItemListPerformanceChildTextBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Util) {
            binding.apply {
                val finalSpeed = when (item.value.toFloat()) {
                    in 0.0 .. 1000.0 -> {
                        "${round(item.value.toFloat())}B/s"
                    }
                    in 1000.1 .. 1000000.0 -> {
                        "${round(item.value.toFloat() / 1000)}KB/s"
                    }
                    else -> {
                        "${round(item.value.toFloat() / 1000000)}MB/s"
                    }
                }
                if (childId == 0) {
                    val finalPackets = when (item.packets.toFloat()) {
                        in 0.0 .. 1000.0 -> {
                            "${round(item.packets.toFloat())}B total"
                        } in 1000.1 .. 1000000.0 -> {
                            "${round(item.packets.toFloat() / 1000)}KB total"
                        } else -> {
                            "${round(item.packets.toFloat() / 1000000)}MB total"
                        }
                    }
                    when (item.direction) {
                        "transmit" -> {
                            tvPerformanceChildDirection.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_transmit, 0, 0, 0)
                        }
                        "receive" -> {
                            tvPerformanceChildDirection.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_receive, 0, 0, 0)
                        }
                    }
                    tvPerformanceChildDirection.text = "${item.direction} : $finalSpeed ($finalPackets)"
                }
                else {
                    tvPerformanceChildDirection.apply {
                        setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_hard_drive, 0, 0, 0)
                        text = "${item.direction} : $finalSpeed"
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val itemBinding = ItemListPerformanceChildTextBinding.inflate(
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

    fun setData(dataList: List<Util>, id: Int) {
        this.childId = id
        this.parentList.apply {
            clear()
            addAll(dataList)
        }
        notifyDataSetChanged()
    }
}