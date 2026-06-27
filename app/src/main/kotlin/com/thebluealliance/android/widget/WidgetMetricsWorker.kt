package com.thebluealliance.android.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.core.content.edit
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

/**
 * Daily rollup of Team Tracking widget engagement (views / visible time) from
 * `AppWidgetManager.queryAppWidgetEvents`, folded by widget state x size-tier and forwarded to
 * Firebase Analytics as `widget_engagement` (see [WidgetAnalytics]).
 *
 * Views only exist on Android 16 QPR2+ — we gate on Android 17 (`SDK_INT >= 37`), a safe major
 * check that avoids touching the API-36-only `SDK_INT_FULL` field on older devices, at the cost
 * of not collecting views on the comparatively rare Android 16 QPR2 devices. Clicks
 * (`widget_click`) are logged in-process and are unaffected by this gate.
 */
@HiltWorker
class WidgetMetricsWorker
    @AssistedInject
    constructor(
        @Assisted appContext: Context,
        @Assisted workerParams: WorkerParameters,
    ) : CoroutineWorker(appContext, workerParams) {
        companion object {
            private const val TAG = "WidgetMetricsWorker"
            private const val WORK_NAME = "widget_metrics_rollup"
            private const val PREFS = "widget_metrics"
            private const val KEY_LAST_RUN = "last_run_millis"
            private const val KEY_SEEN = "seen_events"

            // Daily cadence; overlap the previous window so events finalizing near the boundary
            // aren't dropped, and dedup by identity so the overlap never double-counts.
            private val DEFAULT_WINDOW_MS = TimeUnit.HOURS.toMillis(24)
            private val OVERLAP_MS = TimeUnit.HOURS.toMillis(6)

            // Caps query cost + seen-set size. An outage longer than this drops the excess window
            // — an accepted lossy bound for best-effort daily metrics.
            private val MAX_LOOKBACK_MS = TimeUnit.HOURS.toMillis(48)

            @ChecksSdkIntAtLeast(api = 37)
            fun isSupported(): Boolean = Build.VERSION.SDK_INT >= 37

            /** Idempotent (KEEP) — also called from onUpdate so existing installs self-heal. */
            fun enqueueDaily(context: Context) {
                if (!isSupported()) return
                val request =
                    PeriodicWorkRequestBuilder<WidgetMetricsWorker>(1, TimeUnit.DAYS).build()
                WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    request,
                )
            }

            fun cancel(context: Context) {
                WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
            }
        }

        override suspend fun doWork(): Result {
            if (!isSupported()) return Result.success()
            return try {
                val context = applicationContext
                val appWidgetManager =
                    AppWidgetManager.getInstance(context) ?: return Result.success()
                val glanceManager = GlanceAppWidgetManager(context)
                val glanceIds = glanceManager.getGlanceIds(TeamTrackingWidget::class.java)
                if (glanceIds.isEmpty()) return Result.success()

                // appWidgetId -> (widget_state, size_tier) for the widgets present right now.
                val meta = HashMap<Int, Pair<String, String>>()
                for (glanceId in glanceIds) {
                    try {
                        val appWidgetId = glanceManager.getAppWidgetId(glanceId)
                        val state =
                            getAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId)
                        val widgetState =
                            state[TeamTrackingWidgetKeys.WIDGET_STATE]
                                ?: WidgetAnalytics.STATE_UNKNOWN
                        val options = appWidgetManager.getAppWidgetOptions(appWidgetId)
                        val minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
                        val maxHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT)
                        val tier =
                            if (minWidth <= 0 || maxHeight <= 0) {
                                // Launcher hasn't reported size yet — don't pollute the "tiny" bucket.
                                WidgetSizeTier.UNKNOWN
                            } else {
                                WidgetSizeTier.classify(minWidth, maxHeight)
                            }
                        meta[appWidgetId] = widgetState to tier
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to resolve widget meta for $glanceId", e)
                    }
                }

                val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                val now = System.currentTimeMillis()
                val lastRun = prefs.getLong(KEY_LAST_RUN, now - DEFAULT_WINDOW_MS)
                val start = (lastRun - OVERLAP_MS).coerceAtLeast(now - MAX_LOOKBACK_MS)

                val events = appWidgetManager.queryAppWidgetEvents(start, now)
                val seen = HashSet(prefs.getStringSet(KEY_SEEN, emptySet()).orEmpty())
                val snapshots = ArrayList<WidgetEventSnapshot>()
                for (event in events) {
                    // Identity over the overlapping window: a re-seen (id, start, end) is skipped.
                    val key = "${event.appWidgetId}:${event.start.toEpochMilli()}:${event.end.toEpochMilli()}"
                    if (!seen.add(key)) continue
                    snapshots.add(
                        WidgetEventSnapshot(
                            appWidgetId = event.appWidgetId,
                            visibleMillis = event.visibleDuration.toMillis(),
                        ),
                    )
                }

                // Persist dedup bookkeeping BEFORE emitting, so a crash mid-emit can't replay the
                // window — losing one window (under-count) is safer than double-counting. Prune the
                // seen-set to the lookback horizon (key suffix is the end epoch millis).
                val cutoff = now - MAX_LOOKBACK_MS
                val pruned =
                    seen
                        .filter { (it.substringAfterLast(':').toLongOrNull() ?: 0L) >= cutoff }
                        .toSet()
                prefs.edit {
                    putLong(KEY_LAST_RUN, now)
                    putStringSet(KEY_SEEN, pruned)
                }

                val analytics = Firebase.analytics
                for (group in WidgetAnalytics.foldEngagement(snapshots) { meta[it] }) {
                    analytics.logEvent(WidgetAnalytics.EVENT_WIDGET_ENGAGEMENT) {
                        param(WidgetAnalytics.PARAM_WIDGET_STATE, group.widgetState)
                        param(WidgetAnalytics.PARAM_WIDGET_SIZE_TIER, group.tier)
                        param(
                            WidgetAnalytics.PARAM_VISIBLE_WINDOW_COUNT,
                            group.visibleWindowCount.toLong(),
                        )
                        param(WidgetAnalytics.PARAM_IMPRESSION_MS, group.impressionMillis)
                    }
                }
                Result.success()
            } catch (e: Exception) {
                // Best-effort metrics: a missed window self-heals via the next run's overlap, so
                // don't retry — retrying risks re-emitting events already counted this run.
                Log.e(TAG, "Failed to roll up widget metrics", e)
                Result.success()
            }
        }
    }
