package com.thebluealliance.android.di

import com.thebluealliance.android.widget.GlanceWidgetRefresher
import com.thebluealliance.android.widget.WidgetRefresher
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class WidgetModule {
    @Binds
    abstract fun bindWidgetRefresher(impl: GlanceWidgetRefresher): WidgetRefresher
}
