package com.stancefreak.monkob.views.navigation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.stancefreak.monkob.R
import com.stancefreak.monkob.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NavigationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNav()
    }

    private fun setupNav() {
        val navHost = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navHost.navController
    }


}