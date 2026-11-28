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

/** A message for [UserMessageBus], with an optional [action] shown as the snackbar's button. */
data class UserMessage(
    val text: String,
    val action: Action? = null,
) {
    /** A snackbar button: [label] (say, "Retry") runs [onPerform] when pressed. */
    class Action(
        val label: String,
        val onPerform: () -> Unit,
    )
}

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
        private val channel = Channel<UserMessage>(Channel.BUFFERED)

        val messages: Flow<UserMessage> = channel.receiveAsFlow()

        fun show(message: String) = show(UserMessage(message))

        /** Shows [message] with a Retry button that runs [retry]. */
        fun showRetry(
            message: String,
            retry: () -> Unit,
        ) = show(UserMessage(message, UserMessage.Action(RETRY, retry)))

        /** Shows [message] with an Undo button that runs [undo]. */
        fun showUndo(
            message: String,
            undo: () -> Unit,
        ) = show(UserMessage(message, UserMessage.Action(UNDO, undo)))

        fun show(message: UserMessage) {
            channel.trySend(message)
        }

        companion object {
            const val RETRY = "Retry"
            const val UNDO = "Undo"
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
