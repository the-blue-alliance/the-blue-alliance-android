@file:OptIn(ExperimentalGlancePreviewApi::class)

package com.thebluealliance.android.widget

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.PreviewSizeMode
import androidx.glance.appwidget.SizeMode
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextDecoration
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.thebluealliance.android.R

/**
 * Glance Compose widget for team tracking.
 * Keep in sync with widget_preview.xml — colors from @color/widget_*,
 * dimensions from @dimen/widget_*.
 *
 * Uses SizeMode.Responsive to adapt layout based on widget size:
 * - TINY (1x1):     Solid alliance color background, team number + next match time
 * - MINIMAL (2x1):  Two-line layout: team number bold, match info below
 * - SQUARE (2x2):   Avatar + team number header, next match label + time
 * - COMPACT (4x1):  Team name + subtitle, full next match row (no avatar)
 * - FULL (4x2):     Full layout with last match + next match + footer
 */
class TeamTrackingWidget : GlanceAppWidget() {

    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    companion object {
        private val TINY = DpSize(60.dp, 60.dp)       // 1x1
        private val MINIMAL = DpSize(110.dp, 60.dp)   // 2x1
        private val SQUARE = DpSize(110.dp, 110.dp)   // 2x2
        private val COMPACT = DpSize(250.dp, 60.dp)   // 4x1
        private val FULL = DpSize(250.dp, 110.dp)     // 4x2

    }

    override val sizeMode = SizeMode.Responsive(
        setOf(TINY, MINIMAL, SQUARE, COMPACT, FULL)
    )

    override val previewSizeMode: PreviewSizeMode = SizeMode.Responsive(
        setOf(TINY, MINIMAL, SQUARE, COMPACT, FULL)
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                val prefs = currentState<androidx.datastore.preferences.core.Preferences>()
                val forcedSize = prefs[TeamTrackingWidgetKeys.DEBUG_FORCED_SIZE]
                WidgetContent(readWidgetData(), forcedSize)
            }
        }
    }

    override suspend fun providePreview(context: Context, widgetCategory: Int) {
        provideContent {
            GlanceTheme {
                WidgetContent(WidgetData.sampleData())
            }
        }
    }

    @Composable
    private fun WidgetContent(data: WidgetData, forcedSize: String? = null) {
        val size = LocalSize.current

        // Debug override: force a specific size tier regardless of actual widget size
        val tier = when (forcedSize) {
            "4x2" -> "full"
            "4x1" -> "compact"
            "2x2" -> "square"
            "2x1" -> "minimal"
            "1x1" -> "tiny"
            else -> when {
                size.width >= 250.dp && size.height >= 110.dp -> "full"
                size.width >= 250.dp -> "compact"
                size.width >= 110.dp && size.height >= 110.dp -> "square"
                size.width >= 110.dp -> "minimal"
                else -> "tiny"
            }
        }

        when (tier) {
            "full" -> FullLayout(data)
            "compact" -> CompactHorizontalLayout(data)
            "square" -> CompactSquareLayout(data)
            "minimal" -> MinimalLayout(data)
            else -> TinyLayout(data)
        }
    }

    // ─── Data helper ────────────────────────────────────────────────────────────

    /** Reads all widget state from preferences. */
    @Composable
    private fun readWidgetData(): WidgetData {
        val prefs = currentState<androidx.datastore.preferences.core.Preferences>()
        return WidgetData(
            teamNumber = prefs[TeamTrackingWidgetKeys.TEAM_NUMBER] ?: "",
            teamKey = prefs[TeamTrackingWidgetKeys.TEAM_KEY] ?: "",
            teamNickname = prefs[TeamTrackingWidgetKeys.TEAM_NICKNAME] ?: "",
            avatarBase64 = prefs[TeamTrackingWidgetKeys.AVATAR_BASE64],
            nextAlliance = prefs[TeamTrackingWidgetKeys.NEXT_ALLIANCE] ?: "",
            eventName = prefs[TeamTrackingWidgetKeys.EVENT_NAME] ?: "No event",
            record = prefs[TeamTrackingWidgetKeys.RECORD] ?: "",
            upcomingEvents = prefs[TeamTrackingWidgetKeys.UPCOMING_EVENTS],
            lastUpdated = prefs[TeamTrackingWidgetKeys.LAST_UPDATED] ?: "",
            lastMatchLabel = prefs[TeamTrackingWidgetKeys.LAST_MATCH_LABEL],
            lastRedTeams = prefs[TeamTrackingWidgetKeys.LAST_MATCH_RED_TEAMS],
            lastBlueTeams = prefs[TeamTrackingWidgetKeys.LAST_MATCH_BLUE_TEAMS],
            lastRedScore = prefs[TeamTrackingWidgetKeys.LAST_MATCH_RED_SCORE],
            lastBlueScore = prefs[TeamTrackingWidgetKeys.LAST_MATCH_BLUE_SCORE],
            lastWinningAlliance = prefs[TeamTrackingWidgetKeys.LAST_MATCH_WINNING_ALLIANCE],
            lastRedRp = prefs[TeamTrackingWidgetKeys.LAST_MATCH_RED_RP],
            lastBlueRp = prefs[TeamTrackingWidgetKeys.LAST_MATCH_BLUE_RP],
            nextMatchLabel = prefs[TeamTrackingWidgetKeys.NEXT_MATCH_LABEL],
            nextRedTeams = prefs[TeamTrackingWidgetKeys.NEXT_MATCH_RED_TEAMS],
            nextBlueTeams = prefs[TeamTrackingWidgetKeys.NEXT_MATCH_BLUE_TEAMS],
            nextMatchTime = prefs[TeamTrackingWidgetKeys.NEXT_MATCH_TIME],
            nextTimeIsEstimate = prefs[TeamTrackingWidgetKeys.NEXT_MATCH_TIME_IS_ESTIMATE] == "true",
        )
    }

    private data class WidgetData(
        val teamNumber: String,
        val teamKey: String,
        val teamNickname: String,
        val avatarBase64: String?,
        val nextAlliance: String,
        val eventName: String,
        val record: String,
        val upcomingEvents: String?,
        val lastUpdated: String,
        val lastMatchLabel: String?,
        val lastRedTeams: String?,
        val lastBlueTeams: String?,
        val lastRedScore: String?,
        val lastBlueScore: String?,
        val lastWinningAlliance: String?,
        val lastRedRp: String?,
        val lastBlueRp: String?,
        val nextMatchLabel: String?,
        val nextRedTeams: String?,
        val nextBlueTeams: String?,
        val nextMatchTime: String?,
        val nextTimeIsEstimate: Boolean,
    ) {
        val avatarBitmap by lazy {
            avatarBase64?.let {
                try {
                    val bytes = Base64.decode(it, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                } catch (_: Exception) { null }
            }
        }

        val teamLabel: String
            get() = if (teamNumber.isNotEmpty()) {
                if (teamNickname.isNotEmpty()) "$teamNumber \u2014 $teamNickname" else "Team $teamNumber"
            } else {
                "Team Tracker"
            }

        /** Last match brief summary for when there's no next match. */
        val lastMatchBrief: String?
            get() {
                if (lastMatchLabel == null || lastRedScore == null || lastBlueScore == null) return null
                return "Last: $lastMatchLabel ${lastRedScore}-${lastBlueScore}"
            }

        /** Subtitle line: "record at event", "record", "event", or "Upcoming events". */
        val subtitle: String
            get() = when {
                record.isNotEmpty() && eventName.isNotEmpty() -> "$record at $eventName"
                record.isNotEmpty() -> record
                eventName.isNotEmpty() && eventName != "No event" -> eventName
                upcomingEvents != null -> "Upcoming events"
                else -> ""
            }

        /** Fallback text when no match data. */
        val fallbackInfo: String
            get() = when {
                record.isNotEmpty() -> record
                eventName.isNotEmpty() && eventName != "No event" -> eventName
                else -> ""
            }

        val hasNextMatch: Boolean
            get() = nextMatchLabel != null && nextRedTeams != null && nextBlueTeams != null

        val hasLastMatch: Boolean
            get() = lastMatchLabel != null && lastRedTeams != null && lastBlueTeams != null
                    && lastRedScore != null && lastBlueScore != null

        val upcomingEventsList: List<UpcomingEvent>
            get() = upcomingEvents?.split("\n")?.mapNotNull { line ->
                val parts = line.split("\t")
                if (parts.size >= 3) UpcomingEvent(parts[0], parts[1], parts[2]) else null
            } ?: emptyList()

        companion object {
            /** Sample data for widget previews — at-event state with matches. */
            fun sampleData() = WidgetData(
                teamNumber = "177",
                teamKey = "frc177",
                teamNickname = "Bobcat Robotics",
                avatarBase64 = null,
                nextAlliance = "blue",
                eventName = "NE District Event",
                record = "5-2-0",
                upcomingEvents = null,
                lastUpdated = "just now",
                lastMatchLabel = "Q12",
                lastRedTeams = "195, 1519, 4909",
                lastBlueTeams = "177, 1153, 2067",
                lastRedScore = "52",
                lastBlueScore = "71",
                lastWinningAlliance = "blue",
                lastRedRp = "false, false",
                lastBlueRp = "true, true",
                nextMatchLabel = "Q18",
                nextRedTeams = "177, 3467, 5112",
                nextBlueTeams = "1073, 2791, 3958",
                nextMatchTime = "2:30 PM",
                nextTimeIsEstimate = true,
            )

            /** Sample data for widget previews — off-season with upcoming events. */
            fun sampleUpcomingData() = WidgetData(
                teamNumber = "177",
                teamKey = "frc177",
                teamNickname = "Bobcat Robotics",
                avatarBase64 = null,
                nextAlliance = "",
                eventName = "No event",
                record = "",
                upcomingEvents = "NE District WPI\tWorcester, MA\tMar 7\n" +
                        "NE District UNH\tDurham, NH\tMar 28\n" +
                        "NE FIRST Championship\tSpringfield, MA\tApr 16",
                lastUpdated = "just now",
                lastMatchLabel = null,
                lastRedTeams = null,
                lastBlueTeams = null,
                lastRedScore = null,
                lastBlueScore = null,
                lastWinningAlliance = null,
                lastRedRp = null,
                lastBlueRp = null,
                nextMatchLabel = null,
                nextRedTeams = null,
                nextBlueTeams = null,
                nextMatchTime = null,
                nextTimeIsEstimate = false,
            )
        }
    }

    private data class UpcomingEvent(val name: String, val city: String, val date: String)

    // ─── TINY layout (1x1) ─────────────────────────────────────────────────────
    // Solid alliance color background with team number + next match time.
    // No avatar — avatars are unrecognizable at 60dp.

    @Composable
    private fun TinyLayout(data: WidgetData) {

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.widgetBackground)
                .padding(12.dp)
                .clickable(actionRunCallback<TeamTrackingWidgetOpenAction>()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (data.teamNumber.isNotEmpty()) {
                // Team number pinned to top
                Text(
                    text = data.teamNumber,
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    maxLines = 1,
                )
                if (data.nextMatchLabel != null) {
                    // Push match info to vertical center of remaining space
                    Spacer(modifier = GlanceModifier.defaultWeight())
                    Text(
                        text = data.nextMatchLabel,
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurface,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                        maxLines = 1,
                    )
                    if (data.nextMatchTime != null) {
                        Text(
                            text = formatTimeWithEstimate(data.nextMatchTime, data.nextTimeIsEstimate),
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurfaceVariant,
                                fontSize = 10.sp,
                            ),
                            maxLines = 1,
                        )
                    }
                    Spacer(modifier = GlanceModifier.defaultWeight())
                } else if (data.avatarBitmap != null) {
                    // No match data — show avatar filling available space
                    Spacer(modifier = GlanceModifier.defaultWeight())
                    val avatarBg = when (data.nextAlliance) {
                        "red" -> ColorProvider(R.color.widget_red)
                        "blue" -> ColorProvider(R.color.widget_blue)
                        else -> ColorProvider(R.color.widget_blue)
                    }
                    Box(
                        modifier = GlanceModifier.size(40.dp).cornerRadius(4.dp)
                            .background(avatarBg),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            provider = ImageProvider(data.avatarBitmap!!),
                            contentDescription = "Team avatar",
                            modifier = GlanceModifier.size(36.dp),
                        )
                    }
                    Spacer(modifier = GlanceModifier.defaultWeight())
                }
            } else {
                Box(
                    modifier = GlanceModifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.tba_lamp),
                        contentDescription = "The Blue Alliance",
                        modifier = GlanceModifier.size(24.dp),
                        colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurfaceVariant),
                    )
                }
            }
        }
    }

    // ─── MINIMAL layout (2x1) ──────────────────────────────────────────────────
    // Two-line layout: team number (14sp bold) on top, match info (11sp) below.

    @Composable
    private fun MinimalLayout(data: WidgetData) {
        val redColor = ColorProvider(R.color.widget_red_text)
        val blueColor = ColorProvider(R.color.widget_blue_text)

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.widgetBackground)
                .padding(12.dp)
                .clickable(actionRunCallback<TeamTrackingWidgetOpenAction>()),
        ) {
            if (data.teamNumber.isEmpty()) {
                Text(
                    text = "Tap to set up",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurfaceVariant,
                        fontSize = 12.sp,
                    ),
                )
            } else {
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TeamAvatar(data, boxSize = 20.dp, imageSize = 16.dp, cornerRadius = 3.dp)
                    Text(
                        text = data.teamLabel,
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurface,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                        maxLines = 1,
                    )
                }
                if (data.hasNextMatch) {
                    // Vertically center match content in remaining space
                    Spacer(modifier = GlanceModifier.defaultWeight())
                    // Match label + time (right-aligned)
                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = data.nextMatchLabel!!,
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurface,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                            ),
                        )
                        if (data.nextMatchTime != null) {
                            Spacer(modifier = GlanceModifier.defaultWeight())
                            Text(
                                text = formatTimeWithEstimate(data.nextMatchTime, data.nextTimeIsEstimate),
                                style = TextStyle(
                                    color = GlanceTheme.colors.onSurfaceVariant,
                                    fontSize = 12.sp,
                                ),
                            )
                        }
                    }
                    // Alliance team rows in color
                    TeamNumbersRow(data.nextRedTeams!!, data.teamKey.removePrefix("frc"), redColor, false)
                    TeamNumbersRow(data.nextBlueTeams!!, data.teamKey.removePrefix("frc"), blueColor, false)
                    Spacer(modifier = GlanceModifier.defaultWeight())
                } else {
                    Spacer(modifier = GlanceModifier.defaultWeight())
                    val fallback = data.lastMatchBrief
                    if (fallback != null) {
                        Text(
                            text = fallback,
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurfaceVariant,
                                fontSize = 11.sp,
                            ),
                            maxLines = 1,
                        )
                    } else {
                        val firstEvent = data.upcomingEventsList.firstOrNull()
                        if (firstEvent != null) {
                            UpcomingEventRowCompact(firstEvent.name, firstEvent.date)
                        } else if (data.fallbackInfo.isNotEmpty()) {
                            Text(
                                text = data.fallbackInfo,
                                style = TextStyle(
                                    color = GlanceTheme.colors.onSurfaceVariant,
                                    fontSize = 11.sp,
                                ),
                                maxLines = 1,
                            )
                        }
                    }
                    Spacer(modifier = GlanceModifier.defaultWeight())
                }
            }
        }
    }

    // ─── COMPACT SQUARE layout (2x2) ───────────────────────────────────────────

    @Composable
    private fun CompactSquareLayout(data: WidgetData) {
        val redColor = ColorProvider(R.color.widget_red_text)
        val blueColor = ColorProvider(R.color.widget_blue_text)

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.widgetBackground)
                .padding(12.dp)
                .clickable(actionRunCallback<TeamTrackingWidgetOpenAction>()),
        ) {
            // Header: avatar + team label
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TeamAvatar(data, boxSize = 28.dp, imageSize = 24.dp, cornerRadius = 4.dp)
                Column(modifier = GlanceModifier.defaultWeight()) {
                    Text(
                        text = if (data.teamNumber.isNotEmpty()) data.teamLabel else "TBA",
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurface,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                        maxLines = 1,
                    )
                    if (data.teamNumber.isNotEmpty() && data.subtitle.isNotEmpty()) {
                        Text(
                            text = data.subtitle,
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurfaceVariant,
                                fontSize = 11.sp,
                            ),
                            maxLines = 1,
                        )
                    }
                }
            }

            if (data.teamNumber.isEmpty()) {
                Spacer(modifier = GlanceModifier.defaultWeight())
                Text(
                    text = "Tap to set up",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurfaceVariant,
                        fontSize = 12.sp,
                    ),
                )
                return@Column
            }

            // Match section — vertically centered in remaining space
            Spacer(modifier = GlanceModifier.defaultWeight())
            if (data.hasNextMatch) {
                MatchSectionLabel("Next match")
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = data.nextMatchLabel!!,
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurface,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                    )
                    if (data.nextMatchTime != null) {
                        Spacer(modifier = GlanceModifier.defaultWeight())
                        Text(
                            text = formatTimeWithEstimate(
                                data.nextMatchTime,
                                data.nextTimeIsEstimate,
                            ),
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurfaceVariant,
                                fontSize = 12.sp,
                            ),
                        )
                    }
                }
                TeamNumbersRow(data.nextRedTeams!!, data.teamKey.removePrefix("frc"), redColor, false)
                TeamNumbersRow(data.nextBlueTeams!!, data.teamKey.removePrefix("frc"), blueColor, false)
            } else if (data.hasLastMatch) {
                MatchSectionLabel("Last match")
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = data.lastMatchLabel!!,
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurface,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                    )
                    Spacer(modifier = GlanceModifier.defaultWeight())
                    Text(
                        text = "${data.lastRedScore}-${data.lastBlueScore}",
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurface,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                }
            } else if (data.upcomingEvents != null) {
                val events = data.upcomingEventsList.take(2)
                events.firstOrNull()?.let { event ->
                    UpcomingEventRow(event.name, event.city, event.date)
                }
                events.drop(1).forEach { event ->
                    Spacer(modifier = GlanceModifier.defaultWeight())
                    UpcomingEventRow(event.name, event.city, event.date)
                }
            }
            Spacer(modifier = GlanceModifier.defaultWeight())

            UpdatedFooter(data.lastUpdated)
        }
    }

    // ─── COMPACT HORIZONTAL layout (4x1) ───────────────────────────────────────
    // Two rows: team name + subtitle on top, full next match row below.
    // No avatar — saves vertical space at this short height.

    @Composable
    private fun CompactHorizontalLayout(data: WidgetData) {

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.widgetBackground)
                .padding(12.dp)
                .clickable(actionRunCallback<TeamTrackingWidgetOpenAction>()),
        ) {
            WidgetHeader(data)

            if (data.teamNumber.isEmpty()) {
                Spacer(modifier = GlanceModifier.defaultWeight())
                Text(
                    text = "Tap to set up",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurfaceVariant,
                        fontSize = 12.sp,
                    ),
                )
                return@Column
            }

            // Match row — vertically centered in remaining space
            Spacer(modifier = GlanceModifier.defaultWeight())
            if (data.hasNextMatch) {
                WidgetMatchRow(
                    label = data.nextMatchLabel!!,
                    redTeams = data.nextRedTeams!!,
                    blueTeams = data.nextBlueTeams!!,
                    redScore = null,
                    blueScore = null,
                    winningAlliance = null,
                    teamKey = data.teamKey,
                    time = data.nextMatchTime,
                    timeIsEstimate = data.nextTimeIsEstimate,
                    redRp = null,
                    blueRp = null,
                )
            } else if (data.hasLastMatch) {
                WidgetMatchRow(
                    label = data.lastMatchLabel!!,
                    redTeams = data.lastRedTeams!!,
                    blueTeams = data.lastBlueTeams!!,
                    redScore = data.lastRedScore,
                    blueScore = data.lastBlueScore,
                    winningAlliance = data.lastWinningAlliance,
                    teamKey = data.teamKey,
                    time = null,
                    timeIsEstimate = false,
                    redRp = data.lastRedRp,
                    blueRp = data.lastBlueRp,
                )
            } else if (data.upcomingEvents != null) {
                data.upcomingEventsList.take(2).forEach { event ->
                    UpcomingEventRowCompact(event.name, event.date)
                }
            }
            Spacer(modifier = GlanceModifier.defaultWeight())
        }
    }

    // ─── FULL layout (4x2) ─────────────────────────────────────────────────────

    @Composable
    private fun FullLayout(data: WidgetData) {

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.widgetBackground)
                .padding(12.dp)
                .clickable(actionRunCallback<TeamTrackingWidgetOpenAction>()),
        ) {
            WidgetHeader(data)

            // Match content area — vertically centered
            Column(
                modifier = GlanceModifier.fillMaxWidth().defaultWeight(),
            ) {
                Spacer(modifier = GlanceModifier.defaultWeight())
                // Last Match
                if (data.hasLastMatch) {
                    MatchSectionLabel("Last match")
                    WidgetMatchRow(
                        label = data.lastMatchLabel!!,
                        redTeams = data.lastRedTeams!!,
                        blueTeams = data.lastBlueTeams!!,
                        redScore = data.lastRedScore,
                        blueScore = data.lastBlueScore,
                        winningAlliance = data.lastWinningAlliance,
                        teamKey = data.teamKey,
                        time = null,
                        timeIsEstimate = false,
                        redRp = data.lastRedRp,
                        blueRp = data.lastBlueRp,
                    )
                }

                // Even vertical spacing between match rows
                if (data.hasLastMatch && data.hasNextMatch) {
                    Spacer(modifier = GlanceModifier.defaultWeight())
                }

                // Next Match
                if (data.hasNextMatch) {
                    MatchSectionLabel("Next match")
                    WidgetMatchRow(
                        label = data.nextMatchLabel!!,
                        redTeams = data.nextRedTeams!!,
                        blueTeams = data.nextBlueTeams!!,
                        redScore = null,
                        blueScore = null,
                        winningAlliance = null,
                        teamKey = data.teamKey,
                        time = data.nextMatchTime,
                        timeIsEstimate = data.nextTimeIsEstimate,
                        redRp = null,
                        blueRp = null,
                    )
                }

                // Upcoming events (when no current event)
                if (data.upcomingEvents != null && !data.hasLastMatch && !data.hasNextMatch) {
                    val events = data.upcomingEventsList.take(3)
                    events.firstOrNull()?.let { event ->
                        UpcomingEventRow(event.name, event.city, event.date)
                    }
                    events.drop(1).forEach { event ->
                        Spacer(modifier = GlanceModifier.defaultWeight())
                        UpcomingEventRow(event.name, event.city, event.date)
                    }
                }

                // No data state
                if (data.teamNumber.isEmpty()) {
                    Text(
                        text = "Tap to set up",
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurfaceVariant,
                            fontSize = 12.sp,
                        ),
                    )
                }
                Spacer(modifier = GlanceModifier.defaultWeight())
            }

            UpdatedFooter(data.lastUpdated)
        }
    }

    // ─── Shared composables ─────────────────────────────────────────────────────

    /** "↻ Updated ..." footer with refresh-on-tap. Used by 2x2 and 4x2. */
    @Composable
    private fun UpdatedFooter(lastUpdated: String) {
        if (lastUpdated.isNotEmpty()) {
            Text(
                text = "\u21BB Updated $lastUpdated",
                style = TextStyle(
                    color = GlanceTheme.colors.primary,
                    fontSize = 11.sp,
                ),
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .clickable(actionRunCallback<TeamTrackingWidgetRefreshAction>()),
            )
        }
    }

    /** Header row with avatar, team name/subtitle, and settings gear. Used by 4x1 and 4x2. */
    @Composable
    private fun WidgetHeader(data: WidgetData) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TeamAvatar(data, boxSize = 28.dp, imageSize = 24.dp, cornerRadius = 4.dp)
            Column(modifier = GlanceModifier.defaultWeight()) {
                Text(
                    text = data.teamLabel,
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    maxLines = 1,
                )
                if (data.subtitle.isNotEmpty()) {
                    Text(
                        text = data.subtitle,
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurfaceVariant,
                            fontSize = 11.sp,
                        ),
                        maxLines = 1,
                    )
                }
            }
            Box(
                modifier = GlanceModifier
                    .size(28.dp)
                    .clickable(actionRunCallback<TeamTrackingWidgetSettingsAction>()),
                contentAlignment = Alignment.TopEnd,
            ) {
                Image(
                    provider = ImageProvider(R.drawable.ic_settings),
                    contentDescription = "Change team",
                    modifier = GlanceModifier.size(16.dp),
                    colorFilter = ColorFilter.tint(
                        ColorProvider(R.color.widget_settings_icon)
                    ),
                )
            }
        }
    }

    @Composable
    private fun MatchSectionLabel(label: String) {
        Text(
            text = label,
            style = TextStyle(
                color = GlanceTheme.colors.onSurfaceVariant,
                fontSize = 10.sp,
            ),
        )
    }

    /** Renders the team avatar with alliance-colored background and trailing spacer. */
    @Composable
    private fun TeamAvatar(data: WidgetData, boxSize: Dp, imageSize: Dp, cornerRadius: Dp) {
        if (data.avatarBitmap != null) {
            val avatarBg = when (data.nextAlliance) {
                "red" -> ColorProvider(R.color.widget_red)
                "blue" -> ColorProvider(R.color.widget_blue)
                else -> ColorProvider(R.color.widget_blue)
            }
            Box(
                modifier = GlanceModifier.size(boxSize).cornerRadius(cornerRadius)
                    .background(avatarBg),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    provider = ImageProvider(data.avatarBitmap!!),
                    contentDescription = "Team avatar",
                    modifier = GlanceModifier.size(imageSize),
                )
            }
            Spacer(modifier = GlanceModifier.width(if (boxSize <= 20.dp) 4.dp else 6.dp))
        }
    }

    /**
     * A match row that mirrors the app's MatchItem layout:
     * [Label] [Red teams / Blue teams] [Scores or Time]
     */
    @Composable
    private fun WidgetMatchRow(
        label: String,
        redTeams: String,
        blueTeams: String,
        redScore: String?,
        blueScore: String?,
        winningAlliance: String?,
        teamKey: String,
        time: String?,
        timeIsEstimate: Boolean,
        redRp: String?,
        blueRp: String?,
    ) {
        val redColor = ColorProvider(R.color.widget_red_text)
        val blueColor = ColorProvider(R.color.widget_blue_text)
        val rpInactiveColor = ColorProvider(R.color.widget_rp_inactive)

        val teamNumber = teamKey.removePrefix("frc")
        val isPlayed = redScore != null && blueScore != null

        Row(
            modifier = GlanceModifier.fillMaxWidth().padding(vertical = 1.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Match label (widened to 44dp for longer labels like QF1-1)
            Text(
                text = label,
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                ),
                modifier = GlanceModifier.width(44.dp),
            )

            // Team numbers column
            Column(modifier = GlanceModifier.defaultWeight()) {
                TeamNumbersRow(redTeams, teamNumber, redColor, winningAlliance == "red")
                TeamNumbersRow(blueTeams, teamNumber, blueColor, winningAlliance == "blue")
            }

            // RP dots
            if (redRp != null && blueRp != null) {
                Column(
                    modifier = GlanceModifier.padding(end = 4.dp),
                    horizontalAlignment = Alignment.End,
                ) {
                    RpDotsRow(redRp, redColor, rpInactiveColor)
                    RpDotsRow(blueRp, blueColor, rpInactiveColor)
                }
            }

            // Scores or time
            if (isPlayed) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = redScore!!,
                        style = TextStyle(
                            color = redColor,
                            fontSize = 12.sp,
                            fontWeight = if (winningAlliance == "red") FontWeight.Bold else FontWeight.Normal,
                        ),
                    )
                    Text(
                        text = blueScore!!,
                        style = TextStyle(
                            color = blueColor,
                            fontSize = 12.sp,
                            fontWeight = if (winningAlliance == "blue") FontWeight.Bold else FontWeight.Normal,
                        ),
                    )
                }
            } else if (time != null) {
                // Use "~" prefix for estimates instead of "(est.)" on a separate line
                Text(
                    text = formatTimeWithEstimate(time, timeIsEstimate),
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurfaceVariant,
                        fontSize = 12.sp,
                    ),
                )
            }
        }
    }

    @Composable
    private fun TeamNumbersRow(
        teams: String,
        highlightTeam: String,
        color: ColorProvider,
        isWinner: Boolean,
    ) {
        val teamList = teams.split(",").map { it.trim() }
        Row {
            teamList.forEachIndexed { index, team ->
                if (index > 0) {
                    Text(
                        text = ", ",
                        style = TextStyle(color = color, fontSize = 12.sp),
                    )
                }
                val isTracked = team == highlightTeam
                Text(
                    text = team,
                    style = TextStyle(
                        color = color,
                        fontSize = 12.sp,
                        fontWeight = if (isWinner) FontWeight.Bold else FontWeight.Normal,
                        textDecoration = if (isTracked) TextDecoration.Underline else TextDecoration.None,
                    ),
                )
            }
        }
    }

    /** Compact upcoming event row for 2x1 and 4x1: event name + date, no city. */
    @Composable
    private fun UpcomingEventRowCompact(name: String, date: String) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = name,
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                ),
                modifier = GlanceModifier.defaultWeight(),
                maxLines = 1,
            )
            Text(
                text = date,
                style = TextStyle(
                    color = GlanceTheme.colors.onSurfaceVariant,
                    fontSize = 12.sp,
                ),
            )
        }
    }

    @Composable
    private fun UpcomingEventRow(name: String, city: String, date: String) {
        Row(
            modifier = GlanceModifier.fillMaxWidth().padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = GlanceModifier.defaultWeight()) {
                Text(
                    text = name,
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    maxLines = 1,
                )
                if (city.isNotEmpty()) {
                    Text(
                        text = city,
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurfaceVariant,
                            fontSize = 11.sp,
                        ),
                        maxLines = 1,
                    )
                }
            }
            Text(
                text = date,
                style = TextStyle(
                    color = GlanceTheme.colors.onSurfaceVariant,
                    fontSize = 12.sp,
                ),
            )
        }
    }

    /** Renders RP bonuses as unicode dots: filled for achieved, empty for not. */
    @Composable
    private fun RpDotsRow(
        rpData: String,
        achievedColor: ColorProvider,
        inactiveColor: ColorProvider,
    ) {
        val bonuses = rpData.split(",").map { it.trim() == "true" }
        Row {
            bonuses.forEach { achieved ->
                Text(
                    text = if (achieved) "\u25CF" else "\u25CB",
                    style = TextStyle(
                        color = if (achieved) achievedColor else inactiveColor,
                        fontSize = 8.sp,
                    ),
                    modifier = GlanceModifier.padding(horizontal = 1.dp),
                )
            }
        }
    }

    // ─── Android Studio previews ────────────────────────────────────────────────
    // Grouped by size tier. Typical = Pixel-like launcher, Minimum = breakpoint edge.

    // Tiny (1×1)
    @Preview(widthDp = 80, heightDp = 100) @Composable
    fun TinyPreview() { GlanceTheme { WidgetContent(WidgetData.sampleData()) } }
    @Preview(widthDp = 60, heightDp = 60) @Composable
    fun TinyMinimumPreview() { GlanceTheme { WidgetContent(WidgetData.sampleData()) } }
    @Preview(widthDp = 80, heightDp = 100) @Composable
    fun TinyUpcomingPreview() { GlanceTheme { WidgetContent(WidgetData.sampleUpcomingData()) } }
    @Preview(widthDp = 60, heightDp = 60) @Composable
    fun TinyUpcomingMinimumPreview() { GlanceTheme { WidgetContent(WidgetData.sampleUpcomingData()) } }

    // Minimal (2×1)
    @Preview(widthDp = 170, heightDp = 100) @Composable
    fun MinimalPreview() { GlanceTheme { WidgetContent(WidgetData.sampleData()) } }
    @Preview(widthDp = 110, heightDp = 60) @Composable
    fun MinimalMinimumPreview() { GlanceTheme { WidgetContent(WidgetData.sampleData()) } }
    @Preview(widthDp = 170, heightDp = 100) @Composable
    fun MinimalUpcomingPreview() { GlanceTheme { WidgetContent(WidgetData.sampleUpcomingData()) } }
    @Preview(widthDp = 110, heightDp = 60) @Composable
    fun MinimalUpcomingMinimumPreview() { GlanceTheme { WidgetContent(WidgetData.sampleUpcomingData()) } }

    // Square (2×2)
    @Preview(widthDp = 170, heightDp = 210) @Composable
    fun SquarePreview() { GlanceTheme { WidgetContent(WidgetData.sampleData()) } }
    @Preview(widthDp = 110, heightDp = 110) @Composable
    fun SquareMinimumPreview() { GlanceTheme { WidgetContent(WidgetData.sampleData()) } }
    @Preview(widthDp = 170, heightDp = 210) @Composable
    fun SquareUpcomingPreview() { GlanceTheme { WidgetContent(WidgetData.sampleUpcomingData()) } }
    @Preview(widthDp = 110, heightDp = 110) @Composable
    fun SquareUpcomingMinimumPreview() { GlanceTheme { WidgetContent(WidgetData.sampleUpcomingData()) } }

    // Compact (4×1)
    @Preview(widthDp = 350, heightDp = 100) @Composable
    fun CompactPreview() { GlanceTheme { WidgetContent(WidgetData.sampleData()) } }
    @Preview(widthDp = 250, heightDp = 60) @Composable
    fun CompactMinimumPreview() { GlanceTheme { WidgetContent(WidgetData.sampleData()) } }
    @Preview(widthDp = 350, heightDp = 100) @Composable
    fun CompactUpcomingPreview() { GlanceTheme { WidgetContent(WidgetData.sampleUpcomingData()) } }
    @Preview(widthDp = 250, heightDp = 60) @Composable
    fun CompactUpcomingMinimumPreview() { GlanceTheme { WidgetContent(WidgetData.sampleUpcomingData()) } }

    // Full (4×2)
    @Preview(widthDp = 350, heightDp = 210) @Composable
    fun FullPreview() { GlanceTheme { WidgetContent(WidgetData.sampleData()) } }
    @Preview(widthDp = 250, heightDp = 110) @Composable
    fun FullMinimumPreview() { GlanceTheme { WidgetContent(WidgetData.sampleData()) } }
    @Preview(widthDp = 350, heightDp = 210) @Composable
    fun FullUpcomingPreview() { GlanceTheme { WidgetContent(WidgetData.sampleUpcomingData()) } }
    @Preview(widthDp = 250, heightDp = 110) @Composable
    fun FullUpcomingMinimumPreview() { GlanceTheme { WidgetContent(WidgetData.sampleUpcomingData()) } }
}

/** Formats a time string with "~" prefix for estimates. */
private fun formatTimeWithEstimate(time: String, isEstimate: Boolean): String {
    if (time.isEmpty()) return ""
    return if (isEstimate) "~$time" else time
}
