package com.thebluealliance.android.di

import com.thebluealliance.android.auth.SignInLauncher
import com.thebluealliance.android.auth.UnavailableSignInLauncher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SignInModule {
    @Provides
    @Singleton
    fun provideSignInLauncher(): SignInLauncher = UnavailableSignInLauncher()
}
