package com.thebluealliance.android.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.google.android.play.integrity.internal.ac

class Navigator(
    val state: NavigationState,
    val activity: Activity,
) {
    fun navigate(route: NavKey) {
        if (route in state.topLevelRoutes) {
            state.topLevelRoute = route
        } else {
            state.backStacks[state.topLevelRoute]?.add(route)
        }
    }

    /**
     * Navigate up. This should be used for all in-app "back" arrow navigation.
     *
     * It is subtly different from [goBack] in that if we were launched via deeplink and we are
     * still on that first screen, [navigateUp] will generate a synthetic back stack and navigate
     * to the parent screen whereas [goBack] will simply pop the back stack and return the user
     * to the app they were previously using.
     *
     * See https://developer.android.com/guide/navigation/principles
     */
    fun navigateUp() {
        state.currentStack.navigateUp(activity)
    }

    /**
     * Pop our back stack.
     *
     * This should be used when reacting to system back gestures, not in-app back icon presses.
     * It will not create a synthetic back stack when we have deeplinked to a scren.
     *
     * See https://developer.android.com/guide/navigation/principles
     */
    fun goBack() {
        // If we're at the base of the current route, go back to the start route stack.
        if (state.currentRoute == state.topLevelRoute) {
            state.topLevelRoute = state.startRoute
        } else {
            state.currentStack.removeLastOrNull()
        }
    }

    /**
     * If this app was started on its own Task stack, then navigate up would simply
     * pop from the backStack.
     *
     * Otherwise, it will restart this app in a new Task and build a full synthetic backStack
     * starting from the root key to current key's parent (current key is "popped" upon user click on up button).
     * This operation is required because by definition, an Up button is expected to:
     * 1. Move from current screen to its hierarchical parent
     * 2. Stay within this app
     *
     * Therefore, we need to build a synthetic backStack to fulfill expectation 1., and we need to
     * restart the app in its own Task so that this app's screens are displayed within
     * this app instead of being displayed within the originating app that triggered the deeplink.
     */
    private fun NavBackStack<NavKey>.navigateUp(
        activity: Activity,
    ) {
        /**
         * The root key (the first key on synthetic backStack) would/should never display the Up button.
         * So if the backStack only contains a non-root key, it means a synthetic backStack had not
         * been built (aka the app was opened in the originating Task).
         */
        if (size == 1) {
            // All deeplinks currently use Screen.Events as their parent
            val deeplinkKey = Screen.Events

            /**
             * create a [androidx.core.app.TaskStackBuilder] that will restart the
             * Activity as the root Activity of a new Task
             */
            val builder = createTaskStackBuilder(deeplinkKey, activity, activity)
            // ensure current activity is finished
            activity.finish()
            // trigger restart
            builder.startActivities()
        } else {
            removeLastOrNull()
        }
    }

    /**
     *  Creates a [androidx.core.app.TaskStackBuilder].
     *
     *  The builder takes the current context and Activity and builds a new Task stack with the
     *  restarted activity as the root Activity. The resulting TaskStack is used to restart
     *  the Activity in its own Task.
     */
    private fun createTaskStackBuilder(
        deeplinkKey: NavKey?,
        activity: Activity,
        context: Context
    ): TaskStackBuilder {
        /**
         * The intent to restart the current activity.
         */
        val intent = Intent(context, activity.javaClass)

        /**
         * Pass in the deeplink url of the target key so that upon restart, the app
         * can build the synthetic backStack starting from the deeplink key all the way up to the
         * root key.
         *
         * See [buildBackStack] for building synthetic backStack.
         */
        if (deeplinkKey != null) {
            intent.data = "https://thebluealliance.com".toUri()
        }

        /**
         * Ensure that the Activity is restarted as the root of a new Task
         */
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

        /**
         * Lastly, attach the intent to the TaskStackBuilder.
         *
         * By using `addNextIntentWithParentStack`, the TaskStackBuilder will automatically
         * add the intents for the parent activities (if any) of [activity].
         */
        return TaskStackBuilder.create(context).addNextIntentWithParentStack(intent)
    }
}
