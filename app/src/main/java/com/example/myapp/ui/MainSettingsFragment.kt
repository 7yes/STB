package com.example.myapp.ui

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.myapp.databinding.FragmentMainSettingsBinding
import com.example.myapp.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainSettingsFragment : Fragment() {

    private lateinit var binding: FragmentMainSettingsBinding

    private val settingsViewModel by lazy {
        ViewModelProvider(requireActivity())[SettingsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainSettingsBinding.inflate(inflater, container, false)

        binding.btEnabled.isChecked = settingsViewModel.defaultBTState
        binding.wifiEnabled.isChecked  = settingsViewModel.defaultWifiState

        binding.swtDarkMode.isChecked = when(context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            else -> false
        }

        binding.swtDarkMode.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.enableDarkMode(isChecked)
        }

        binding.wifiEnabled.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.changeWifiState(isChecked) {
                startActivityForResult(it, 0)
            }
        }

        binding.btEnabled.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.changeBluetoothState(isChecked)
        }

        binding.swEnvironment.setOnCheckedChangeListener { _, isChecked ->
            val environment = if (isChecked) "PRODUCTION" else "QA"
            Toast.makeText(
                requireContext(),
                "You have switch to $environment environment",
                Toast.LENGTH_LONG
            )
                .show()
        }

        binding.debugLogs.setOnCheckedChangeListener { _, isChecked ->
            SettingsViewModel.debugEnabled = isChecked

            Toast.makeText(
                requireContext(),
                if (isChecked) "Logs are enabled" else "Logs are disabled",
                Toast.LENGTH_LONG
            )
                .show()
        }

        return binding.root
    }
}