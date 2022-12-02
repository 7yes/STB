package com.example.myapp.chat

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pManager
import com.example.myapp.connectDeviceUseCase.FriendConnectionUseCase
import com.example.myapp.friendsusecase.FriendsUseCase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FriendStatusReceiver @Inject constructor(
    private val manager: WifiP2pManager,
    private val channel: WifiP2pManager.Channel,
    private val friendsUseCase: FriendsUseCase,
    private val friendConnectionUseCase: FriendConnectionUseCase
) : BroadcastReceiver() {

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action) {
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                manager.requestPeers(channel, friendsUseCase)
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                manager.requestConnectionInfo(channel, friendConnectionUseCase)
            }
        }
    }

    companion object {
        val intentFilter =
            IntentFilter().apply {
                addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
                addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            }
    }
}