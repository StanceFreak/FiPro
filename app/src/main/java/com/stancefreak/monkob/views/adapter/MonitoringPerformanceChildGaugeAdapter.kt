package com.stancefreak.monkob.views.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.anastr.speedviewlib.components.Style
import com.stancefreak.monkob.R
import com.stancefreak.monkob.databinding.ItemListPerformanceChildGaugeBinding
import com.stancefreak.monkob.remote.model.response.Util
import kotlin.math.ceil
import kotlin.math.round

class MonitoringPerformanceChildGaugeAdapter: RecyclerView.Adapter<MonitoringPerformanceChildGaugeAdapter.RecyclerViewHolder>() {

    private val parentList = ArrayList<Util>()
    private var childId = 0
    inner class RecyclerViewHolder(private val binding: ItemListPerformanceChildGaugeBinding): RecyclerView.ViewHolder(binding.root) {

        private var itemUnits = ""
        private var finalSpeed = 0.0F
        fun bind(item: Util) {
            binding.apply {
                when (item.value.toFloat()) {
                    in 0.0 .. 1000.0 -> {
                        itemUnits = "B/s"
                        finalSpeed = round(item.value.toFloat())
                    }
                    in 1000.1 .. 1000000.0 -> {
                        itemUnits = "KB/s"
                        finalSpeed = round(item.value.toFloat() / 1000)
                    }
                    in 1000000.1 .. 1000000000.0 -> {
                        itemUnits = "MB/s"
                        finalSpeed = round(item.value.toFloat() / 1000000)
                    }
                    else -> {
                        itemUnits = "GB/s"
                        finalSpeed = round(item.value.toFloat() / 1000000000)
                    }
                }
                tvPerformanceChildSpeedValue.text = "${finalSpeed}${itemUnits}"
                svPerformanceChildSpeed.apply {
                    speedTo(finalSpeed/1000000)
                }
                if (childId == 0) {
                    svPerformanceChildSpeed.apply {
                        makeSections(11, Color.CYAN, Style.BUTT)
                        sections[0].color = Color.GREEN
                        sections[1].color = Color.GREEN
                        sections[2].color = Color.GREEN
                        sections[3].color = Color.GREEN
                        sections[4].color = Color.GREEN
                        sections[5].color = Color.GREEN
                        sections[6].color = Color.YELLOW
                        sections[7].color = Color.YELLOW
                        sections[8].color = Color.YELLOW
                        sections[9].color = Color.RED
                        sections[10].color = Color.RED
                    }
                    val finalPackets = when (item.packets.toFloat()) {
                        in 0.0 .. 1000.0 -> {
                            "${round(item.packets.toFloat())}B"
                        } in 1000.1 .. 1000000.0 -> {
                            "${round(item.packets.toFloat() / 1000)}KB"
                        } in 1000000.1 .. 1000000000.0 -> {
                            "${round(item.packets.toFloat() / 1000000)}MB"
                        }else -> {
                            "${round(item.packets.toFloat() / 1000000000)}GB"
                        }
                    }
                    when (item.direction) {
                        "transmit" -> {
                            tvPerformanceChildDirection.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_transmit, 0, 0, 0)
                            tvPerformanceChildDirection.text = "transmitted a total of $finalPackets data"
                        }
                        "receive" -> {
                            tvPerformanceChildDirection.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_receive, 0, 0, 0)
                            tvPerformanceChildDirection.text = "received a total of $finalPackets data"
                        }
                    }
                }
                else {
                    svPerformanceChildSpeed.apply {
                        makeSections(11, Color.CYAN, Style.BUTT)
                        sections[0].color = Color.RED
                        sections[1].color = Color.YELLOW
                        sections[2].color = Color.GREEN
                        sections[3].color = Color.GREEN
                        sections[4].color = Color.GREEN
                        sections[5].color = Color.GREEN
                        sections[6].color = Color.GREEN
                        sections[7].color = Color.GREEN
                        sections[8].color = Color.YELLOW
                        sections[9].color = Color.YELLOW
                        sections[10].color = Color.RED
                    }
                    tvPerformanceChildDirection.apply {
                        setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_hard_drive, 0, 0, 0)
                        text = "${item.direction}:"
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val itemBinding = ItemListPerformanceChildGaugeBinding.inflate(
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