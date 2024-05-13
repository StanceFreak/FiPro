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
import com.github.mikephil.charting.data.Entry
import com.stancefreak.monkob.R
import com.stancefreak.monkob.databinding.FragmentHistoryBinding
import com.stancefreak.monkob.remote.model.response.ChartType
import com.stancefreak.monkob.remote.model.response.LastRetrieve
import com.stancefreak.monkob.views.adapter.HistoryAdapter
import com.stancefreak.monkob.views.adapter.TypeSpinnerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment(){

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var spAdapter: TypeSpinnerAdapter
    private lateinit var historyAdapter: HistoryAdapter
    private val historyViewModel: HistoryViewModel by viewModels()

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
        historyAdapter = HistoryAdapter(requireContext(), historyViewModel, viewLifecycleOwner)
        spAdapter = TypeSpinnerAdapter(requireContext())
        binding.apply {
            rvHistoryChart.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = historyAdapter
                setHasFixedSize(true)
            }
            spHistoryType.adapter = spAdapter
            spAdapter.setData(lrList)
            spHistoryType.apply {
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                        Log.d("tes select", isSelected.toString())
                        setSelection(lrList[position].id)
                        historyAdapter.setParentData(typeList, lrList[position].query)
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        setSelection(0)
                        historyAdapter.setParentData(typeList, "today")
                    }
                }
            }
        }
    }

}