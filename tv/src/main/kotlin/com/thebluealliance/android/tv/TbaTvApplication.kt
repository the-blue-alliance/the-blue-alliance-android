package com.thebluealliance.android.tv

import android.app.Application
import com.thebluealliance.android.tv.data.AppContainer
import com.thebluealliance.android.tv.data.DefaultAppContainer

class TbaTvApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
