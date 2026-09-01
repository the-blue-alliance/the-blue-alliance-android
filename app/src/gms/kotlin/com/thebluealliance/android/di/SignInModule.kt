package com.thebluealliance.android.di

import com.google.firebase.auth.FirebaseAuth
import com.thebluealliance.android.auth.CredentialManagerSignInLauncher
import com.thebluealliance.android.auth.SignInLauncher
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
    fun provideSignInLauncher(firebaseAuth: FirebaseAuth): SignInLauncher =
        CredentialManagerSignInLauncher(firebaseAuth)
}
