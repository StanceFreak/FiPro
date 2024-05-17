package com.stancefreak.monkob.views.adapter

import android.graphics.DashPathEffect
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.stancefreak.monkob.R
import com.stancefreak.monkob.databinding.ItemListHistoryBinding
import com.stancefreak.monkob.remote.model.response.ChartType
import com.stancefreak.monkob.views.history.HistoryViewModel
import java.text.DecimalFormat

class HistoryAdapter(
    private val viewModel: HistoryViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val onRetrieveData: OnRetrieveData
): RecyclerView.Adapter<HistoryAdapter.RecyclerViewHolder>() {

    interface OnRetrieveData {
        fun getQuery(query: String)
    }

    private var typeList = ArrayList<ChartType>()
    private var queryInterval = ""
    inner class RecyclerViewHolder(private val binding: ItemListHistoryBinding): RecyclerView.ViewHolder(binding.root), OnChartValueSelectedListener {
        fun bind(item: ChartType) {
            val tesDataList = ArrayList<Entry>()
            val tesDataLabels = ArrayList<String>()
            binding.apply {
                tvHistoryTypeChart.text = item.type
                onRetrieveData.getQuery(queryInterval)
                when (item.id) {
                    0 -> {
                        viewModel.fetchCpuRecords(queryInterval)
                        viewModel.observeServerCpuUtilsRecord().observe(lifecycleOwner) {
                            it.getContentIfNotHandled()?.let { response ->
                                tesDataLabels.clear()
                                tesDataList.clear()
                                for ((itemPos, i) in response.withIndex()) {
                                    tesDataList.add(Entry(itemPos.toFloat(), DecimalFormat("0.#").format(i.value).toFloat()))
                                    tesDataLabels.add(i.time)
                                }
                                chartData(tesDataList, tesDataLabels)
                            }
                        }
                    }
                    1 -> {
                        viewModel.fetchMemRecords(queryInterval)
                        viewModel.observeServerMemUtilsRecord().observe(lifecycleOwner) {
                            it.getContentIfNotHandled()?.let { response ->
                                tesDataLabels.clear()
                                tesDataList.clear()
                                for ((itemPos, i) in response.withIndex()) {
                                    tesDataList.add(Entry(itemPos.toFloat(), DecimalFormat("0.#").format(i.value).toFloat()))
                                    tesDataLabels.add(i.time)
                                }
                                chartData(tesDataList, tesDataLabels)
                            }
                        }

                    }
                    2 -> {
                        viewModel.fetchLatencyRecords(queryInterval)
                        viewModel.observeServerNetLatencyRecord().observe(lifecycleOwner) {
                            it.getContentIfNotHandled()?.let { response ->
                                tesDataLabels.clear()
                                tesDataList.clear()
                                for ((itemPos, i) in response.withIndex()) {
                                    tesDataList.add(Entry(itemPos.toFloat(), DecimalFormat("0.#").format(i.value).toFloat()))
                                    tesDataLabels.add(i.time)
                                }
                                chartData(tesDataList, tesDataLabels)
                            }
                        }
                    }
                }
                lcHistoryLatencyChart.apply {
                    description.isEnabled = false
                    setTouchEnabled(true)
                    setOnChartValueSelectedListener(this@RecyclerViewHolder)
                    isDragEnabled = true
                    setScaleEnabled(true)
                    setPinchZoom(true)
                    setDrawGridBackground(false)
                    val xAxis = lcHistoryLatencyChart.xAxis
                    xAxis.apply {
                        enableGridDashedLine(10f, 10f, 0f)
                        position = XAxis.XAxisPosition.BOTTOM
                    }

                    val yAxis = lcHistoryLatencyChart.axisLeft
                    yAxis.apply {
                        axisRight.isEnabled = false
                        setDrawLabels(false)
                        setDrawAxisLine(true)
                        enableGridDashedLine(10f, 10f, 0f)
                        typeface = ResourcesCompat.getFont(context, R.font.os_medium)
                        setLabelCount(6, false)
                        textColor = ContextCompat.getColor(itemView.context, R.color.red)
                        textSize = 10f
                        axisMinimum = -50f
                        axisMaximum = 200f
                        setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
                    }

                    val limitLineAxis = LimitLine(9f, "Index 10")
                    limitLineAxis.apply {
                        lineColor = ContextCompat.getColor(itemView.context, R.color.grey)
                        lineWidth = 4f
                        typeface = ResourcesCompat.getFont(context, R.font.os_medium)
                        enableDashedLine(10f, 10f, 0f)
                        labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
                        textSize = 10f
                    }

                    val limitLine1 = LimitLine(150f, "Upper Limit")
                    limitLine1.apply {
                        lineColor = ContextCompat.getColor(itemView.context, R.color.grey)
                        lineWidth = 4f
                        typeface = ResourcesCompat.getFont(context, R.font.os_medium)
//                    enableDashedLine(10f, 10f, 0f)
                        labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
                        textSize = 10f
                    }

                    val limitLine2 = LimitLine(-30f,"Lower Limit")
                    limitLine2.apply {
                        lineColor = ContextCompat.getColor(itemView.context, R.color.yellow)
                        lineWidth = 4f
                        typeface = ResourcesCompat.getFont(context, R.font.os_medium)
//                    enableDashedLine(10f, 10f, 0f)
                        labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
                        textSize = 10f
                    }

                    xAxis.setDrawLimitLinesBehindData(true)
                    yAxis.setDrawLimitLinesBehindData(true)
                    yAxis.addLimitLine(limitLine1)
                    yAxis.addLimitLine(limitLine2)
//
//                val xAxis = this.xAxis
//                val leftAxis = this.axisLeft
//
//                xAxis.apply {
//                    removeAllLimitLines()
//                    addLimitLine(limitLine1)
//                    addLimitLine(limitLine2)
//                }
//                leftAxis.removeAllLimitLines()
                }
//                val tesDataList = ArrayList<Entry>()
//                tesDataList.apply {
//                    add(Entry(1f, 33f))
//                    add(Entry(2f, 15f))
//                    add(Entry(3f, 14f))
//                    add(Entry(4f, 56f))
//                    add(Entry(5f, 38f))
//                    add(Entry(6f, 21f))
//                    add(Entry(7f, 10f))
//                    add(Entry(8f, 100f))
//                    add(Entry(9f, 0f))
//                }
                val l = lcHistoryLatencyChart.legend
                l.isEnabled = false
                l.form = Legend.LegendForm.LINE
            }
        }

        private fun chartData(dataList: ArrayList<Entry>, labelList: ArrayList<String>) {
            binding.apply {
                lcHistoryLatencyChart.xAxis.valueFormatter = IndexAxisValueFormatter(labelList)
                val tesData = LineDataSet(dataList, "data 1")
                tesData.apply {
                    setDrawIcons(false)
                    enableDashedLine(10f,5f, 0f)
                    color = ContextCompat.getColor(itemView.context, R.color.red)
                    setCircleColor(ContextCompat.getColor(itemView.context, R.color.grey))
                    lineWidth = 1.8f
                    circleRadius = 4f
                    formLineWidth = 1f
                    formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
                    formSize = 15f
                    valueTextSize = 9f
                    enableDashedHighlightLine(10f, 5f, 0f)
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    cubicIntensity = 0.2f
                    setDrawFilled(true)
                    setDrawCircles(false)
                    highLightColor = ContextCompat.getColor(itemView.context, R.color.yellow)
                    color = ContextCompat.getColor(itemView.context, R.color.red)
                    fillColor = ContextCompat.getColor(itemView.context, R.color.grey)
                    fillFormatter = IFillFormatter { _, _ ->
                        lcHistoryLatencyChart.axisLeft.axisMinimum
                    }
                }
                val dataSets = ArrayList<ILineDataSet>()
                dataSets.add(tesData)
                val tesDataSet = LineData(dataSets)
                lcHistoryLatencyChart.data = tesDataSet
                lcHistoryLatencyChart.invalidate()
            }
        }

        override fun onValueSelected(e: Entry?, h: Highlight?) {
        }

        override fun onNothingSelected() {
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val itemBinding = ItemListHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecyclerViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return this.typeList.size
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val data = this.typeList[position]
        holder.bind(data)
    }

    fun setParentData(dataList: ArrayList<ChartType>, query: String) {
        this.queryInterval = query
        this.typeList.apply {
            clear()
            addAll(dataList)
        }
        notifyDataSetChanged()
    }
}