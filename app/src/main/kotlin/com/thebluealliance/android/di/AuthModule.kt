package com.thebluealliance.android.di

import com.google.firebase.auth.FirebaseAuth
import com.thebluealliance.android.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        val auth = FirebaseAuth.getInstance()
        if (BuildConfig.DEBUG) {
            auth.useEmulator("10.0.2.2", 9099)
        }
        return auth
    }
}
