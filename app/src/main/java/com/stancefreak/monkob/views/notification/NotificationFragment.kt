package com.stancefreak.monkob.views.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.stancefreak.monkob.R
import com.stancefreak.monkob.databinding.FragmentNotificationBinding
import com.stancefreak.monkob.views.adapter.NotificationParentAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationFragment : Fragment() {
    private lateinit var binding: FragmentNotificationBinding
    private lateinit var notifAdapter: NotificationParentAdapter
    private val viewModel: NotificationViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initViews()
        observeData()
    }

    private fun initToolbar() {
        binding.apply {
            toolbarNotifHeader.setNavigationIcon(R.drawable.ic_arrow_back)
            (activity as AppCompatActivity).apply {
                setSupportActionBar(toolbarNotifHeader)
                title = ""
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                toolbarNotifHeader.setNavigationOnClickListener {
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }
    }

    private fun initViews() {
        notifAdapter = NotificationParentAdapter()
        viewModel.fetchNotifRecords()
        binding.apply {
            srlNotifRefresh.apply {
                setOnRefreshListener {
                    viewModel.fetchNotifRecords()
                    isRefreshing = false
                }
            }
            rvNotifList.addOnScrollListener(object : OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val topItemPosition =
                        if (recyclerView.childCount == 0) 0 else recyclerView.getChildAt(0).top
                    srlNotifRefresh.isEnabled = topItemPosition >= 0
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                }
            })
        }
    }

    private fun observeData() {
        viewModel.apply {
            binding.apply {
                observeServerNotif().observe(viewLifecycleOwner) {
                    it.getContentIfNotHandled()?.let { response ->
                        clNotifLoading.isGone = true
                        if (response.isEmpty()) {
                            clNotifEmpty.isGone = false
                            rvNotifList.isGone = true
                        }
                        else {
                            clNotifEmpty.isGone = true
                            rvNotifList.isGone = false
                            rvNotifList.apply {
                                layoutManager = LinearLayoutManager(requireContext())
                                adapter = notifAdapter
                            }
                            notifAdapter.setData(response)
                        }
                    }
                }
                observeApiLoading().observe(viewLifecycleOwner) {
                    it.getContentIfNotHandled()?.let { loading ->
                        if (loading) {
                            clNotifLoading.isGone = false
                            clNotifEmpty.isGone = true
                            rvNotifList.isGone = true
                        }
                    }
                }
            }
        }
    }

}