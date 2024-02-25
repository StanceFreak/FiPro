package com.stancefreak.monkob.views.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.stancefreak.monkob.databinding.FragmentHomeBinding
import com.stancefreak.monkob.views.adapter.HomePagerAdapter

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
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
    }

    private fun initViews() {
        val pagerAdapter = HomePagerAdapter((activity as AppCompatActivity).supportFragmentManager, 2, lifecycle)
        binding.apply {
            vpHomeContainer.apply {
                adapter = pagerAdapter
                isUserInputEnabled = false
            }
            val listTab = arrayOf("Server Condition", "Server Performance")
            Log.d("tes size", listTab.size.toString())
            TabLayoutMediator(tlHomeContainer, vpHomeContainer) { tab, pos ->
                tab.text = listTab[pos]
            }.attach()
        }
    }


}