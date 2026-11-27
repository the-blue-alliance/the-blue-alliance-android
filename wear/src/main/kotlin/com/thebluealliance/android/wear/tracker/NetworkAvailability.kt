package com.thebluealliance.android.wear.tracker

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.core.content.getSystemService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Whether the watch has a network with internet access, directly or through the phone. This is
 * the condition the refresh worker's NetworkType.CONNECTED constraint waits for.
 */
internal fun networkAvailability(context: Context): Flow<Boolean> =
    callbackFlow {
        val connectivity = context.getSystemService<ConnectivityManager>()
        if (connectivity == null) {
            trySend(true)
            awaitClose()
            return@callbackFlow
        }

        fun hasInternet(network: Network?): Boolean =
            connectivity
                .getNetworkCapabilities(network)
                ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

        trySend(hasInternet(connectivity.activeNetwork))
        val callback =
            object : ConnectivityManager.NetworkCallback() {
                override fun onCapabilitiesChanged(
                    network: Network,
                    capabilities: NetworkCapabilities,
                ) {
                    trySend(capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))
                }

                override fun onLost(network: Network) {
                    trySend(false)
                }
            }
        connectivity.registerDefaultNetworkCallback(callback)
        awaitClose { connectivity.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()
