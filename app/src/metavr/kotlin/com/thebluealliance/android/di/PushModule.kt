package com.thebluealliance.android.di

import com.thebluealliance.android.messaging.NoPushRegistrar
import com.thebluealliance.android.messaging.PushRegistrar
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PushModule {
    @Provides
    @Singleton
    fun providePushRegistrar(): PushRegistrar = NoPushRegistrar()
}
