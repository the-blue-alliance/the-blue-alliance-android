package com.thebluealliance.android.wear.complication

import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.Icon
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.LongTextComplicationData
import androidx.wear.watchface.complications.data.MonochromaticImage
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import com.thebluealliance.android.wear.R

class TeamTrackingComplicationService : SuspendingComplicationDataSourceService() {

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        val lamp = MonochromaticImage.Builder(
            Icon.createWithResource(this, R.drawable.tba_lamp)
        ).build()

        return when (type) {
            ComplicationType.SHORT_TEXT -> ShortTextComplicationData.Builder(
                text = PlainComplicationText.Builder("~2:30P").build(),
                contentDescription = PlainComplicationText.Builder("Team 177 next match: Q18 at ~2:30P").build(),
            )
                .setTitle(PlainComplicationText.Builder("Q18").build())
                .setMonochromaticImage(lamp)
                .build()

            ComplicationType.LONG_TEXT -> LongTextComplicationData.Builder(
                text = PlainComplicationText.Builder("Q18 ~2:30P").build(),
                contentDescription = PlainComplicationText.Builder("Team 177 next match: Q18 at ~2:30P").build(),
            )
                .setTitle(PlainComplicationText.Builder("177 — Hartford").build())
                .setMonochromaticImage(lamp)
                .build()

            else -> null
        }
    }

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData? {
        val complicationId = request.complicationInstanceId
        val tapAction = configPendingIntent(complicationId)
        val prefs = TeamTrackingComplicationPreferences(applicationContext, complicationId)
        val teamNumber = prefs.teamNumber

        if (teamNumber.isBlank()) {
            return buildFallback(request.complicationType, tapAction)
        }

        val matchLabel = prefs.matchLabel
        val matchTime = prefs.matchTime
        val avatarBase64 = prefs.avatarBase64
        val activeEventName = prefs.activeEventName
        val upcomingName = prefs.upcomingEventName
        val upcomingDate = prefs.upcomingEventDate

        val image = avatarBase64?.let { AvatarConverter.toMonochromaticImage(it) }
            ?: MonochromaticImage.Builder(
                Icon.createWithResource(this, R.drawable.tba_lamp)
            ).build()

        return when (request.complicationType) {
            ComplicationType.SHORT_TEXT -> buildShortText(
                teamNumber, matchLabel, matchTime, upcomingName, upcomingDate, image, tapAction,
            )
            ComplicationType.LONG_TEXT -> buildLongText(
                teamNumber, matchLabel, matchTime, activeEventName, upcomingName, upcomingDate, image, tapAction,
            )
            else -> null
        }
    }

    // region SHORT_TEXT

    private fun buildShortText(
        teamNumber: String,
        matchLabel: String,
        matchTime: String,
        upcomingName: String,
        upcomingDate: String,
        image: MonochromaticImage,
        tapAction: PendingIntent,
    ): ShortTextComplicationData {
        if (matchLabel.isNotBlank()) {
            return ShortTextComplicationData.Builder(
                text = PlainComplicationText.Builder(matchTime).build(),
                contentDescription = PlainComplicationText.Builder("Team $teamNumber next match: $matchLabel at $matchTime").build(),
            )
                .setTitle(PlainComplicationText.Builder(matchLabel).build())
                .setMonochromaticImage(image)
                .setTapAction(tapAction)
                .build()
        }

        if (upcomingName.isNotBlank()) {
            return ShortTextComplicationData.Builder(
                text = PlainComplicationText.Builder(upcomingDate).build(),
                contentDescription = PlainComplicationText.Builder("Team $teamNumber: $upcomingName on $upcomingDate").build(),
            )
                .setTitle(PlainComplicationText.Builder(upcomingName).build())
                .setMonochromaticImage(image)
                .setTapAction(tapAction)
                .build()
        }

        return ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder(teamNumber).build(),
            contentDescription = PlainComplicationText.Builder("Team $teamNumber").build(),
        )
            .setMonochromaticImage(image)
            .setTapAction(tapAction)
            .build()
    }

    // endregion

    // region LONG_TEXT

    private fun buildLongText(
        teamNumber: String,
        matchLabel: String,
        matchTime: String,
        activeEventName: String,
        upcomingName: String,
        upcomingDate: String,
        image: MonochromaticImage,
        tapAction: PendingIntent,
    ): LongTextComplicationData {
        if (matchLabel.isNotBlank()) {
            // "Q18 ~2:30P" / "177 — Hartford"
            val eventSuffix = if (activeEventName.isNotBlank()) " — $activeEventName" else ""
            return LongTextComplicationData.Builder(
                text = PlainComplicationText.Builder("$matchLabel $matchTime").build(),
                contentDescription = PlainComplicationText.Builder("Team $teamNumber next match: $matchLabel at $matchTime").build(),
            )
                .setTitle(PlainComplicationText.Builder("$teamNumber$eventSuffix").build())
                .setMonochromaticImage(image)
                .setTapAction(tapAction)
                .build()
        }

        if (upcomingName.isNotBlank()) {
            // "Mar 27" / "177 — Hartford"
            return LongTextComplicationData.Builder(
                text = PlainComplicationText.Builder(upcomingDate).build(),
                contentDescription = PlainComplicationText.Builder("Team $teamNumber: $upcomingName on $upcomingDate").build(),
            )
                .setTitle(PlainComplicationText.Builder("$teamNumber — $upcomingName").build())
                .setMonochromaticImage(image)
                .setTapAction(tapAction)
                .build()
        }

        return LongTextComplicationData.Builder(
            text = PlainComplicationText.Builder("Team $teamNumber").build(),
            contentDescription = PlainComplicationText.Builder("Team $teamNumber").build(),
        )
            .setMonochromaticImage(image)
            .setTapAction(tapAction)
            .build()
    }

    // endregion

    private fun buildFallback(type: ComplicationType, tapAction: PendingIntent): ComplicationData {
        val lamp = MonochromaticImage.Builder(
            Icon.createWithResource(this, R.drawable.tba_lamp)
        ).build()

        return when (type) {
            ComplicationType.LONG_TEXT -> LongTextComplicationData.Builder(
                text = PlainComplicationText.Builder("The Blue Alliance").build(),
                contentDescription = PlainComplicationText.Builder("The Blue Alliance — tap to configure").build(),
            )
                .setMonochromaticImage(lamp)
                .setTapAction(tapAction)
                .build()

            else -> ShortTextComplicationData.Builder(
                text = PlainComplicationText.Builder("TBA").build(),
                contentDescription = PlainComplicationText.Builder("The Blue Alliance — tap to configure").build(),
            )
                .setMonochromaticImage(lamp)
                .setTapAction(tapAction)
                .build()
        }
    }

    private fun configPendingIntent(complicationId: Int): PendingIntent {
        val intent = Intent(applicationContext, TeamTrackingComplicationConfigActivity::class.java).apply {
            putExtra("android.support.wearable.complications.EXTRA_CONFIG_COMPLICATION_ID", complicationId)
        }
        return PendingIntent.getActivity(
            applicationContext,
            complicationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    override fun onComplicationDeactivated(complicationInstanceId: Int) {
        super.onComplicationDeactivated(complicationInstanceId)
        TeamTrackingComplicationPreferences.removeComplicationId(applicationContext, complicationInstanceId)
        TeamTrackingComplicationPreferences(applicationContext, complicationInstanceId).clear()
    }

    override fun onComplicationActivated(complicationInstanceId: Int, type: ComplicationType) {
        super.onComplicationActivated(complicationInstanceId, type)
        TeamTrackingComplicationPreferences.addComplicationId(applicationContext, complicationInstanceId)
    }
}
