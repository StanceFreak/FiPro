package com.stancefreak.monkob.views.adapter

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stancefreak.monkob.R
import com.stancefreak.monkob.databinding.ItemListPerformanceParentBinding
import com.stancefreak.monkob.remote.model.response.HomePerformanceResponse
import com.stancefreak.monkob.remote.model.response.UtilCalc

class MonitoringPerformanceParentAdapter: RecyclerView.Adapter<MonitoringPerformanceParentAdapter.RecyclerViewHolder>() {

    private val parentList = ArrayList<HomePerformanceResponse>()
    inner class RecyclerViewHolder(private val binding: ItemListPerformanceParentBinding): RecyclerView.ViewHolder(binding.root) {

        private var childAdapter = MonitoringPerformanceChildAdapter("text")
        fun bind(item: HomePerformanceResponse) {
            binding.apply {
                tvPerformanceItemTitle.text = item.title
                rvPerformanceItemList.apply {
                    layoutManager = LinearLayoutManager(itemView.context)
                    adapter = childAdapter
                }
                item.utilData?.let { childAdapter.setData(it, item.id) }
                rgPerformanceRvMode.setOnCheckedChangeListener { _, id ->
                    if (id == R.id.rb_performance_text_mode) {
                        this@RecyclerViewHolder.childAdapter = MonitoringPerformanceChildAdapter("text")
                        rvPerformanceItemList.apply {
                            val lm = LinearLayoutManager(itemView.context)
                            layoutManager = lm
                            lm.reverseLayout = true
                            adapter = childAdapter
                        }
                        item.utilData?.let { childAdapter.setData(it, item.id) }
                    }
                    else {
                        this@RecyclerViewHolder.childAdapter = MonitoringPerformanceChildAdapter("gauge")
                        rvPerformanceItemList.apply {
                            val lm = LinearLayoutManager(itemView.context)
                            layoutManager = lm
                            lm.reverseLayout = true
                            adapter = childAdapter
                        }
                        item.utilData?.let { childAdapter.setData(it, item.id) }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val itemBinding = ItemListPerformanceParentBinding.inflate(
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

    fun setData(dataList: List<HomePerformanceResponse>) {
        this.parentList.apply {
            clear()
            addAll(dataList)
        }
        notifyDataSetChanged()
    }
}