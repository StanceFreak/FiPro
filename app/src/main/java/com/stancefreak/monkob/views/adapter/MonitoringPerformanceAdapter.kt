package com.stancefreak.monkob.views.adapter

import android.content.Context
import android.graphics.DashPathEffect
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import com.stancefreak.monkob.databinding.ItemListPerformanceBinding
import com.stancefreak.monkob.model.response.ChartType

class MonitoringPerformanceAdapter(private val context: Context, private val spAdapter: TypeSpinnerAdapter): RecyclerView.Adapter<MonitoringPerformanceAdapter.RecyclerViewHolder>() {

    private val typeList = ArrayList<ChartType>()
    inner class RecyclerViewHolder(private val binding: ItemListPerformanceBinding): RecyclerView.ViewHolder(binding.root), OnChartValueSelectedListener {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(item: ChartType) {
            binding.apply {
                tvTypeChartPerformance.text = item.type
                spTypeChartPerformance.adapter = spAdapter
                spAdapter.setData(typeList[bindingAdapterPosition].lastRetrieve)
                spTypeChartPerformance.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                        Toast.makeText(context, "Displaying data ${typeList[position].lastRetrieve[position].label}", Toast.LENGTH_LONG).show()
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }

                }
                lcPerformLatencyChart.apply {
                    setBackgroundColor(resources.getColor(R.color.white))
                    description.isEnabled = false
                    setTouchEnabled(true)
                    setOnChartValueSelectedListener(this@RecyclerViewHolder)
                    isDragEnabled = true
                    setScaleEnabled(true)
                    setPinchZoom(true)
                    setDrawGridBackground(false)
                    val xAxis = lcPerformLatencyChart.xAxis
                    xAxis.apply {
                        enableGridDashedLine(10f, 10f, 0f)
                        position = XAxis.XAxisPosition.BOTTOM
                    }

                    val yAxis = lcPerformLatencyChart.axisLeft
                    yAxis.apply {
                        axisRight.isEnabled = false
                        setDrawLabels(false)
                        setDrawAxisLine(true)
                        enableGridDashedLine(10f, 10f, 0f)
                        typeface = resources.getFont(R.font.os_medium)
                        setLabelCount(6, false)
                        textColor = resources.getColor(R.color.red)
                        textSize = 10f
                        axisMinimum = -50f
                        axisMaximum = 200f
                        setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
                    }

                    val limitLineAxis = LimitLine(9f, "Index 10")
                    limitLineAxis.apply {
                        lineColor = resources.getColor(R.color.grey)
                        lineWidth = 4f
                        typeface = resources.getFont(R.font.os_medium)
                        enableDashedLine(10f, 10f, 0f)
                        labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
                        textSize = 10f
                    }

                    val limitLine1 = LimitLine(150f, "Upper Limit")
                    limitLine1.apply {
                        lineColor = resources.getColor(R.color.grey)
                        lineWidth = 4f
                        typeface = resources.getFont(R.font.os_medium)
//                    enableDashedLine(10f, 10f, 0f)
                        labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
                        textSize = 10f
                    }

                    val limitLine2 = LimitLine(-30f,"Lower Limit")
                    limitLine2.apply {
                        lineColor = resources.getColor(R.color.yellow)
                        lineWidth = 4f
                        typeface = resources.getFont(R.font.os_medium)
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

                val tesDataList = ArrayList<Entry>()
                tesDataList.apply {
                    add(Entry(1f, 33f))
                    add(Entry(2f, 15f))
                    add(Entry(3f, 14f))
                    add(Entry(4f, 56f))
                    add(Entry(5f, 38f))
                    add(Entry(6f, 21f))
                    add(Entry(7f, 10f))
                    add(Entry(8f, 100f))
                    add(Entry(9f, 0f))
                }
                val tesDataLabels = ArrayList<String>()
                tesDataLabels.add("19:00")
                tesDataLabels.add("19:10")
                tesDataLabels.add("19:20")
                tesDataLabels.add("19:30")
                tesDataLabels.add("19:40")
                tesDataLabels.add("19:50")
                tesDataLabels.add("20:00")
                lcPerformLatencyChart.xAxis.valueFormatter = IndexAxisValueFormatter(tesDataLabels)
                val tesData = LineDataSet(tesDataList, "data 1")
                tesData.apply {
                    setDrawIcons(false)
                    enableDashedLine(10f,5f, 0f)
                    color = context.resources.getColor(R.color.red)
                    setCircleColor(context.resources.getColor(R.color.grey))
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
                    highLightColor = context.resources.getColor(R.color.yellow)
                    color = context.resources.getColor(R.color.red)
                    fillColor = context.resources.getColor(R.color.grey)
                    fillFormatter = IFillFormatter { _, _ ->
                        lcPerformLatencyChart.axisLeft.axisMinimum
                    }
                }
                val dataSets = ArrayList<ILineDataSet>()
                dataSets.add(tesData)
                val tesDataSet = LineData(dataSets)
                lcPerformLatencyChart.data = tesDataSet
                lcPerformLatencyChart.invalidate()
                val l = lcPerformLatencyChart.legend
                l.isEnabled = false
                l.form = Legend.LegendForm.LINE
            }
        }

        override fun onValueSelected(e: Entry?, h: Highlight?) {
        }

        override fun onNothingSelected() {
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val itemBinding = ItemListPerformanceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecyclerViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return this.typeList.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val data = this.typeList[position]
        holder.bind(data)
    }

    fun setData(dataList: List<ChartType>) {
        this.typeList.apply {
            clear()
            addAll(dataList)
        }
        notifyDataSetChanged()
    }
}