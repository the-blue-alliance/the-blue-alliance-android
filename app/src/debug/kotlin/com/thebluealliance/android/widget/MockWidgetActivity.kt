package com.thebluealliance.android.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.room.Room
import com.thebluealliance.android.data.local.TBADatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * Debug-only activity that injects mock data into a widget's DataStore preferences,
 * bypassing [TeamTrackingWorker] entirely. This produces deterministic widget screenshots
 * without requiring network access or live event data.
 *
 * Avatars are read from the local Room database so they stay up-to-date and don't bloat
 * the source code with hardcoded base64 strings.
 *
 * Usage:
 *   adb shell am start -n com.thebluealliance.androidclient.development/com.thebluealliance.android.widget.MockWidgetActivity \
 *     --ei index 0 --es mock at-event
 *
 *   adb shell am start -n com.thebluealliance.androidclient.development/com.thebluealliance.android.widget.MockWidgetActivity \
 *     --ei index 1 --es mock upcoming
 *
 * Presets:
 *   "at-event"  — Team 177 at NE District Event with last/next match (matches sampleData())
 *   "upcoming"  — Team 254 with 3 upcoming events, no current event
 */
class MockWidgetActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MockWidget"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val index = intent?.getIntExtra("index", -1) ?: -1
        val preset = intent?.getStringExtra("mock")

        if (index < 0 || preset == null) {
            Log.e(TAG, "Usage: --ei index <N> --es mock <at-event|upcoming>")
            finish()
            return
        }

        val awm = AppWidgetManager.getInstance(this)
        val provider = ComponentName(this, TeamTrackingWidgetReceiver::class.java)
        val widgetIds = awm.getAppWidgetIds(provider)

        if (index >= widgetIds.size) {
            Log.e(TAG, "Widget index $index out of range (${widgetIds.size} widget(s) found)")
            finish()
            return
        }

        val appWidgetId = widgetIds[index]
        Log.d(TAG, "Injecting preset '$preset' into widget index=$index (appWidgetId=$appWidgetId)")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val manager = GlanceAppWidgetManager(this@MockWidgetActivity)
                val glanceId = manager.getGlanceIdBy(appWidgetId)

                updateAppWidgetState(this@MockWidgetActivity, glanceId) { prefs ->
                    when (preset) {
                        "at-event" -> writeAtEventPreset(prefs)
                        "upcoming" -> writeUpcomingPreset(prefs)
                        else -> {
                            Log.e(TAG, "Unknown preset: $preset (expected 'at-event' or 'upcoming')")
                            return@updateAppWidgetState
                        }
                    }
                }

                // Re-render — no WorkManager enqueue, so mock data stays put
                TeamTrackingWidget().update(this@MockWidgetActivity, glanceId)
                Log.d(TAG, "Done. Widget $appWidgetId now shows '$preset' mock data.")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to inject mock data into widget $appWidgetId", e)
            }
            finish()
        }
    }

    /** Look up the team's avatar from the local Room DB (current year, falling back to last year). */
    private suspend fun fetchAvatarBase64(teamKey: String): String? {
        val db = Room.databaseBuilder(applicationContext, TBADatabase::class.java, "tba.db")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
        return try {
            val year = LocalDate.now().year
            val media = db.mediaDao().observeByTeamYear(teamKey, year).firstOrNull()
                ?: emptyList()
            val avatar = media.firstOrNull { it.type == "avatar" && it.base64Image != null }
                ?: db.mediaDao().observeByTeamYear(teamKey, year - 1).firstOrNull()
                    ?.firstOrNull { it.type == "avatar" && it.base64Image != null }
            avatar?.base64Image
        } finally {
            db.close()
        }
    }

    private suspend fun writeAtEventPreset(prefs: androidx.datastore.preferences.core.MutablePreferences) {
        // Team 177 "Bobcat Robotics" at NE District Event — matches sampleData()
        prefs[TeamTrackingWidgetKeys.TEAM_NUMBER] = "177"
        prefs[TeamTrackingWidgetKeys.TEAM_KEY] = "frc177"
        prefs[TeamTrackingWidgetKeys.TEAM_NICKNAME] = "Bobcat Robotics"

        val avatar = fetchAvatarBase64("frc177")
        if (avatar != null) {
            prefs[TeamTrackingWidgetKeys.AVATAR_BASE64] = avatar
        } else {
            Log.w(TAG, "No avatar found for frc177 in local DB")
            prefs.remove(TeamTrackingWidgetKeys.AVATAR_BASE64)
        }

        prefs[TeamTrackingWidgetKeys.NEXT_ALLIANCE] = "red"
        prefs[TeamTrackingWidgetKeys.EVENT_KEY] = "2026ne"
        prefs[TeamTrackingWidgetKeys.EVENT_NAME] = "NE District Event"
        prefs[TeamTrackingWidgetKeys.RECORD] = "5-2-0"
        prefs[TeamTrackingWidgetKeys.LAST_UPDATED] = "just now"

        // Last match: Q12 — blue 177 wins
        prefs[TeamTrackingWidgetKeys.LAST_MATCH_LABEL] = "Q12"
        prefs[TeamTrackingWidgetKeys.LAST_MATCH_RED_TEAMS] = "195, 1519, 4909"
        prefs[TeamTrackingWidgetKeys.LAST_MATCH_BLUE_TEAMS] = "177, 1153, 2067"
        prefs[TeamTrackingWidgetKeys.LAST_MATCH_RED_SCORE] = "52"
        prefs[TeamTrackingWidgetKeys.LAST_MATCH_BLUE_SCORE] = "71"
        prefs[TeamTrackingWidgetKeys.LAST_MATCH_WINNING_ALLIANCE] = "blue"
        prefs[TeamTrackingWidgetKeys.LAST_MATCH_RED_RP] = "false, false"
        prefs[TeamTrackingWidgetKeys.LAST_MATCH_BLUE_RP] = "true, true"

        // Next match: Q18
        prefs[TeamTrackingWidgetKeys.NEXT_MATCH_LABEL] = "Q18"
        prefs[TeamTrackingWidgetKeys.NEXT_MATCH_RED_TEAMS] = "177, 3467, 5112"
        prefs[TeamTrackingWidgetKeys.NEXT_MATCH_BLUE_TEAMS] = "1073, 2791, 3958"
        prefs[TeamTrackingWidgetKeys.NEXT_MATCH_TIME] = "2:30 PM"
        prefs[TeamTrackingWidgetKeys.NEXT_MATCH_TIME_IS_ESTIMATE] = "true"

        // No upcoming events when at an event
        prefs.remove(TeamTrackingWidgetKeys.UPCOMING_EVENTS)
    }

    private suspend fun writeUpcomingPreset(prefs: androidx.datastore.preferences.core.MutablePreferences) {
        // Team 254 "The Cheesy Poofs" — not at an event, 3 upcoming events
        prefs[TeamTrackingWidgetKeys.TEAM_NUMBER] = "254"
        prefs[TeamTrackingWidgetKeys.TEAM_KEY] = "frc254"
        prefs[TeamTrackingWidgetKeys.TEAM_NICKNAME] = "The Cheesy Poofs"

        val avatar = fetchAvatarBase64("frc254")
        if (avatar != null) {
            prefs[TeamTrackingWidgetKeys.AVATAR_BASE64] = avatar
        } else {
            Log.w(TAG, "No avatar found for frc254 in local DB")
            prefs.remove(TeamTrackingWidgetKeys.AVATAR_BASE64)
        }

        prefs.remove(TeamTrackingWidgetKeys.NEXT_ALLIANCE)
        prefs.remove(TeamTrackingWidgetKeys.EVENT_KEY)
        prefs.remove(TeamTrackingWidgetKeys.EVENT_NAME)
        prefs.remove(TeamTrackingWidgetKeys.RECORD)
        prefs[TeamTrackingWidgetKeys.LAST_UPDATED] = "just now"

        // No last match
        for (k in TeamTrackingWidgetKeys.ALL_LAST_MATCH_KEYS) prefs.remove(k)

        // No next match
        for (k in TeamTrackingWidgetKeys.ALL_NEXT_MATCH_KEYS) prefs.remove(k)

        // Upcoming events (tab-separated: name\tcity\tdate)
        prefs[TeamTrackingWidgetKeys.UPCOMING_EVENTS] = listOf(
            "Silicon Valley Regional\tSan Jose, CA\tMar 27–29",
            "Sacramento Regional\tSacramento, CA\tApr 3–5",
            "Championship\tHouston, TX\tApr 17–19",
        ).joinToString("\n")
    }
}
