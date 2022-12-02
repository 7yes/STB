package com.example.myapp.di

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pManager
import android.os.Looper
import androidx.core.content.getSystemService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    fun providesWifiP2pManager(@ApplicationContext context: Context): WifiP2pManager =
        context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager

    @Provides
    fun providesWifiChannel(
        @ApplicationContext context: Context,
        wifiManager: WifiP2pManager
    ): WifiP2pManager.Channel =
        wifiManager.initialize(context, Looper.getMainLooper(), null)

    @Provides
    fun providesWifiManager(
        @ApplicationContext context: Context
    ): WifiManager =
        context.getSystemService(Context.WIFI_SERVICE) as WifiManager

    @Provides
    fun providesBTAdapter(): BluetoothAdapter =
        BluetoothAdapter.getDefaultAdapter()

    @Provides
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO
}