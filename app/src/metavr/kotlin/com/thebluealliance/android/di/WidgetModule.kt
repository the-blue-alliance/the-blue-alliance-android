package com.thebluealliance.android.di

import com.thebluealliance.android.widget.NoWidgetRefresher
import com.thebluealliance.android.widget.WidgetRefresher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WidgetModule {
    @Provides
    @Singleton
    fun provideWidgetRefresher(): WidgetRefresher = NoWidgetRefresher()
}
