package com.thebluealliance.android.widget

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
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
 */
class TeamTrackingWidget : GlanceAppWidget() {

    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                WidgetContent()
            }
        }
    }

    @Composable
    private fun WidgetContent() {
        val prefs = currentState<androidx.datastore.preferences.core.Preferences>()

        val teamNumber = prefs[TeamTrackingWidgetKeys.TEAM_NUMBER] ?: ""
        val teamKey = prefs[TeamTrackingWidgetKeys.TEAM_KEY] ?: ""
        val teamNickname = prefs[TeamTrackingWidgetKeys.TEAM_NICKNAME] ?: ""
        val avatarBase64 = prefs[TeamTrackingWidgetKeys.AVATAR_BASE64]
        val nextAlliance = prefs[TeamTrackingWidgetKeys.NEXT_ALLIANCE] ?: ""
        val eventName = prefs[TeamTrackingWidgetKeys.EVENT_NAME] ?: "No event"
        val record = prefs[TeamTrackingWidgetKeys.RECORD] ?: ""
        val upcomingEvents = prefs[TeamTrackingWidgetKeys.UPCOMING_EVENTS]
        val lastUpdated = prefs[TeamTrackingWidgetKeys.LAST_UPDATED] ?: ""

        // Last match data
        val lastMatchLabel = prefs[TeamTrackingWidgetKeys.LAST_MATCH_LABEL]
        val lastRedTeams = prefs[TeamTrackingWidgetKeys.LAST_MATCH_RED_TEAMS]
        val lastBlueTeams = prefs[TeamTrackingWidgetKeys.LAST_MATCH_BLUE_TEAMS]
        val lastRedScore = prefs[TeamTrackingWidgetKeys.LAST_MATCH_RED_SCORE]
        val lastBlueScore = prefs[TeamTrackingWidgetKeys.LAST_MATCH_BLUE_SCORE]
        val lastWinningAlliance = prefs[TeamTrackingWidgetKeys.LAST_MATCH_WINNING_ALLIANCE]
        val lastRedRp = prefs[TeamTrackingWidgetKeys.LAST_MATCH_RED_RP]
        val lastBlueRp = prefs[TeamTrackingWidgetKeys.LAST_MATCH_BLUE_RP]

        // Next match data
        val nextMatchLabel = prefs[TeamTrackingWidgetKeys.NEXT_MATCH_LABEL]
        val nextRedTeams = prefs[TeamTrackingWidgetKeys.NEXT_MATCH_RED_TEAMS]
        val nextBlueTeams = prefs[TeamTrackingWidgetKeys.NEXT_MATCH_BLUE_TEAMS]
        val nextMatchTime = prefs[TeamTrackingWidgetKeys.NEXT_MATCH_TIME]
        val nextTimeIsEstimate = prefs[TeamTrackingWidgetKeys.NEXT_MATCH_TIME_IS_ESTIMATE] == "true"

        // Decode avatar bitmap
        val avatarBitmap = avatarBase64?.let {
            try {
                val bytes = Base64.decode(it, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } catch (_: Exception) { null }
        }

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
                if (avatarBitmap != null) {
                    val avatarBg = when (nextAlliance) {
                        "red" -> ColorProvider(R.color.widget_red)
                        "blue" -> ColorProvider(R.color.widget_blue)
                        else -> ColorProvider(R.color.widget_blue)
                    }
                    Box(
                        modifier = GlanceModifier.size(36.dp).cornerRadius(4.dp).background(avatarBg),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            provider = ImageProvider(avatarBitmap),
                            contentDescription = "Team avatar",
                            modifier = GlanceModifier.size(32.dp),
                        )
                    }
                    Spacer(modifier = GlanceModifier.width(8.dp))
                }
                Column(modifier = GlanceModifier.defaultWeight().clickable(actionRunCallback<TeamTrackingWidgetOpenAction>())) {
                    val teamLabel = if (teamNumber.isNotEmpty()) {
                        if (teamNickname.isNotEmpty()) "$teamNumber — $teamNickname" else "Team $teamNumber"
                    } else {
                        "Team Tracker"
                    }
                    Text(
                        text = teamLabel,
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurface,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                        maxLines = 1,
                    )
                    // Record + Event name
                    val subtitle = when {
                        record.isNotEmpty() && eventName.isNotEmpty() -> "$record at $eventName"
                        eventName.isNotEmpty() -> eventName
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
                if (lastMatchLabel != null && lastRedTeams != null && lastBlueTeams != null) {
                    MatchSectionLabel("Last match")
                    WidgetMatchRow(
                        label = lastMatchLabel,
                        redTeams = lastRedTeams,
                        blueTeams = lastBlueTeams,
                        redScore = lastRedScore,
                        blueScore = lastBlueScore,
                        winningAlliance = lastWinningAlliance,
                        teamKey = teamKey,
                        time = null,
                        timeIsEstimate = false,
                        redRp = lastRedRp,
                        blueRp = lastBlueRp,
                    )
                }

                // Next Match
                if (nextMatchLabel != null && nextRedTeams != null && nextBlueTeams != null) {
                    Spacer(modifier = GlanceModifier.height(12.dp))
                    MatchSectionLabel("Next match")
                    WidgetMatchRow(
                        label = nextMatchLabel,
                        redTeams = nextRedTeams,
                        blueTeams = nextBlueTeams,
                        redScore = null,
                        blueScore = null,
                        winningAlliance = null,
                        teamKey = teamKey,
                        time = nextMatchTime,
                        timeIsEstimate = nextTimeIsEstimate,
                        redRp = null,
                        blueRp = null,
                    )
                }

                // Upcoming events (when no current event)
                if (upcomingEvents != null && lastMatchLabel == null && nextMatchLabel == null) {
                    MatchSectionLabel("Upcoming events")
                    Spacer(modifier = GlanceModifier.height(2.dp))
                    upcomingEvents.split("\n").forEach { line ->
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
                if (teamNumber.isEmpty()) {
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
                if (lastUpdated.isNotEmpty()) {
                    Text(
                        text = "\u21BB Updated $lastUpdated",
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

    /** Renders RP bonuses as unicode dots: ● for achieved, ○ for not. */
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
                    text = if (achieved) "●" else "○",
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
