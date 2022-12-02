package com.example.myapp.friendsusecase

import android.annotation.SuppressLint
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import com.example.myapp.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

private const val TAG = "FriendsUseCase"

class FriendsUseCase @Inject constructor(
    private val p2pManager: WifiP2pManager,
    private val channel: WifiP2pManager.Channel
) : WifiP2pManager.PeerListListener, WifiP2pManager.ActionListener {

    private val _usersFlow: MutableStateFlow<List<WifiP2pDevice>> = MutableStateFlow(mutableListOf())
    val usersFlow: StateFlow<List<WifiP2pDevice>> get() = _usersFlow

    @SuppressLint("MissingPermission")
    fun discoverPeers() {
        p2pManager.discoverPeers(
            channel,
            this
        )
    }

    override fun onPeersAvailable(peers: WifiP2pDeviceList?) {
        peers?.let {
            _usersFlow.value = it.deviceList.toList()
        } ?: run {
            if (SettingsViewModel.debugEnabled) {
                Log.d(TAG, "onPeersAvailable: Peers are null")
            }
        }
    }

    override fun onSuccess() {
        if (SettingsViewModel.debugEnabled) {
            Log.d(TAG, "onSuccess: Success discovering peers")
        }
    }
    override fun onFailure(p0: Int) {
        if (SettingsViewModel.debugEnabled) {
            Log.e(TAG, "onFailure: Failure discovering peers $p0")
        }
    }
}