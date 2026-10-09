package com.thebluealliance.android.tv

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import com.thebluealliance.android.tv.data.AppContainer
import com.thebluealliance.android.tv.data.DefaultAppContainer

class TbaTvApplication :
    Application(),
    SingletonImageLoader.Factory {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
        container.apiKeyProvider.init()
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader = container.imageLoader
}
