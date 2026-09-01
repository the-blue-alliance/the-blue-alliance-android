package com.thebluealliance.android.di

import com.thebluealliance.android.messaging.DeviceRegistrationManager
import com.thebluealliance.android.messaging.PushRegistrar
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class PushModule {
    // Bound rather than provided so TBAFirebaseMessagingService can also inject the
    // concrete manager for its FCM-only onNewToken callback.
    @Binds
    abstract fun bindPushRegistrar(impl: DeviceRegistrationManager): PushRegistrar
}
