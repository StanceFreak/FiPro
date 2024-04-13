package com.stancefreak.monkob.views.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.stancefreak.monkob.views.monitoring.performance.MonitoringPerformanceFragment
import com.stancefreak.monkob.views.monitoring.physical.MonitoringPhysicalFragment

class HomePagerAdapter(
    fm: FragmentManager,
    private var totalTab: Int,
    lifecycle: Lifecycle
): FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int {
        return totalTab
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MonitoringPhysicalFragment()
            1 -> MonitoringPerformanceFragment()
            else -> MonitoringPhysicalFragment()
        }
    }

}