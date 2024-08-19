package com.stancefreak.monkob.views.adapter

import android.Manifest
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.graphics.DashPathEffect
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isGone
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
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
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.stancefreak.monkob.BuildConfig
import com.stancefreak.monkob.R
import com.stancefreak.monkob.databinding.ItemListHistoryBinding
import com.stancefreak.monkob.remote.model.response.ChartType
import com.stancefreak.monkob.remote.model.response.ServerRecord
import com.stancefreak.monkob.remote.model.response.ServerRecordsDownload
import com.stancefreak.monkob.utils.SingleLiveEvent
import com.stancefreak.monkob.views.history.HistoryViewModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat

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
    inner class RecyclerViewHolder(
        private val binding: ItemListHistoryBinding
    ): RecyclerView.ViewHolder(binding.root), OnChartValueSelectedListener {
        fun bind(item: ChartType) {
            binding.apply {
                tvHistoryTypeChart.text = item.type
                onRetrieveData.getQuery(queryInterval)
                when (item.id) {
                    0 -> {
                        viewModel.apply {
                            fetchCpuRecords(queryInterval)
                            ivHistoryDownload.setOnClickListener {
                                downloadCpuRecords(queryInterval)
                            }
                            observeFetchRecords(
                                observeServerCpuUtilsRecord(),
                                observeCpuError(),
                                observeCpuLoading(),
                                0
                            )
                            observeDownloadRecords(
                                observeServerDownloadCpuRecords(),
                                observeDownloadError()
                            )
                        }
                    }
                    1 -> {
                        viewModel.apply {
                            fetchMemRecords(queryInterval)
                            ivHistoryDownload.setOnClickListener {
                                downloadMemRecords(queryInterval)
                            }
                            observeFetchRecords(
                                observeServerMemUtilsRecord(),
                                observeMemError(),
                                observeMemLoading(),
                                item.id
                            )
                            observeDownloadRecords(
                                observeServerDownloadMemRecords(),
                                observeDownloadError()
                            )
                        }

                    }
                    2 -> {
                        viewModel.apply {
                            fetchLatencyRecords(queryInterval)
                            ivHistoryDownload.setOnClickListener {
                                downloadLatRecords(queryInterval)
                            }
                            observeFetchRecords(
                                observeServerNetLatencyRecord(),
                                observeLatError(),
                                observeLatencyLoading(),
                                item.id
                            )
                            observeDownloadRecords(
                                observeServerDownloadLatRecords(),
                                observeDownloadError()
                            )
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

        private fun observeFetchRecords(
            success: LiveData<SingleLiveEvent<ArrayList<ServerRecord>?>>,
            error:LiveData<SingleLiveEvent<Pair<Boolean, String?>>>,
            loading: LiveData<SingleLiveEvent<Boolean>>,
            id: Int
        ) {
            val tesDataList = ArrayList<Entry>()
            val tesDataLabels = ArrayList<String>()
            binding.apply {
                success.observe(lifecycleOwner) {
                    it.getContentIfNotHandled()?.let { response ->
                        if (response.isNotEmpty()) {
                            sfHistoryChartLoading.stopShimmer()
                            sfHistoryChartLoading.isGone = true
                            rlHistoryHeader.isGone = false
                            clHistoryChartLoading.isGone = true
                            clHistoryChartError.isGone = true
                            lcHistoryChart.isGone = false
                            tesDataLabels.clear()
                            tesDataList.clear()
                            for ((itemPos, i) in response.withIndex()) {
                                tesDataList.add(Entry(itemPos.toFloat(), DecimalFormat("0.#").format(i.value).toFloat()))
                                tesDataLabels.add(i.time)
                            }
                            val sdfParse = SimpleDateFormat("dd/MM/yyyy'T'HH:mm:ss").parse("${response[0].date}T${response[0].time}")?.time
                            val sdfFormatted = SimpleDateFormat("EEEE, dd MMM yyyy").format(sdfParse)
                            chartData(tesDataList, tesDataLabels, sdfFormatted)
                        }
                        else {
                            sfHistoryChartLoading.stopShimmer()
                            sfHistoryChartLoading.isGone = true
                            clHistoryChartLoading.isGone = true
                            rlHistoryHeader.isGone = false
                            clHistoryChartError.isGone = false
                            ivHistoryDownload.isGone = true
                            ivHistoryChartError.setImageResource(R.drawable.ic_empty)
                            tvHistoryChartError.text = "There is no data, try asking the administator!"
                            lcHistoryChart.isGone = true
                            btnHistoryChartRetry.isGone = true
                        }
                    }
                }
                error.observe(lifecycleOwner) {
                    it.getContentIfNotHandled()?.let { exception ->
                        if (exception.first) {
                            sfHistoryChartLoading.stopShimmer()
                            sfHistoryChartLoading.isGone = true
                            clHistoryChartLoading.isGone = true
                            rlHistoryHeader.isGone = false
                            ivHistoryDownload.isGone = true
                            ivHistoryChartError.setImageResource(R.drawable.ic_conn_refused)
                            tvHistoryChartError.text = "Error occured while getting data from the server!"
                            clHistoryChartError.isGone = false
                            lcHistoryChart.isGone = true
                            btnHistoryChartRetry.isGone = false
                            btnHistoryChartRetry.setOnClickListener {
                                when(id) {
                                    0 -> viewModel.fetchCpuRecords(queryInterval)
                                    1 -> viewModel.fetchMemRecords(queryInterval)
                                    2 -> viewModel.fetchLatencyRecords(queryInterval)
                                    else -> Toast.makeText(itemView.context, "Chart not found!", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                }
                loading.observe(lifecycleOwner) {
                    it.getContentIfNotHandled()?.let { loading ->
                        if (loading) {
                            sfHistoryChartLoading.isGone = false
                            rlHistoryHeader.isGone = true
                            clHistoryChartLoading.isGone = false
                            clHistoryChartError.isGone = true
                            lcHistoryChart.isGone = true
                        }
                    }
                }
            }
        }

        private fun observeDownloadRecords(
            success: LiveData<SingleLiveEvent<ServerRecordsDownload?>>,
            error:LiveData<SingleLiveEvent<Pair<Boolean, String?>>>
        ) {
            success.observe(lifecycleOwner) {
                it.getContentIfNotHandled()?.let { response ->
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                        Dexter.withContext(itemView.context)
                            .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .withListener(object : PermissionListener {
                                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                                    downloadData(response.data)
                                }

                                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                                    val alertBuilder = AlertDialog.Builder(itemView.context)
                                    alertBuilder.apply {
                                        setTitle("Need Permission!")
                                        setMessage("To use this feature, you need to allow the required permission in App settings!")
                                        setPositiveButton("Settings") { dialog, _ ->
                                            dialog.cancel()
                                            val goSetting =
                                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                            val uri =
                                                Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                                            goSetting.data = uri
                                            itemView.context.startActivity(goSetting)
                                        }
                                        setNegativeButton("Cancel") { dialog, _ ->
                                            dialog.cancel()
                                        }
                                        show()
                                    }
                                }

                                override fun onPermissionRationaleShouldBeShown(
                                    permission: PermissionRequest?,
                                    token: PermissionToken?
                                ) {
                                    token?.continuePermissionRequest()
                                }

                            }).withErrorListener { error ->
                                Log.e("permissionError", error.name)
                            }.check()
                    }
                    else {
                        downloadData(response.data)
                    }
                }
            }
            error.observe(lifecycleOwner) {
                it.getContentIfNotHandled()?.let { exception ->
                    if (exception.first) {
                        Toast.makeText(itemView.context, exception.second, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        private fun downloadData(url: String) {
            val downloadManager = itemView.context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val uri = Uri.parse(url)
            val request = DownloadManager.Request(uri)
            request.apply {
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    uri.lastPathSegment
                )
            }
            downloadManager.enqueue(request)
            Toast.makeText(
                itemView.context,
                "File downloaded at ${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}",
                Toast.LENGTH_LONG
            ).show()
        }

        private fun chartData(dataList: ArrayList<Entry>, labelList: ArrayList<String>, date: String) {
            binding.apply {
                if (dataList.isEmpty() || labelList.isEmpty()) {
//                    sfHistoryChartLoading.stopShimmer()
//                    sfHistoryChartLoading.isGone = true
                    clHistoryChartLoading.isGone = true
                    ivHistoryDownload.isGone = true
                    ivHistoryChartError.isGone = false
                    ivHistoryChartError.setImageResource(R.drawable.ic_empty)
                    clHistoryChartError.isGone = false
                    btnHistoryChartRetry.isGone = false
                    lcHistoryChart.isGone = true
                }
                else {
                    val l = lcHistoryChart.legend
                    l.form = Legend.LegendForm.LINE
                    clHistoryChartLoading.isGone = true
                    clHistoryChartError.isGone = true
                    ivHistoryDownload.isGone = false
                    lcHistoryChart.isGone = false
                    lcHistoryChart.apply {
                        isGone = false
                        description.isEnabled = false
                        setExtraOffsets(0F,0F,0F,10F)
                        setTouchEnabled(true)
                        setOnChartValueSelectedListener(this@RecyclerViewHolder)
                        isDragEnabled = true
                        setScaleEnabled(true)
                        setPinchZoom(true)
                        setDrawGridBackground(false)
                        val xAxis = lcHistoryChart.xAxis
                        xAxis.apply {
                            disableGridDashedLine()
//                            enableGridDashedLine(10f, 10f, 0f)
                            position = XAxis.XAxisPosition.BOTTOM
                        }

                        val yAxis = lcHistoryChart.axisLeft
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
                        val tesData = LineDataSet(dataList, date)
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
                                lcHistoryChart.axisLeft.axisMinimum
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