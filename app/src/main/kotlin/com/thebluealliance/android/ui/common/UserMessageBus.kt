package com.thebluealliance.android.ui.common

import android.content.Context
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Short messages for the user, shown as a snackbar inside the app's window by [UserMessageHost].
 *
 * Replaces Toasts, which Horizon OS draws outside the app panel. Buffered, so a message sent
 * while no host is collecting (e.g. returning from the sign-in browser) is shown once one is.
 */
@Singleton
class UserMessageBus
    @Inject
    constructor() {
        private val channel = Channel<String>(Channel.BUFFERED)

        val messages: Flow<String> = channel.receiveAsFlow()

        fun show(message: String) {
            channel.trySend(message)
        }
    }

@EntryPoint
@InstallIn(SingletonComponent::class)
internal interface UserMessageBusEntryPoint {
    fun userMessageBus(): UserMessageBus
}

/** The app's [UserMessageBus], for callers Hilt doesn't construct. */
fun Context.userMessageBus(): UserMessageBus =
    EntryPointAccessors
        .fromApplication(this, UserMessageBusEntryPoint::class.java)
        .userMessageBus()

fun Context.showUserMessage(message: String) = userMessageBus().show(message)
