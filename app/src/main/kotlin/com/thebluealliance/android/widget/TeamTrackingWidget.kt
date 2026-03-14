package com.thebluealliance.android.widget

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.runtime.Composable
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
import androidx.glance.appwidget.SizeMode
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
import androidx.glance.layout.height
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
 * - TINY (1x1):     Avatar fills widget, team number + next match overlaid
 * - MINIMAL (2x1):  "177 — Q29 2:45 PM" single line centered
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

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                WidgetContent()
            }
        }
    }

    @Composable
    private fun WidgetContent() {
        val size = LocalSize.current

        when {
            size.width >= 250.dp && size.height >= 110.dp -> FullLayout()
            size.width >= 250.dp -> CompactHorizontalLayout()
            size.width >= 110.dp && size.height >= 110.dp -> CompactSquareLayout()
            size.width >= 110.dp -> MinimalLayout()
            else -> TinyLayout()
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

        /** Short summary of next match for compact layouts. */
        val nextMatchSummary: String?
            get() {
                if (nextMatchLabel == null) return null
                val time = nextMatchTime ?: ""
                val est = if (nextTimeIsEstimate && time.isNotEmpty()) " ~" else ""
                return if (time.isNotEmpty()) {
                    "Next: $nextMatchLabel @ $est$time"
                } else {
                    "Next: $nextMatchLabel"
                }
            }

        /** Very short summary for minimal layout. */
        val nextMatchBrief: String?
            get() {
                if (nextMatchLabel == null) return null
                val time = nextMatchTime ?: ""
                return if (time.isNotEmpty()) "$nextMatchLabel $time" else nextMatchLabel
            }

        /** Ultra-short summary for 1x1 widget: "Q17 2:34p" */
        val nextMatchTiny: String?
            get() {
                if (nextMatchLabel == null) return null
                val time = nextMatchTime ?: ""
                // Abbreviate "AM"/"PM" to "a"/"p" for space
                val shortTime = time
                    .replace(" AM", "a").replace(" PM", "p")
                    .replace(" am", "a").replace(" pm", "p")
                return if (shortTime.isNotEmpty()) "$nextMatchLabel $shortTime" else nextMatchLabel
            }

        /** Last match brief summary for when there's no next match. */
        val lastMatchBrief: String?
            get() {
                if (lastMatchLabel == null || lastRedScore == null || lastBlueScore == null) return null
                return "Last: $lastMatchLabel ${lastRedScore}-${lastBlueScore}"
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
    }

    // ─── TINY layout (1x1) ─────────────────────────────────────────────────────
    // Avatar fills the widget as background; team number and next match time
    // are overlaid on top with a semi-transparent scrim for legibility.

    @Composable
    private fun TinyLayout() {
        val data = readWidgetData()

        val avatarBg = when (data.nextAlliance) {
            "red" -> ColorProvider(R.color.widget_red)
            "blue" -> ColorProvider(R.color.widget_blue)
            else -> ColorProvider(R.color.widget_blue)
        }

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(avatarBg)
                .clickable(actionRunCallback<TeamTrackingWidgetOpenAction>()),
            contentAlignment = Alignment.Center,
        ) {
            // Avatar image fills the box
            if (data.avatarBitmap != null) {
                Image(
                    provider = ImageProvider(data.avatarBitmap!!),
                    contentDescription = "Team avatar",
                    modifier = GlanceModifier.fillMaxSize(),
                )
            }

            // Semi-transparent scrim + text overlay
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(ColorProvider(R.color.widget_tiny_scrim)),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (data.teamNumber.isNotEmpty()) {
                        Text(
                            text = data.teamNumber,
                            style = TextStyle(
                                color = ColorProvider(R.color.widget_tiny_text),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                            ),
                            maxLines = 1,
                        )
                        val matchInfo = data.nextMatchTiny
                        if (matchInfo != null) {
                            Text(
                                text = matchInfo,
                                style = TextStyle(
                                    color = ColorProvider(R.color.widget_tiny_text),
                                    fontSize = 10.sp,
                                ),
                                maxLines = 1,
                            )
                        }
                    } else {
                        Image(
                            provider = ImageProvider(R.drawable.tba_lamp),
                            contentDescription = "The Blue Alliance",
                            modifier = GlanceModifier.size(24.dp),
                            colorFilter = ColorFilter.tint(
                                ColorProvider(R.color.widget_tiny_text)
                            ),
                        )
                    }
                }
            }
        }
    }

    // ─── MINIMAL layout (2x1) ──────────────────────────────────────────────────

    @Composable
    private fun MinimalLayout() {
        val data = readWidgetData()

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.widgetBackground)
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .clickable(actionRunCallback<TeamTrackingWidgetOpenAction>()),
            contentAlignment = Alignment.Center,
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
                // "177 — Q29 2:45 PM" or "177 — 5-2-0"
                val info = data.nextMatchBrief
                    ?: data.lastMatchBrief
                    ?: data.fallbackInfo
                val displayText = if (info.isNotEmpty()) {
                    "${data.teamNumber} \u2014 $info"
                } else {
                    data.teamNumber
                }
                Text(
                    text = displayText,
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    maxLines = 1,
                )
            }
        }
    }

    // ─── COMPACT SQUARE layout (2x2) ───────────────────────────────────────────

    @Composable
    private fun CompactSquareLayout() {
        val data = readWidgetData()

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.widgetBackground)
                .padding(8.dp)
                .clickable(actionRunCallback<TeamTrackingWidgetOpenAction>()),
        ) {
            // Header: avatar + team number
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val avatarBg = when (data.nextAlliance) {
                    "red" -> ColorProvider(R.color.widget_red)
                    "blue" -> ColorProvider(R.color.widget_blue)
                    else -> ColorProvider(R.color.widget_blue)
                }
                if (data.avatarBitmap != null) {
                    Box(
                        modifier = GlanceModifier.size(28.dp).cornerRadius(4.dp).background(avatarBg),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            provider = ImageProvider(data.avatarBitmap!!),
                            contentDescription = "Team avatar",
                            modifier = GlanceModifier.size(24.dp),
                        )
                    }
                    Spacer(modifier = GlanceModifier.width(6.dp))
                }
                Text(
                    text = if (data.teamNumber.isNotEmpty()) data.teamNumber else "TBA",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    maxLines = 1,
                )
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

            Spacer(modifier = GlanceModifier.height(4.dp))

            // Next match compact
            if (data.hasNextMatch) {
                Text(
                    text = "Next match",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurfaceVariant,
                        fontSize = 11.sp,
                    ),
                )
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = data.nextMatchLabel!!,
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurface,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                    if (data.nextMatchTime != null) {
                        Spacer(modifier = GlanceModifier.defaultWeight())
                        val timePrefix = if (data.nextTimeIsEstimate) "~" else ""
                        Text(
                            text = "$timePrefix${data.nextMatchTime}",
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurfaceVariant,
                                fontSize = 13.sp,
                            ),
                        )
                    }
                }
            } else if (data.hasLastMatch) {
                // Show last match result instead
                Text(
                    text = "Last match",
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurfaceVariant,
                        fontSize = 11.sp,
                    ),
                )
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val allianceColor = when (data.lastWinningAlliance) {
                        "red" -> ColorProvider(R.color.widget_red_text)
                        "blue" -> ColorProvider(R.color.widget_blue_text)
                        else -> GlanceTheme.colors.onSurface
                    }
                    Text(
                        text = data.lastMatchLabel!!,
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurface,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                    Spacer(modifier = GlanceModifier.defaultWeight())
                    Text(
                        text = "${data.lastRedScore}-${data.lastBlueScore}",
                        style = TextStyle(
                            color = allianceColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                }
            }

            // Record or event
            Spacer(modifier = GlanceModifier.defaultWeight())
            val subtitle = when {
                data.record.isNotEmpty() -> data.record
                data.eventName.isNotEmpty() && data.eventName != "No event" -> data.eventName
                else -> null
            }
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurfaceVariant,
                        fontSize = 11.sp,
                    ),
                    maxLines = 1,
                )
            }
        }
    }

    // ─── COMPACT HORIZONTAL layout (4x1) ───────────────────────────────────────
    // Two rows: team name + subtitle on top, full next match row below.
    // No avatar — saves vertical space at this short height.

    @Composable
    private fun CompactHorizontalLayout() {
        val data = readWidgetData()

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.widgetBackground)
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .clickable(actionRunCallback<TeamTrackingWidgetOpenAction>()),
        ) {
            // Row 1: Team name + record/event
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = data.teamLabel,
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    maxLines = 1,
                )
                val subtitle = when {
                    data.record.isNotEmpty() && data.eventName.isNotEmpty() -> " \u2022 ${data.record} at ${data.eventName}"
                    data.record.isNotEmpty() -> " \u2022 ${data.record}"
                    data.eventName.isNotEmpty() && data.eventName != "No event" -> " \u2022 ${data.eventName}"
                    else -> ""
                }
                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurfaceVariant,
                            fontSize = 12.sp,
                        ),
                        maxLines = 1,
                    )
                }
            }

            // Row 2: Full next match row (or last match, or fallback)
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
            }
        }
    }

    // ─── FULL layout (4x2) ─────────────────────────────────────────────────────

    @Composable
    private fun FullLayout() {
        val data = readWidgetData()

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.widgetBackground)
                .padding(12.dp),
        ) {
            // Header: Avatar + Team name + Settings gear
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (data.avatarBitmap != null) {
                    val avatarBg = when (data.nextAlliance) {
                        "red" -> ColorProvider(R.color.widget_red)
                        "blue" -> ColorProvider(R.color.widget_blue)
                        else -> ColorProvider(R.color.widget_blue)
                    }
                    Box(
                        modifier = GlanceModifier.size(36.dp).cornerRadius(4.dp).background(avatarBg),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            provider = ImageProvider(data.avatarBitmap!!),
                            contentDescription = "Team avatar",
                            modifier = GlanceModifier.size(32.dp),
                        )
                    }
                    Spacer(modifier = GlanceModifier.width(8.dp))
                }
                Column(modifier = GlanceModifier.defaultWeight().clickable(actionRunCallback<TeamTrackingWidgetOpenAction>())) {
                    Text(
                        text = data.teamLabel,
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurface,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                        maxLines = 1,
                    )
                    // Record + Event name
                    val subtitle = when {
                        data.record.isNotEmpty() && data.eventName.isNotEmpty() -> "${data.record} at ${data.eventName}"
                        data.eventName.isNotEmpty() -> data.eventName
                        else -> ""
                    }
                    if (subtitle.isNotEmpty()) {
                        Text(
                            text = subtitle,
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurfaceVariant,
                                fontSize = 13.sp,
                            ),
                            maxLines = 1,
                        )
                    }
                }
                Box(
                    modifier = GlanceModifier
                        .size(36.dp)
                        .clickable(actionRunCallback<TeamTrackingWidgetSettingsAction>()),
                    contentAlignment = Alignment.TopEnd,
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.ic_settings),
                        contentDescription = "Change team",
                        modifier = GlanceModifier.size(18.dp),
                        colorFilter = ColorFilter.tint(
                            ColorProvider(R.color.widget_settings_icon)
                        ),
                    )
                }
            }

            Spacer(modifier = GlanceModifier.height(6.dp))

            // Match content area — tapping opens TBA
            Column(
                modifier = GlanceModifier.fillMaxWidth().defaultWeight()
                    .clickable(actionRunCallback<TeamTrackingWidgetOpenAction>()),
            ) {
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

                // Next Match
                if (data.hasNextMatch) {
                    Spacer(modifier = GlanceModifier.height(12.dp))
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
                    MatchSectionLabel("Upcoming events")
                    Spacer(modifier = GlanceModifier.height(2.dp))
                    data.upcomingEvents.split("\n").forEach { line ->
                        val parts = line.split("\t")
                        if (parts.size >= 3) {
                            UpcomingEventRow(
                                name = parts[0],
                                city = parts[1],
                                date = parts[2],
                            )
                        }
                    }
                }

                // No data state
                if (data.teamNumber.isEmpty()) {
                    Spacer(modifier = GlanceModifier.defaultWeight())
                    Text(
                        text = "Tap to open TBA",
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurfaceVariant,
                            fontSize = 12.sp,
                        ),
                    )
                }
            }

            // Footer: TBA lamp (left) + refresh icon + updated time (right)
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    provider = ImageProvider(R.drawable.tba_lamp),
                    contentDescription = "The Blue Alliance",
                    modifier = GlanceModifier.size(16.dp),
                    colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurfaceVariant),
                )
                Spacer(modifier = GlanceModifier.defaultWeight())
                if (data.lastUpdated.isNotEmpty()) {
                    Text(
                        text = "\u21BB Updated ${data.lastUpdated}",
                        style = TextStyle(
                            color = GlanceTheme.colors.primary,
                            fontSize = 11.sp,
                        ),
                        modifier = GlanceModifier
                            .padding(top = 8.dp)
                            .clickable(
                                actionRunCallback<TeamTrackingWidgetRefreshAction>()
                            ),
                    )
                }
            }
        }
    }

    // ─── Shared composables ─────────────────────────────────────────────────────

    @Composable
    private fun MatchSectionLabel(label: String) {
        Text(
            text = label,
            style = TextStyle(
                color = GlanceTheme.colors.onSurfaceVariant,
                fontSize = 11.sp,
            ),
        )
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
            modifier = GlanceModifier.fillMaxWidth().padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Match label
            Text(
                text = label,
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                ),
                modifier = GlanceModifier.width(36.dp),
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
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = time,
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurfaceVariant,
                            fontSize = 12.sp,
                        ),
                    )
                    if (timeIsEstimate) {
                        Text(
                            text = "(est.)",
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurfaceVariant,
                                fontSize = 10.sp,
                            ),
                        )
                    }
                }
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
}
