package com.example.myapp.viewmodel

import android.net.wifi.p2p.*
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapp.chat.FriendStatusReceiver
import com.example.myapp.connectDeviceUseCase.FriendConnectionUseCase
import com.example.myapp.friendsusecase.FriendsUseCase
import com.example.myapp.receivemessage.ReceiveMessageUseCase
import com.example.myapp.sendMessageUseCase.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.lang.Exception
import java.net.Socket
import javax.inject.Inject

private const val TAG = "MainViewModel"

@HiltViewModel
class MainViewModel @Inject constructor(
    private val channel: WifiP2pManager.Channel,
    private val p2pManager: WifiP2pManager,
    private val ioDispatcher: CoroutineDispatcher,
    private val friendConnectionUseCase: FriendConnectionUseCase,
    private val friendsUseCase: FriendsUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val receiveMessageUseCase: ReceiveMessageUseCase
) : ViewModel() {

    val friendStatusReceiver by lazy {
        FriendStatusReceiver(p2pManager, channel, friendsUseCase, friendConnectionUseCase)
    }

    private val _mFriends: MutableLiveData<List<WifiP2pDevice>> = MutableLiveData()
    val friends: LiveData<List<WifiP2pDevice>> get() = _mFriends

    private val _connected: MutableLiveData<Boolean> = MutableLiveData()
    val connectedToFriend: LiveData<Boolean> get() = _connected

    private val _messages: MutableLiveData<String> = MutableLiveData()
    val messagesReceived: LiveData<String> get() = _messages

    var deviceInfo: WifiP2pInfo? = null
    var selectedUser: WifiP2pDevice? = null

    fun searchForUsersToChat() {
        viewModelScope.launch(ioDispatcher) {
            friendsUseCase.usersFlow.collect {
                _mFriends.postValue(it)
            }
        }

        friendsUseCase.discoverPeers()

        viewModelScope.launch(ioDispatcher) {
            receiveMessageUseCase.messageReceived.collect {
                it?.let {
                    _messages.postValue(it)
                }
            }
        }
    }

    fun connectToFriend(): Boolean {
        selectedUser?.let {
            friendConnectionUseCase.connect(it)
            viewModelScope.launch {
                friendConnectionUseCase.deviceInfo.collect { info ->
                    deviceInfo = info
                }
            }
            return true
        } ?: return false
    }

    fun sendMessage(message: String) {
        viewModelScope.launch(ioDispatcher) {
            if(!FriendConnectionUseCase.isPeerServer) {
                try {
                    sendMessageUseCase.send(message, deviceInfo)
                } catch (e: Exception) {
                    if (SettingsViewModel.debugEnabled) {
                        Log.e(
                            TAG,
                            "sendMessage: Error sending message CLIENT to SERVER: ${e.localizedMessage}",
                            e
                        )
                    }
                }
            } else {
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