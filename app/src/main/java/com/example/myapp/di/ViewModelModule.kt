package com.example.myapp.di

import com.example.myapp.receivemessage.ReceiveMessageUseCase
import com.example.myapp.sendMessageUseCase.SendMessageUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ViewModelModule {

    @Provides
    @Singleton
    fun providesSendUseCase(): SendMessageUseCase = SendMessageUseCase()

    @Provides
    @Singleton
    fun providesReceiverUseCase(): ReceiveMessageUseCase = ReceiveMessageUseCase()
}