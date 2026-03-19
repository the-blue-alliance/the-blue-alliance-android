package com.thebluealliance.android.wear.complication

import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.Icon
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.MonochromaticImage
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import com.thebluealliance.android.wear.R

class TeamTrackingComplicationService : SuspendingComplicationDataSourceService() {

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        if (type != ComplicationType.SHORT_TEXT) return null
        return ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder("~2:30P").build(),
            contentDescription = PlainComplicationText.Builder("Team 177 next match: Q18 at ~2:30P").build(),
        )
            .setTitle(PlainComplicationText.Builder("Q18").build())
            .setMonochromaticImage(
                MonochromaticImage.Builder(
                    Icon.createWithResource(this, R.drawable.tba_lamp)
                ).build()
            )
            .build()
    }

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData? {
        if (request.complicationType != ComplicationType.SHORT_TEXT) return null

        val complicationId = request.complicationInstanceId
        val tapAction = configPendingIntent(complicationId)
        val prefs = TeamTrackingComplicationPreferences(applicationContext, complicationId)
        val teamNumber = prefs.teamNumber

        if (teamNumber.isBlank()) {
            return buildFallback(tapAction)
        }

        val matchLabel = prefs.matchLabel
        val matchTime = prefs.matchTime
        val avatarBase64 = prefs.avatarBase64

        val image = avatarBase64?.let { AvatarConverter.toMonochromaticImage(it) }
            ?: MonochromaticImage.Builder(
                Icon.createWithResource(this, R.drawable.tba_lamp)
            ).build()

        if (matchLabel.isNotBlank()) {
            // Has next match: icon = avatar, title = "Q18", text = "~8:51P"
            val contentDesc = "Team $teamNumber next match: $matchLabel at $matchTime"
            return ShortTextComplicationData.Builder(
                text = PlainComplicationText.Builder(matchTime).build(),
                contentDescription = PlainComplicationText.Builder(contentDesc).build(),
            )
                .setTitle(PlainComplicationText.Builder(matchLabel).build())
                .setMonochromaticImage(image)
                .setTapAction(tapAction)
                .build()
        }

        // No current event: show upcoming event or just team number
        val upcomingName = prefs.upcomingEventName
        val upcomingDate = prefs.upcomingEventDate

        if (upcomingName.isNotBlank()) {
            val contentDesc = "Team $teamNumber: $upcomingName on $upcomingDate"
            return ShortTextComplicationData.Builder(
                text = PlainComplicationText.Builder(upcomingDate).build(),
                contentDescription = PlainComplicationText.Builder(contentDesc).build(),
            )
                .setTitle(PlainComplicationText.Builder(upcomingName).build())
                .setMonochromaticImage(image)
                .setTapAction(tapAction)
                .build()
        }

        // No upcoming events at all
        val contentDesc = "Team $teamNumber"
        return ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder(teamNumber).build(),
            contentDescription = PlainComplicationText.Builder(contentDesc).build(),
        )
            .setMonochromaticImage(image)
            .setTapAction(tapAction)
            .build()
    }

    private fun buildFallback(tapAction: PendingIntent): ShortTextComplicationData {
        return ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder("TBA").build(),
            contentDescription = PlainComplicationText.Builder("The Blue Alliance — tap to configure").build(),
        )
            .setMonochromaticImage(
                MonochromaticImage.Builder(
                    Icon.createWithResource(this, R.drawable.tba_lamp)
                ).build()
            )
            .setTapAction(tapAction)
            .build()
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
