package com.stancefreak.monkob.views.adapter

import android.graphics.DashPathEffect
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isGone
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
                        viewModel.apply {
                            fetchCpuRecords(queryInterval)
                            observeServerCpuUtilsRecord().observe(lifecycleOwner) {
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
                            observeCpuLoading().observe(lifecycleOwner) {
                                it.getContentIfNotHandled()?.let { loading ->
                                    if (loading) {
                                        clHistoryChartLoading.isGone = false
                                        clHistoryChartEmpty.isGone = true
                                        lcHistoryLatencyChart.isGone = true
                                    }
                                }
                            }
                        }
                    }
                    1 -> {
                        viewModel.apply {
                            fetchMemRecords(queryInterval)
                            observeServerMemUtilsRecord().observe(lifecycleOwner) {
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
                            observeMemLoading().observe(lifecycleOwner) {
                                it.getContentIfNotHandled()?.let { loading ->
                                    if (loading) {
                                        clHistoryChartLoading.isGone = false
                                        clHistoryChartEmpty.isGone = true
                                        lcHistoryLatencyChart.isGone = true
                                    }
                                }
                            }
                        }

                    }
                    2 -> {
                        viewModel.apply {
                            fetchLatencyRecords(queryInterval)
                            observeServerNetLatencyRecord().observe(lifecycleOwner) {
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
                            observeLatencyLoading().observe(lifecycleOwner) {
                                it.getContentIfNotHandled()?.let { loading ->
                                    if (loading) {
                                        clHistoryChartLoading.isGone = false
                                        clHistoryChartEmpty.isGone = true
                                        lcHistoryLatencyChart.isGone = true
                                    }
                                }
                            }
                        }
                    }
                }

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
            }
        }

        private fun chartData(dataList: ArrayList<Entry>, labelList: ArrayList<String>) {
            binding.apply {
                if (dataList.isEmpty() || labelList.isEmpty()) {
                    clHistoryChartLoading.isGone = true
                    clHistoryChartEmpty.isGone = false
                    lcHistoryLatencyChart.isGone = true
                }
                else {
                    val l = lcHistoryLatencyChart.legend
//                    l.isEnabled = false
                    l.form = Legend.LegendForm.LINE
                    clHistoryChartLoading.isGone = true
                    clHistoryChartEmpty.isGone = true
                    lcHistoryLatencyChart.apply {
                        isGone = false
                        description.isEnabled = false
                        setExtraOffsets(0F,0F,0F,10F)
                        setTouchEnabled(true)
                        setOnChartValueSelectedListener(this@RecyclerViewHolder)
                        isDragEnabled = true
                        setScaleEnabled(true)
                        setPinchZoom(true)
                        setDrawGridBackground(false)
                        val xAxis = lcHistoryLatencyChart.xAxis
                        xAxis.apply {
                            disableGridDashedLine()
//                            enableGridDashedLine(10f, 10f, 0f)
                            position = XAxis.XAxisPosition.BOTTOM
                        }

                        val yAxis = lcHistoryLatencyChart.axisLeft
                        yAxis.apply {
                            axisRight.isEnabled = false
                            setDrawLabels(false)
                            setDrawAxisLine(true)
                            disableGridDashedLine()
//                            enableGridDashedLine(10f, 10f, 0f)
                            typeface = ResourcesCompat.getFont(context, R.font.os_medium)
                            setLabelCount(6, false)
                            textColor = ContextCompat.getColor(itemView.context, R.color.red)
                            textSize = 10f
                            axisMinimum = 0f
                            axisMaximum = 200f
                            setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
                        }

                        val limitLine1 = LimitLine(100f, "Critical")
                        limitLine1.apply {
                            lineColor = ContextCompat.getColor(itemView.context, R.color.red)
                            lineWidth = 4f
                            typeface = ResourcesCompat.getFont(context, R.font.os_medium)
//                            enableDashedLine(10f, 10f, 0f)
                            labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
                            textSize = 10f
                        }

                        val limitLine2 = LimitLine(50f, "Warning")
                        limitLine2.apply {
                            lineColor = ContextCompat.getColor(itemView.context, R.color.yellow_light)
                            lineWidth = 4f
                            typeface = ResourcesCompat.getFont(context, R.font.os_medium)
//                            enableDashedLine(10f, 10f, 0f)
                            labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
                            textSize = 10f
                        }

                        val limitLine3 = LimitLine(0f,"Safe")
                        limitLine3.apply {
                            lineColor = ContextCompat.getColor(itemView.context, R.color.green)
                            lineWidth = 4f
                            typeface = ResourcesCompat.getFont(context, R.font.os_medium)
//                            enableDashedLine(10f, 10f, 0f)
                            labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
                            textSize = 10f
                        }

                        xAxis.setDrawLimitLinesBehindData(true)
                        yAxis.setDrawLimitLinesBehindData(true)
                        yAxis.addLimitLine(limitLine1)
                        yAxis.addLimitLine(limitLine2)
                        yAxis.addLimitLine(limitLine3)
                        xAxis.valueFormatter = IndexAxisValueFormatter(labelList)
                        val tesData = LineDataSet(dataList, "data 1")
                        tesData.apply {
                            setDrawIcons(false)
                            enableDashedLine(10f,5f, 0f)
                            color = ContextCompat.getColor(itemView.context, R.color.black)
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
                            fillColor = ContextCompat.getColor(itemView.context, R.color.white)
                            fillFormatter = IFillFormatter { _, _ ->
                                lcHistoryLatencyChart.axisLeft.axisMinimum
                            }
                        }
                        val dataSets = ArrayList<ILineDataSet>()
                        dataSets.add(tesData)
                        val tesDataSet = LineData(dataSets)
                        data = tesDataSet
                        invalidate()
                    }
                }
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