package com.thebluealliance.android.widget

import androidx.datastore.preferences.core.mutablePreferencesOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Locale

class WidgetRefreshStateTest {
    private val zone = ZoneId.of("America/New_York")
    private val now = ZonedDateTime.of(2027, 3, 19, 10, 0, 0, 0, zone) // a Friday
    private val todayAt0753 =
        ZonedDateTime
            .of(
                2027,
                3,
                19,
                7,
                53,
                0,
                0,
                zone,
            ).toInstant()
            .toEpochMilli()
    private val tuesdayAt0753 =
        ZonedDateTime
            .of(
                2027,
                3,
                16,
                7,
                53,
                0,
                0,
                zone,
            ).toInstant()
            .toEpochMilli()

    private fun atEvent() =
        mutablePreferencesOf().apply {
            this[TeamTrackingWidgetKeys.TEAM_KEY] = "frc177"
            this[TeamTrackingWidgetKeys.EVENT_KEY] = "2027ctwat"
            this[TeamTrackingWidgetKeys.EVENT_NAME] = "Waterbury"
            this[TeamTrackingWidgetKeys.RECORD] = "5-2-0"
            this[TeamTrackingWidgetKeys.LAST_MATCH_LABEL] = "Q12"
            this[TeamTrackingWidgetKeys.NEXT_MATCH_LABEL] = "Q18"
            this[TeamTrackingWidgetKeys.WIDGET_STATE] = WidgetState.CURRENT_EVENT.value
            this[TeamTrackingWidgetKeys.LAST_SUCCESS_AT] = todayAt0753
        }

    private fun footer(
        lastSuccessAtMillis: Long?,
        refreshFailed: Boolean,
        use24Hour: Boolean = false,
    ) = WidgetRefreshState.footerText(lastSuccessAtMillis, refreshFailed, now, use24Hour, Locale.US)

    @Test
    fun `a failed refresh keeps the last good data`() {
        val prefs = atEvent()
        WidgetRefreshState.markFailed(prefs)
        assertEquals("Q12", prefs[TeamTrackingWidgetKeys.LAST_MATCH_LABEL])
        assertEquals("Q18", prefs[TeamTrackingWidgetKeys.NEXT_MATCH_LABEL])
        assertEquals("5-2-0", prefs[TeamTrackingWidgetKeys.RECORD])
        assertEquals(WidgetState.CURRENT_EVENT.value, prefs[TeamTrackingWidgetKeys.WIDGET_STATE])
    }

    @Test
    fun `a failed refresh keeps the last successful update time and flags the failure`() {
        val prefs = atEvent()
        WidgetRefreshState.markFailed(prefs)
        assertEquals(todayAt0753, prefs[TeamTrackingWidgetKeys.LAST_SUCCESS_AT])
        assertEquals(true, prefs[TeamTrackingWidgetKeys.REFRESH_FAILED])
    }

    @Test
    fun `a failed refresh with nothing loaded yet shows the load-failed state`() {
        val prefs =
            mutablePreferencesOf().apply {
                this[TeamTrackingWidgetKeys.TEAM_KEY] = "frc177"
                this[TeamTrackingWidgetKeys.WIDGET_STATE] = WidgetState.NO_UPCOMING.value
            }
        WidgetRefreshState.markFailed(prefs)
        assertEquals(WidgetState.LOAD_FAILED.value, prefs[TeamTrackingWidgetKeys.WIDGET_STATE])
    }

    @Test
    fun `a successful refresh stamps the time and clears the failure`() {
        val prefs = atEvent().apply { this[TeamTrackingWidgetKeys.REFRESH_FAILED] = true }
        val later = todayAt0753 + 60_000
        WidgetRefreshState.markSucceeded(prefs, later)
        assertEquals(later, prefs[TeamTrackingWidgetKeys.LAST_SUCCESS_AT])
        assertEquals(false, prefs[TeamTrackingWidgetKeys.REFRESH_FAILED] ?: false)
    }

    @Test
    fun `switching teams drops the previous team's data`() {
        val prefs = atEvent()
        WidgetRefreshState.setTeam(prefs, "254")
        assertEquals("frc254", prefs[TeamTrackingWidgetKeys.TEAM_KEY])
        assertNull(prefs[TeamTrackingWidgetKeys.NEXT_MATCH_LABEL])
        assertNull(prefs[TeamTrackingWidgetKeys.LAST_SUCCESS_AT])
        WidgetRefreshState.markFailed(prefs)
        assertEquals(WidgetState.LOAD_FAILED.value, prefs[TeamTrackingWidgetKeys.WIDGET_STATE])
    }

    @Test
    fun `saving the same team keeps its data`() {
        val prefs = atEvent()
        WidgetRefreshState.setTeam(prefs, "177")
        assertEquals("Q18", prefs[TeamTrackingWidgetKeys.NEXT_MATCH_LABEL])
        assertEquals(todayAt0753, prefs[TeamTrackingWidgetKeys.LAST_SUCCESS_AT])
    }

    @Test
    fun `footer shows today's update time`() {
        assertEquals("Updated 7:53 AM", footer(todayAt0753, refreshFailed = false))
        assertEquals("Updated 07:53", footer(todayAt0753, refreshFailed = false, use24Hour = true))
    }

    @Test
    fun `footer shows the day for an older update`() {
        assertEquals("Updated Tue 7:53 AM", footer(tuesdayAt0753, refreshFailed = false))
    }

    @Test
    fun `footer says the latest refresh failed`() {
        assertEquals(
            "Couldn't refresh · updated 7:53 AM",
            footer(todayAt0753, refreshFailed = true),
        )
    }

    @Test
    fun `footer offers a retry when nothing has loaded`() {
        assertEquals("Couldn't load · tap to retry", footer(null, refreshFailed = true))
    }

    @Test
    fun `footer is hidden before the first refresh finishes`() {
        assertNull(footer(null, refreshFailed = false))
    }
}
