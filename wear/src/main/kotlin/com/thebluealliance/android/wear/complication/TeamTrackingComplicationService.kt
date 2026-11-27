package com.thebluealliance.android.wear.complication

import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.Icon
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationText
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.CountDownTimeReference
import androidx.wear.watchface.complications.data.LongTextComplicationData
import androidx.wear.watchface.complications.data.MonochromaticImage
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.data.TimeDifferenceComplicationText
import androidx.wear.watchface.complications.data.TimeDifferenceStyle
import androidx.wear.watchface.complications.data.TimeRange
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import com.thebluealliance.android.wear.R
import com.thebluealliance.android.wear.tracker.TeamTrackerActivity
import com.thebluealliance.android.wear.tracker.TeamTrackerPreferences
import com.thebluealliance.android.wear.worker.TeamTrackingComplicationWorker
import java.time.Instant
import java.util.concurrent.TimeUnit

/**
 * Shows the tracked team's next match, counting down to it, or its next event. Reads the same
 * [TeamTrackerPreferences] as the tracker app, so a complication shows what the app shows from the
 * moment it's added.
 */
class TeamTrackingComplicationService : SuspendingComplicationDataSourceService() {
    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        val lamp = lampImage()
        val description = plain("Team 177 next match: Q18 in 25 minutes")
        return when (type) {
            ComplicationType.SHORT_TEXT ->
                ShortTextComplicationData
                    .Builder(text = plain("~25m"), contentDescription = description)
                    .setTitle(plain("Q18"))
                    .setMonochromaticImage(lamp)
                    .build()

            ComplicationType.LONG_TEXT ->
                LongTextComplicationData
                    .Builder(text = plain("Q18 ~25m"), contentDescription = description)
                    .setTitle(plain("177 — Hartford"))
                    .setMonochromaticImage(lamp)
                    .build()

            else -> null
        }
    }

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData? {
        val tapAction = openTrackerIntent(request.complicationInstanceId)
        val prefs = TeamTrackerPreferences(applicationContext)
        val content = ComplicationContentLogic.resolve(prefs.snapshot(), Instant.now())
        val image =
            prefs.avatarBase64?.let { AvatarConverter.toMonochromaticImage(it) } ?: lampImage()
        val type = request.complicationType
        if (type != ComplicationType.SHORT_TEXT && type != ComplicationType.LONG_TEXT) return null
        val long = type == ComplicationType.LONG_TEXT

        return when (content) {
            ComplicationContent.NotConfigured ->
                build(
                    long,
                    text = plain(if (long) "The Blue Alliance" else "TBA"),
                    title = null,
                    description = plain("The Blue Alliance — tap to configure"),
                    image = lampImage(),
                    tapAction = tapAction,
                )

            is ComplicationContent.NextMatch -> nextMatch(long, content, image, tapAction)

            is ComplicationContent.UpcomingEvent ->
                build(
                    long,
                    // "Mar 27" / "Hartford", or "Mar 27" / "177 — Hartford"
                    text = plain(content.date),
                    title = plain(upcomingTitle(long, content)),
                    description =
                        plain(
                            "Team ${content.teamNumber}: ${content.eventName} on ${content.date}",
                        ),
                    image = image,
                    tapAction = tapAction,
                )

            is ComplicationContent.TeamOnly ->
                build(
                    long,
                    text = plain(if (long) "Team ${content.teamNumber}" else content.teamNumber),
                    title = null,
                    description = plain("Team ${content.teamNumber}"),
                    image = image,
                    tapAction = tapAction,
                )
        }
    }

    /**
     * "Q18" over "~25m" (short), or "Q18 ~25m" under "177 — Hartford" (long). The countdown is a
     * [TimeDifferenceComplicationText], so the watch face keeps it current and shows "Now" once
     * the match is due; the data stops showing [ComplicationContent.NextMatch.validUntil].
     */
    private fun nextMatch(
        long: Boolean,
        content: ComplicationContent.NextMatch,
        image: MonochromaticImage,
        tapAction: PendingIntent,
    ): ComplicationData {
        val prefix = if (content.isEstimate) "~" else ""
        val label = content.matchLabel
        val text: ComplicationText
        val description: ComplicationText
        if (content.matchTime == null) {
            text = plain(if (long) "$label TBD" else "TBD")
            description = plain("Team ${content.teamNumber} next match: $label, time to be decided")
        } else {
            text = countdown(content.matchTime, if (long) "$label $prefix^1" else "$prefix^1")
            description =
                countdown(content.matchTime, "Team ${content.teamNumber} next match: $label, ^1")
        }
        val eventSuffix = if (content.eventName.isNotBlank()) " — ${content.eventName}" else ""
        return build(
            long,
            text = text,
            title = plain(if (long) "${content.teamNumber}$eventSuffix" else label),
            description = description,
            image = image,
            tapAction = tapAction,
            validUntil = content.validUntil,
        )
    }

    private fun upcomingTitle(
        long: Boolean,
        content: ComplicationContent.UpcomingEvent,
    ): String = if (long) "${content.teamNumber} — ${content.eventName}" else content.eventName

    private fun countdown(
        matchTime: Instant,
        template: String,
    ): ComplicationText =
        TimeDifferenceComplicationText
            .Builder(TimeDifferenceStyle.SHORT_SINGLE_UNIT, CountDownTimeReference(matchTime))
            .setMinimumTimeUnit(TimeUnit.MINUTES)
            .setDisplayAsNow(true)
            .setText(template)
            .build()

    private fun build(
        long: Boolean,
        text: ComplicationText,
        title: ComplicationText?,
        description: ComplicationText,
        image: MonochromaticImage,
        tapAction: PendingIntent,
        validUntil: Instant? = null,
    ): ComplicationData {
        val validTimeRange = validUntil?.let { TimeRange.before(it) }
        return if (long) {
            LongTextComplicationData
                .Builder(text = text, contentDescription = description)
                .setTitle(title)
                .setMonochromaticImage(image)
                .setTapAction(tapAction)
                .setValidTimeRange(validTimeRange)
                .build()
        } else {
            ShortTextComplicationData
                .Builder(text = text, contentDescription = description)
                .setTitle(title)
                .setMonochromaticImage(image)
                .setTapAction(tapAction)
                .setValidTimeRange(validTimeRange)
                .build()
        }
    }

    private fun plain(text: String): ComplicationText = PlainComplicationText.Builder(text).build()

    private fun lampImage(): MonochromaticImage =
        MonochromaticImage.Builder(Icon.createWithResource(this, R.drawable.tba_lamp)).build()

    private fun openTrackerIntent(complicationId: Int): PendingIntent {
        val intent = Intent(applicationContext, TeamTrackerActivity::class.java)
        return PendingIntent.getActivity(
            applicationContext,
            complicationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    override fun onComplicationActivated(
        complicationInstanceId: Int,
        type: ComplicationType,
    ) {
        super.onComplicationActivated(complicationInstanceId, type)
        // The first request is answered from whatever the tracker has stored; fetch fresh data so
        // a newly added complication doesn't wait for the next scheduled refresh.
        TeamTrackingComplicationWorker.enqueueImmediateRefresh(applicationContext)
    }

    override fun onComplicationDeactivated(complicationInstanceId: Int) {
        super.onComplicationDeactivated(complicationInstanceId)
        // Older versions kept a per-complication copy of the data; drop it if it's still there.
        deleteSharedPreferences("complication_$complicationInstanceId")
    }
}

private fun TeamTrackerPreferences.snapshot() =
    TrackerSnapshot(
        teamNumber = teamNumber,
        eventName = if (hasActiveEvent) eventName else "",
        nextMatchLabel = nextMatchLabel,
        nextMatchTime = nextMatchEpochSeconds?.let(Instant::ofEpochSecond),
        nextMatchIsEstimate = nextMatchTimeIsEstimate,
        upcomingEventName = upcomingEventName,
        upcomingEventDate = upcomingEventDate,
    )
