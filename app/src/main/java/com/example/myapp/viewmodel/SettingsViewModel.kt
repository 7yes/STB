package com.example.myapp.viewmodel

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val wifiManager: WifiManager,
    private val btAdapter: BluetoothAdapter
) : ViewModel() {

    val defaultBTState = btAdapter.isEnabled
    val defaultWifiState = wifiManager.isWifiEnabled

    fun changeWifiState(state: Boolean, openSettings: (Intent) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            openSettings(Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY))
        } else {
            wifiManager.isWifiEnabled = state
        }
    }

    @SuppressLint("MissingPermission")
    fun changeBluetoothState(state: Boolean) {
        if (state) {
            btAdapter.enable()
        } else {
            btAdapter.disable()
        }
    }

    fun enableDarkMode(switch: Boolean) {
        if(switch)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    companion object {
        var debugEnabled = true
    }
}