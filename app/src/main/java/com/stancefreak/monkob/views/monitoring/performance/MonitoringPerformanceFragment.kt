package com.stancefreak.monkob.views.monitoring.performance

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.stancefreak.monkob.databinding.FragmentMonitoringPerformanceBinding
import com.stancefreak.monkob.remote.model.response.ChartType
import com.stancefreak.monkob.remote.model.response.LastRetrieve
import com.stancefreak.monkob.views.adapter.MonitoringPerformanceAdapter
import com.stancefreak.monkob.views.adapter.TypeSpinnerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MonitoringPerformanceFragment : Fragment() {

    private lateinit var binding: FragmentMonitoringPerformanceBinding
    private lateinit var spAdapter: TypeSpinnerAdapter
    private lateinit var monitoringPerformanceAdapter: MonitoringPerformanceAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMonitoringPerformanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initViews() {
        val lrList = arrayListOf(
            LastRetrieve(0, "Last 10 Minutes", "10m"),
            LastRetrieve(1, "Last 30 Minutes", "30m"),
            LastRetrieve(2, "Last 1 Hour", "1h"),
            LastRetrieve(3, "Last 12 Hour", "12h"),
            LastRetrieve(4, "Last 24 Hour", "24h"),
        )
        val typeList = arrayListOf(
            ChartType(0, "Cpu usage (%)", lrList),
            ChartType(1, "Memory usage (%)", lrList),
            ChartType(2, "Network latency (ms)", lrList)
        )
        spAdapter = TypeSpinnerAdapter(requireContext())
        monitoringPerformanceAdapter = MonitoringPerformanceAdapter(requireContext(), spAdapter)
        binding.rvPerformChart.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = monitoringPerformanceAdapter
        }
        monitoringPerformanceAdapter.setData(typeList)
    }

}