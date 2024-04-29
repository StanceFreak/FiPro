package com.stancefreak.monkob.views.monitoring

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.stancefreak.monkob.databinding.FragmentMonitoringPhysicalBinding
import com.stancefreak.monkob.views.adapter.MonitoringPerformanceParentAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.round

@AndroidEntryPoint
class MonitoringPhysicalFragment : Fragment() {

    private lateinit var monitoringPerformanceParentAdapter: MonitoringPerformanceParentAdapter
    private lateinit var binding: FragmentMonitoringPhysicalBinding
    private val viewModel: MonitoringPhysicalViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMonitoringPhysicalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeData()
    }

    private fun initViews() {
        viewModel.getPhysicalStats()
        monitoringPerformanceParentAdapter = MonitoringPerformanceParentAdapter()
        binding.rvPerformance.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = monitoringPerformanceParentAdapter
        }
    }

    private fun observeData() {
        binding.apply {
            viewModel.apply {
                observeServerCpuUsage().observe(viewLifecycleOwner) {
                    it.getContentIfNotHandled()?.let { response ->
                        tvPhysProcUtil.text = "Current CPU usage: ${response.value}%"
                    }
                }
                observeServerAvgMemory().observe(viewLifecycleOwner) {
                    it.getContentIfNotHandled()?.let { response ->
                        val avgMemory = response.memoryTotal - response.memoryAvailable
                        val finalAvgMemory = when (avgMemory.toFloat()) {
                            in 0.0 .. 1000.0 -> {
                                "${round(avgMemory.toFloat())}B"
                            }
                            in 1000.1 .. 1000000.0 -> {
                                "${round(avgMemory.toFloat() / 1000)}KB"
                            }
                            in 1000000.1 .. 1000000000.0 -> {
                                "${round(avgMemory.toFloat() / 1000000)}MB"
                            }
                            else -> {
                                "${round(avgMemory.toFloat() / 1000000000)}GB"
                            }
                        }
                        tvPhysMemUtil.text = "Current memory usage: $finalAvgMemory of ${round(response.memoryTotal / 1000000000)}GiB (${response.memoryUsage}%)"
                    }
                }
                observePhysicalServerUtil().observe(viewLifecycleOwner) {
                    it.getContentIfNotHandled()?.let { response ->
                        for (item in response) {
                            val receiveValue = item.utilTotalData?.get(0)?.value?.toFloat()
                            val transmitValue = item.utilTotalData?.get(1)?.value?.toFloat()
                            if (receiveValue != null && transmitValue != null) {
                                val finalReceiveValue = when (receiveValue) {
                                    in 0.0 .. 1000.0 -> {
                                        "${round(receiveValue)}B/s"
                                    }
                                    in 1000.1 .. 1000000.0 -> {
                                        "${round(receiveValue / 1000)}KB/s"
                                    }
                                    in 1000000.1 .. 1000000000.0 -> {
                                        "${round(receiveValue / 1000000)}MB/s"
                                    }
                                    else -> {
                                        "${round(receiveValue / 1000000000)}GB/s"
                                    }
                                }
                                val finalTransmitValue = when (transmitValue) {
                                    in 0.0 .. 1000.0 -> {
                                        "${round(transmitValue)}B/s"
                                    }
                                    in 1000.1 .. 1000000.0 -> {
                                        "${round(transmitValue / 1000)}KB/s"
                                    }
                                    in 1000000.1 .. 1000000000.0 -> {
                                        "${round(transmitValue / 1000000)}MB/s"
                                    }
                                    else -> {
                                        "${round(transmitValue / 1000000000)}GB/s"
                                    }
                                }
                                if (item.id == 1) {
                                    tvPhysNetUtil.text = "Network: ${finalReceiveValue} rx, ${finalTransmitValue} tx"
                                }
                                else {
                                    tvPhysDriveUtil.text = "Disk: ${finalReceiveValue} read, ${finalTransmitValue} write"
                                }
                            }
                        }
                    }
                }
                observePerformanceServerUtil().observe(viewLifecycleOwner) {
                    it.getContentIfNotHandled()?.let { response ->
                        monitoringPerformanceParentAdapter.setData(response)
                    }
                }
            }
        }
    }

}