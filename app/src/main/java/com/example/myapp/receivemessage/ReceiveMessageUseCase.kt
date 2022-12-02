package com.example.myapp.receivemessage

import android.util.Log
import com.example.myapp.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.DataInputStream
import java.net.ServerSocket
import java.net.Socket

private const val TAG = "ReceiveMessageUseCase"

class ReceiveMessageUseCase {

    private val _message: MutableStateFlow<String?> = MutableStateFlow(null)
    val messageReceived: StateFlow<String?> get() = _message

    fun receiveMessage(): Socket {
        val serverSocket = ServerSocket(8000)

        //Reading the message from the client
        val socket = serverSocket.accept()
        val stream = DataInputStream(socket.getInputStream())
        val messageReceived = stream.readUTF()

        if (SettingsViewModel.debugEnabled) {
            Log.d(TAG, "receiveMessage: Message coming from server is: $messageReceived")
        }

        _message.value = messageReceived

        return socket
    }
}