package com.stancefreak.monkob.views.monitoring.physical

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.stancefreak.monkob.databinding.FragmentMonitoringPhysicalBinding
import com.stancefreak.monkob.views.adapter.NetworkInterfacesAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MonitoringPhysicalFragment : Fragment() {

    private lateinit var interfacesAdapter: NetworkInterfacesAdapter
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
        interfacesAdapter = NetworkInterfacesAdapter()
        binding.rvPhysNetInterfaces.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = interfacesAdapter
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
                        tvPhysMemUtil.text = "Current memory usage: ${response.memoryAvailable}GiB of ${response.memoryTotal}GiB (${response.memoryUsage}%)"
                    }
                }
                observeServerUtil().observe(viewLifecycleOwner) {
                    it.getContentIfNotHandled()?.let { response ->
                        tvPhysNetUtil.text = "Network: ${response[1].utilTotalData?.get(0)?.value}MiB/s rx, ${response[1].utilTotalData?.get(1)?.value}MiB/s tx"
                        tvPhysDriveUtil.text = "Disk: ${response[3].utilTotalData?.get(0)?.value}MiB/s read, ${response[3].utilTotalData?.get(1)?.value}MiB/s write"
                        response[0].netUtilData?.let { it1 -> interfacesAdapter.setData(it1) }
                        Log.d("tes interface", response[0].netUtilData.toString())
                    }
                }
            }
        }
    }

}