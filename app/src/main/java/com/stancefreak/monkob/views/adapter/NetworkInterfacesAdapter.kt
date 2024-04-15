package com.stancefreak.monkob.views.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stancefreak.monkob.databinding.ItemListNetInterfaceBinding
import com.stancefreak.monkob.remote.model.response.ServerNetworkUtil
import com.stancefreak.monkob.remote.model.response.UtilCalc

class NetworkInterfacesAdapter: RecyclerView.Adapter<NetworkInterfacesAdapter.RecyclerViewHolder>() {

    private val interfacesData = ArrayList<ServerNetworkUtil>()
    inner class RecyclerViewHolder(private val binding: ItemListNetInterfaceBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ServerNetworkUtil) {
            binding.apply {
                val listUtil = ArrayList<UtilCalc>()
                var totalBandwidth = 0.0
                var totalPackets = 0.0
                for (item in interfacesData) {
                    for (innerItem in item.utils) {
                        listUtil.add(UtilCalc(item.device, innerItem.value.toDouble(), innerItem.packets.toDouble()))
                    }
                }
                for (item in listUtil) {
                    if (item.device == data.device) {
                        totalBandwidth += item.value
                        totalPackets += item.packets
                    }
                }
                tvInterfaceName.text = "${data.device}: ${totalBandwidth}KiB/s (${totalPackets}KiB total)"
                tvInterfaceTransmit.text = "Transmit: ${data.utils[1].value}KiB/s (${data.utils[1].packets}KiB total)"
                tvInterfaceReceive.text = "Transmit: ${data.utils[0].value}KiB/s (${data.utils[0].packets}KiB total)"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val itemBinding = ItemListNetInterfaceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecyclerViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return interfacesData.size
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val data = interfacesData[position]
        holder.bind(data)
    }

    fun setData(dataList: List<ServerNetworkUtil>) {
        interfacesData.apply {
            clear()
            addAll(dataList)
        }
        notifyDataSetChanged()
    }
}