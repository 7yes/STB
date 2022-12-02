package com.example.myapp.connectDeviceUseCase

import android.annotation.SuppressLint
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import com.example.myapp.receivemessage.ReceiveMessageUseCase
import com.example.myapp.viewmodel.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import java.net.Socket
import javax.inject.Inject

private const val TAG = "FriendConnectionUseCase"

class FriendConnectionUseCase @Inject constructor(
    private val p2pManager: WifiP2pManager,
    private val channel: WifiP2pManager.Channel,
    private val receiveMessageUseCase: ReceiveMessageUseCase
) : WifiP2pManager.ConnectionInfoListener, WifiP2pManager.ActionListener {

    private val _deviceInfo: MutableStateFlow<WifiP2pInfo?> = MutableStateFlow(null)
    val deviceInfo: StateFlow<WifiP2pInfo?> get() = _deviceInfo

    @SuppressLint("MissingPermission")
    fun connect(device: WifiP2pDevice) {
        with(WifiP2pConfig().apply {
            deviceAddress = device.deviceAddress
            wps.setup = WpsInfo.PBC
        }) {
            p2pManager.connect(
                channel,
                this,
                this@FriendConnectionUseCase
            )
        }
    }

    override fun onConnectionInfoAvailable(info: WifiP2pInfo?) {
        _deviceInfo.value = info
        isPeerServer = info?.isGroupOwner ?: false

        CoroutineScope(Dispatchers.IO).launch {
            if(info!!.groupFormed) {
                if (isPeerServer) {
                    var socket: Socket? = null
                    try {
                        socket = receiveMessageUseCase.receiveMessage()
                    } catch (e: Exception) {
                        if (SettingsViewModel.debugEnabled) {
                            Log.e(TAG, "SERVER to CLIENT message sending exception: " + e.message)
                        }
                    } finally {
                        try {
                            socket?.close()
                        } catch (e: Exception) {
                            if (SettingsViewModel.debugEnabled) {
                                Log.e(TAG, "sendMessage: ${e.localizedMessage}", e)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onSuccess() {
        if (SettingsViewModel.debugEnabled) {
            Log.d(TAG, "onSuccess: Connection SUCCESS!")
        }
        p2pManager.requestConnectionInfo(channel, this)
    }

    override fun onFailure(p0: Int) {
        if (SettingsViewModel.debugEnabled) {
            Log.e(TAG, "onFailure: Connection failure! $p0")
        }
    }

    companion object {
        var isPeerServer = false
    }
}