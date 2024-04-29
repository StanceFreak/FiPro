package com.stancefreak.monkob.views.adapter

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stancefreak.monkob.R
import com.stancefreak.monkob.databinding.ItemListPerformanceChildBinding
import com.stancefreak.monkob.remote.model.response.ServerPerformanceUtil
import com.stancefreak.monkob.remote.model.response.UtilCalc
import java.text.DecimalFormat
import kotlin.math.ceil
import kotlin.math.round

class MonitoringPerformanceChildAdapter(private val mode: String): RecyclerView.Adapter<MonitoringPerformanceChildAdapter.RecyclerViewHolder>() {

    private val parentList = ArrayList<ServerPerformanceUtil>()
    private var childId = 0
    inner class RecyclerViewHolder(private val binding: ItemListPerformanceChildBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ServerPerformanceUtil) {
            binding.apply {
                val listUtil = ArrayList<UtilCalc>()
                var totalBandwidth = 0.0
                var totalPackets = 0.0
                for (i in parentList) {
                    for (innerItem in i.utils) {
                        listUtil.add(UtilCalc(i.name, innerItem.value.toDouble(), innerItem.packets.toDouble()))
                    }
                }
                for (i in listUtil) {
                    if (i.device == item.name) {
                        totalBandwidth += i.value
                        totalPackets += i.packets
                    }
                }
                val interfaceSpeed = when (totalBandwidth) {
                    in 0.0 .. 1000.0 -> {
                        "${round(totalBandwidth.toFloat())}B/s"
                    }
                    in 1000.1 .. 1000000.0 -> {
                        "${round(totalBandwidth.toFloat() / 1000)}KB/s"
                    }
                    else -> {
                        "${round(totalBandwidth.toFloat() / 1000000)}MB/s"
                    }
                }
                if (childId == 0) {
                    tvPerformanceChildName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_net_interface, 0, 0, 0)
                    val interfacePackets = when (totalPackets) {
                        in 0.0 .. 1000.0 -> {
                            "${round(totalPackets.toFloat())}B total"
                        } in 1000.1 .. 1000000.0 -> {
                            "${round(totalPackets.toFloat() / 1000)}KB total"
                        } else -> {
                            "${round(totalPackets.toFloat() / 1000000)}MB total"
                        }
                    }
                    tvPerformanceChildName.text = "${item.name}: ${interfaceSpeed} (${interfacePackets})"
                    if (mode == "text") {
                        val textAdapter = MonitoringPerformanceChildTextAdapter()
                        rvPerformanceChildGauge.isGone = true
                        rvPerformanceChildText.apply {
                            isGone = false
                            val lm = LinearLayoutManager(itemView.context)
                            lm.reverseLayout = true
                            layoutManager = lm
                            adapter = textAdapter
                        }
                        textAdapter.setData(item.utils, childId)
                    }
                    else if (mode == "gauge") {
                        val gaugeAdapter = MonitoringPerformanceChildGaugeAdapter()
                        rvPerformanceChildText.isGone = true
                        rvPerformanceChildGauge.apply {
                            isGone = false
                            val lm = LinearLayoutManager(itemView.context)
                            lm.reverseLayout = true
                            layoutManager = lm
                            adapter = gaugeAdapter
                        }
                        gaugeAdapter.setData(item.utils, childId)
                    }
                }
                else {
                    tvPerformanceChildName.apply {
                        setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_drive, 0, 0, 0)
                        text = "${item.name}: $interfaceSpeed"
                    }
                    if (mode == "text") {
                        val textAdapter = MonitoringPerformanceChildTextAdapter()
                        rvPerformanceChildGauge.isGone = true
                        rvPerformanceChildText.apply {
                            isGone = false
                            layoutManager = LinearLayoutManager(itemView.context)
                            adapter = textAdapter
                        }
                        textAdapter.setData(item.utils, childId)
                    }
                    else if (mode == "gauge") {
                        val gaugeAdapter = MonitoringPerformanceChildGaugeAdapter()
                        rvPerformanceChildText.isGone = true
                        rvPerformanceChildGauge.apply {
                            isGone = false
                            layoutManager = LinearLayoutManager(itemView.context)
                            adapter = gaugeAdapter
                        }
                        gaugeAdapter.setData(item.utils, childId)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val itemBinding = ItemListPerformanceChildBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecyclerViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return this.parentList.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val data = this.parentList[position]
        holder.bind(data)
    }

    fun setData(dataList: List<ServerPerformanceUtil>, id: Int) {
        this.childId = id
        this.parentList.apply {
            clear()
            addAll(dataList)
        }
        notifyDataSetChanged()
    }
}