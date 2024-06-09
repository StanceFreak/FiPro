package com.stancefreak.monkob.views.home

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.stancefreak.monkob.R
import com.stancefreak.monkob.databinding.FragmentHomeBinding
import com.stancefreak.monkob.databinding.ItemDialogServiceManagementBinding
import com.stancefreak.monkob.views.adapter.HomePagerAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.ceil

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private var fetchCount = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeData()
    }

    private fun initViews() {
        val pagerAdapter = HomePagerAdapter((activity as AppCompatActivity).supportFragmentManager, 2, lifecycle)
        binding.apply {
            vpHomeContainer.apply {
                adapter = pagerAdapter
                isUserInputEnabled = false
            }
            viewModel.apply {
                fetchServerStatus(true)
                fetchServerUptime(true)
            }
            fetchCount++
            CoroutineScope(Dispatchers.IO).launch {
                val firstCallTime = ceil(System.currentTimeMillis() / 10_000.0).toLong() * 10_000
                delay(firstCallTime - System.currentTimeMillis())
                while (isActive) {
                    launch {
                        if (fetchCount > 0) {
                            viewModel.fetchServerStatus(false)
                        }
                    }
                    delay(10_000)
                }
            }
            CoroutineScope(Dispatchers.IO).launch {
                val firstCallTime = ceil(System.currentTimeMillis() / 60_000.0).toLong() * 60_000
                delay(firstCallTime - System.currentTimeMillis())
                while (isActive) {
                    launch {
                        if (fetchCount > 0) {
                            viewModel.fetchServerUptime(false)
                        }
                    }
                    delay(60_000)
                }
            }
            ivHomeHeaderNotif.setOnClickListener {
                findNavController().navigate(R.id.home_to_notif)
            }
            btnHomeHeaderReboot.setOnClickListener {
                val statManagementDialog =
                    ItemDialogServiceManagementBinding.inflate(LayoutInflater.from(context))
                val deleteDialogBuilder = android.app.AlertDialog.Builder(context)
                    .setView(statManagementDialog.root)
                deleteDialogBuilder.setCancelable(false)
                val showDialog = deleteDialogBuilder.show()

                statManagementDialog.apply {
                    tvDialogStatManagementTitle.text = "Restart Service"
                    tvDialogStatManagementSubTitle.text = "Restart the Prometheus service?"
                    btnDialogStatManagementYes.setOnClickListener {
                        viewModel.postServerChangeStatus("restart")
                        showDialog.dismiss()
                    }

                    btnDialogStatManagementNo.setOnClickListener {
                        showDialog.dismiss()
                    }
                }
            }
//            srHomeRefresh.setOnRefreshListener {
//                onRetrieveData.getRefreshState(srHomeRefresh.isRefreshing)
//                viewModel.getServerUptime()
//                srHomeRefresh.isRefreshing = false
//            }

            val listTab = arrayOf("Server Condition", "Server Activity")
            Log.d("tes size", listTab.size.toString())
            TabLayoutMediator(tlHomeContainer, vpHomeContainer) { tab, pos ->
                tab.text = listTab[pos]
            }.attach()
        }
    }

    private fun observeData() {
        binding.apply {
            viewModel.apply {
                observeServerUptime().observe(viewLifecycleOwner) {
                    it.getContentIfNotHandled()?.let { response ->
                        if (!response.equals(null)) {
                            tvHomeHeaderStatus.text = "Online for : ${response.uptime}"
                        }
                        else {
                            tvHomeHeaderStatus.text = "Online for : -"
                        }
                    }
                }
                observeServerStatus().observe(viewLifecycleOwner) {
                    it.getContentIfNotHandled()?.let {response ->
                        if (!response.equals(null)) {
                            tlHomeContainer.isGone = false
                            vpHomeContainer.isGone = false
                            clHomeEmptyContainer.isGone = true
                            tvHomeHeaderPrometheusLabel.text = "Prometheus service is UP"
                            btnHomeHeaderStop.text = "Stop"
                            btnHomeHeaderStop.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red))
                            btnHomeHeaderStop.setOnClickListener {
                                val statManagementDialog =
                                    ItemDialogServiceManagementBinding.inflate(LayoutInflater.from(context))
                                val deleteDialogBuilder = android.app.AlertDialog.Builder(context)
                                    .setView(statManagementDialog.root)
                                deleteDialogBuilder.setCancelable(false)
                                val showDialog = deleteDialogBuilder.show()

                                statManagementDialog.apply {
                                    tvDialogStatManagementTitle.text = "Stop Service"
                                    tvDialogStatManagementSubTitle.text = "Do you really want to stop the Prometheus service?"
                                    btnDialogStatManagementYes.setOnClickListener {
                                        viewModel.postServerChangeStatus("stop")
                                        showDialog.dismiss()
                                    }

                                    btnDialogStatManagementNo.setOnClickListener {
                                        showDialog.dismiss()
                                    }
                                }
                            }
                        }
                        else {
                            tlHomeContainer.isGone = true
                            vpHomeContainer.isGone = true
                            clHomeEmptyContainer.isGone = false
                            tvHomeHeaderPrometheusLabel.text = "Prometheus service is DOWN"
                            btnHomeHeaderStop.text = "Start"
                            btnHomeHeaderStop.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.green))
                            btnHomeHeaderStop.setOnClickListener {
                                val statManagementDialog =
                                    ItemDialogServiceManagementBinding.inflate(LayoutInflater.from(context))
                                val deleteDialogBuilder = android.app.AlertDialog.Builder(context)
                                    .setView(statManagementDialog.root)
                                deleteDialogBuilder.setCancelable(false)
                                val showDialog = deleteDialogBuilder.show()

                                statManagementDialog.apply {
                                    tvDialogStatManagementTitle.text = "Start Service"
                                    tvDialogStatManagementSubTitle.text = "Start the Prometheus service?"
                                    btnDialogStatManagementYes.setOnClickListener {
                                        viewModel.postServerChangeStatus("start")
                                        showDialog.dismiss()
                                    }

                                    btnDialogStatManagementNo.setOnClickListener {
                                        showDialog.dismiss()
                                    }
                                }
                            }
                        }
                    }
                }
                observeServerChangeStatus().observe(viewLifecycleOwner) {
                    it.getContentIfNotHandled()?.let {response ->
                        if (response.result == "Server is running" || response.result == "Server restarted") {
                            tlHomeContainer.isGone = false
                            vpHomeContainer.isGone = false
                            clHomeEmptyContainer.isGone = true
                            tvHomeHeaderPrometheusLabel.text = "Prometheus service is UP"
                            btnHomeHeaderStop.text = "Stop"
                            btnHomeHeaderStop.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red))
                        }
                        else {
                            tlHomeContainer.isGone = true
                            vpHomeContainer.isGone = true
                            clHomeEmptyContainer.isGone = false
                            tvHomeHeaderPrometheusLabel.text = "Prometheus service is DOWN"
                            btnHomeHeaderStop.text = "Start"
                            btnHomeHeaderStop.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.green))
                        }
                        viewModel.fetchServerStatus(true)
                        viewModel.fetchServerUptime(true)
                    }

                }
                observeServerUptimeLoading().observe(viewLifecycleOwner) {
                    it.getContentIfNotHandled()?.let {loading ->
                        if (loading) {
                            sfHomeHeaderUptimeLoading.visibility = View.VISIBLE
                            tvHomeHeaderStatus.visibility = View.INVISIBLE
                        }
                        else {
                            sfHomeHeaderUptimeLoading.stopShimmer()
                            sfHomeHeaderUptimeLoading.visibility = View.INVISIBLE
                            tvHomeHeaderStatus.visibility = View.VISIBLE
                        }
                    }
                }
                observeServerStatusLoading().observe(viewLifecycleOwner) {
                    it.getContentIfNotHandled()?.let {loading ->
                        if (loading) {
                            sfHomeHeaderLoading.visibility = View.VISIBLE
                            sfHomeBodyLoading.visibility = View.VISIBLE
                            rlHomeHeaderContainer.visibility = View.INVISIBLE
                        }
                        else {
                            sfHomeHeaderLoading.stopShimmer()
                            sfHomeBodyLoading.stopShimmer()
                            sfHomeHeaderLoading.visibility = View.INVISIBLE
                            sfHomeBodyLoading.visibility = View.INVISIBLE
                            rlHomeHeaderContainer.visibility = View.VISIBLE
                        }
                    }
                }
                observeServerChangeStatusLoading().observe(viewLifecycleOwner) {
                    it.getContentIfNotHandled()?.let {loading ->
                        if (loading) {
                            sfHomeHeaderLoading.visibility = View.VISIBLE
                            rlHomeHeaderContainer.visibility = View.INVISIBLE
                            sfHomeHeaderUptimeLoading.visibility = View.VISIBLE
                            tvHomeHeaderStatus.visibility = View.INVISIBLE
                        }
                        else {
                            sfHomeHeaderUptimeLoading.stopShimmer()
                            sfHomeHeaderLoading.stopShimmer()
                            sfHomeHeaderLoading.visibility = View.INVISIBLE
                            rlHomeHeaderContainer.visibility = View.VISIBLE
                            sfHomeHeaderUptimeLoading.visibility = View.INVISIBLE
                            tvHomeHeaderStatus.visibility = View.VISIBLE
                        }
                    }
                }
                observeServerStatusError().observe(viewLifecycleOwner) {
                    it.getContentIfNotHandled()?.let {error ->
                        if (error.first) {
                            if (error.second == "connect ECONNREFUSED 128.199.135.220:9090") {
                                tvHomeHeaderStatus.text = "Online for : -"
                                tlHomeContainer.isGone = true
                                vpHomeContainer.isGone = true
                                clHomeEmptyContainer.isGone = false
                                tvHomeHeaderPrometheusLabel.text = "Prometheus service is DOWN"
                                btnHomeHeaderStop.text = "Start"
                                btnHomeHeaderStop.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.green))
                                btnHomeHeaderStop.setOnClickListener {
                                    val statManagementDialog =
                                        ItemDialogServiceManagementBinding.inflate(LayoutInflater.from(context))
                                    val deleteDialogBuilder = android.app.AlertDialog.Builder(context)
                                        .setView(statManagementDialog.root)
                                    deleteDialogBuilder.setCancelable(false)
                                    val showDialog = deleteDialogBuilder.show()

                                    statManagementDialog.apply {
                                        tvDialogStatManagementTitle.text = "Start Service"
                                        tvDialogStatManagementSubTitle.text = "Start the Prometheus service?"
                                        btnDialogStatManagementYes.setOnClickListener {
                                            viewModel.postServerChangeStatus("start")
                                            viewModel.fetchServerStatus(true)
                                            showDialog.dismiss()
                                        }

                                        btnDialogStatManagementNo.setOnClickListener {
                                            showDialog.dismiss()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}