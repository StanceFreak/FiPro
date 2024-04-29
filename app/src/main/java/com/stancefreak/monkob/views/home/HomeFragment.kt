package com.stancefreak.monkob.views.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.stancefreak.monkob.databinding.FragmentHomeBinding
import com.stancefreak.monkob.views.adapter.HomePagerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()

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
            viewModel.getServerUptime()
            vpHomeContainer.apply {
                adapter = pagerAdapter
                isUserInputEnabled = false
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
                    it.getContentIfNotHandled()?.let {response ->
                        tvHomeHeaderStatus.text = "Online for ${response.serverUptime}"
                    }
                }
            }
        }
    }

}