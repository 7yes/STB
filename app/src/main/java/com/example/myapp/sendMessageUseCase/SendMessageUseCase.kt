package com.example.myapp.sendMessageUseCase

import android.net.wifi.p2p.WifiP2pInfo
import android.util.Log
import com.example.myapp.viewmodel.SettingsViewModel
import java.io.DataOutputStream
import java.net.InetSocketAddress
import java.net.Socket

private const val TAG = "SendMessageUseCase"

class SendMessageUseCase {

    fun send(message: String, info: WifiP2pInfo?) {
        info?.let {
            val socket = Socket()
            socket.bind(null)
            socket.connect(InetSocketAddress(it.groupOwnerAddress.hostAddress, 8000), 5000)
            val stream = DataOutputStream(socket.getOutputStream())
            stream.writeUTF(message)
            stream.close()

            if (SettingsViewModel.debugEnabled) {
                Log.d(TAG, "send: Message sent to the server: $message")
            }
        }
    }
}