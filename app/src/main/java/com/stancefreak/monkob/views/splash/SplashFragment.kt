package com.stancefreak.monkob.views.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.stancefreak.monkob.R
import com.stancefreak.monkob.databinding.FragmentSplashBinding
import com.stancefreak.monkob.databinding.ItemDialogErrorBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private lateinit var binding: FragmentSplashBinding
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSplash()
        observeData()
    }

    private fun setupSplash() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val token = task.result
            viewModel.registerDevice(token)
        })
    }

    private fun observeData() {
        viewModel.apply {
            observeDeviceRegister().observe(viewLifecycleOwner) {
                it.getContentIfNotHandled()?.let { response ->
                    if (response.status == 200) {
                        viewLifecycleOwner.lifecycleScope.launch {
                            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                                Handler(Looper.myLooper()!!).postDelayed({
                                    findNavController().navigate(R.id.splash_to_home)
                                }, 3000)
                            }
                        }
                    }
                }
            }
            observeDeviceRegisterError().observe(viewLifecycleOwner) {
                it.getContentIfNotHandled()?.let { error ->
                    if (error.first) {
                        if (error.second == "timeout") {
                            val networkErrorDialog =
                                ItemDialogErrorBinding.inflate(LayoutInflater.from(context))
                            val networkErrorDialogBuilder = android.app.AlertDialog.Builder(context)
                                .setView(networkErrorDialog.root)
                            networkErrorDialogBuilder.setCancelable(false)
                            val showDialog = networkErrorDialogBuilder.show()
                            networkErrorDialog.btnDialogError.setOnClickListener {
                                FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                                    if (!task.isSuccessful) {
                                        return@OnCompleteListener
                                    }
                                    val token = task.result
                                    viewModel.registerDevice(token)
                                })
                                showDialog.dismiss()
                            }
                        }
                    }
                }
            }
        }
    }

}