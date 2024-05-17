package com.stancefreak.monkob.views.history

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.stancefreak.monkob.databinding.FragmentHistoryBinding
import com.stancefreak.monkob.remote.model.response.ChartType
import com.stancefreak.monkob.remote.model.response.LastRetrieve
import com.stancefreak.monkob.views.adapter.HistoryAdapter
import com.stancefreak.monkob.views.adapter.TypeSpinnerAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.ceil

@AndroidEntryPoint
class HistoryFragment : Fragment(), HistoryAdapter.OnRetrieveData{

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var spAdapter: TypeSpinnerAdapter
    private lateinit var historyAdapter: HistoryAdapter
    private val historyViewModel: HistoryViewModel by viewModels()
    private var queryInterval = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        val lrList = arrayListOf(
            LastRetrieve(0, "Today", "today"),
            LastRetrieve(1, "Yesterday", "1 day"),
            LastRetrieve(2, "Two days ago", "2 day"),
            LastRetrieve(3, "Three days ago", "3 day"),
        )
        val typeList = arrayListOf(
            ChartType(0, "CPU usage (%)"),
            ChartType(1, "Memory usage (%)"),
            ChartType(2, "Network latency (ms)")
        )
        historyAdapter = HistoryAdapter(historyViewModel, viewLifecycleOwner, this)
        spAdapter = TypeSpinnerAdapter(requireContext())
        binding.apply {
            rvHistoryChart.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = historyAdapter
            }
            spHistoryType.adapter = spAdapter
            spAdapter.setData(lrList)
            spHistoryType.apply {
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                        Log.d("tes select", isSelected.toString())
                        historyAdapter.setParentData(typeList, lrList[position].query)
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }
                }
            }
            CoroutineScope(Dispatchers.IO).launch {
                val firstCallTime = ceil(System.currentTimeMillis() / 60_000.0).toLong() * 60_000
                delay(firstCallTime - System.currentTimeMillis())
                while (isActive) {
                    launch {
                        historyViewModel.apply {
                            fetchCpuRecords(queryInterval)
                            fetchMemRecords(queryInterval)
                            fetchLatencyRecords(queryInterval)
                        }
                    }
                    delay(60_000)
                }
            }
        }
    }

    override fun getQuery(query: String) {
        queryInterval = query
    }

}