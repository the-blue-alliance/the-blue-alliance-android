package com.thebluealliance.android.ui.events.detail.tabs

import com.thebluealliance.android.domain.model.Award
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class EventAwardsTabLogicTest {
    private fun award(
        type: Int,
        name: String,
        teamKey: String = "",
        awardee: String? = null,
    ) = Award("2024nhgrs", type, teamKey, awardee, name, 2024)

    private val nicknames = mapOf("frc422" to "The Mech Tech Dragons", "frc122" to "NASA Knights")

    @Test
    fun `multi-team award shows its title once with recipients in data order`() {
        val groups =
            groupAwards(
                listOf(
                    award(1, "District Event Winner", "frc122"),
                    award(1, "District Event Winner", "frc422"),
                    award(1, "District Event Winner", "frc2998"),
                    award(2, "District Event Finalist", "frc1"),
                ),
                nicknames,
            )
        assertEquals(
            listOf("District Event Winner", "District Event Finalist"),
            groups.map { it.name },
        )
        assertEquals(listOf("122", "422", "2998"), groups[0].recipients.map { it.teamNumber })
    }

    @Test
    fun `groups keep first-seen order even when rows interleave`() {
        val groups =
            groupAwards(
                listOf(award(1, "A", "frc1"), award(2, "B", "frc2"), award(1, "A", "frc3")),
                emptyMap(),
            )
        assertEquals(listOf("A", "B"), groups.map { it.name })
        assertEquals(listOf("1", "3"), groups[0].recipients.map { it.teamNumber })
    }

    @Test
    fun `same type with different recorded names stays separate`() {
        val groups =
            groupAwards(
                listOf(award(0, "Chairman's Award", "frc1"), award(0, "Impact Award", "frc2")),
                emptyMap(),
            )
        assertEquals(listOf("Chairman's Award", "Impact Award"), groups.map { it.name })
    }

    @Test
    fun `team recipient reads number then nickname`() {
        val r = awardRecipient(award(1, "Winner", "frc422"), nicknames)!!
        assertEquals("422 · The Mech Tech Dragons", r.label)
        assertNull(r.person)
        assertEquals("422", r.teamNumber)
        assertEquals("frc422", r.teamKey)
        assertEquals("Team 422, The Mech Tech Dragons", r.spoken)
    }

    @Test
    fun `team without a loaded nickname shows the number alone`() {
        val r = awardRecipient(award(1, "Winner", "frc2998"), nicknames)!!
        assertEquals("2998", r.label)
        assertNull(r.nickname)
        assertEquals("Team 2998", r.spoken)
    }

    @Test
    fun `individual awardee reads name, team number, then nickname`() {
        val r = awardRecipient(award(4, "FIRST Leadership Award", "frc422", "Sofia S"), nicknames)!!
        assertEquals("Sofia S · 422 · The Mech Tech Dragons", r.label)
        assertEquals("422", r.teamNumber)
        assertEquals("frc422", r.teamKey)
        assertEquals("Sofia S, team 422, The Mech Tech Dragons", r.spoken)
    }

    @Test
    fun `individual awardee whose team has no loaded nickname reads name then number`() {
        val r = awardRecipient(award(4, "FIRST Leadership Award", "frc2998", "Ana B"), nicknames)!!
        assertEquals("Ana B · 2998", r.label)
        assertEquals("Ana B, team 2998", r.spoken)
    }

    @Test
    fun `individual awardee without a team is name only and not a link`() {
        val r = awardRecipient(award(5, "Volunteer of the Year", awardee = "Pat Q"), nicknames)!!
        assertEquals("Pat Q", r.label)
        assertNull(r.teamNumber)
        assertNull(r.teamKey)
        assertEquals("Pat Q", r.spoken)
    }

    @Test
    fun `recipient with neither team nor awardee is dropped`() {
        assertNull(awardRecipient(award(9, "Old Award", awardee = "  "), nicknames))
        val groups = groupAwards(listOf(award(9, "Old Award")), nicknames)
        assertEquals(listOf("Old Award"), groups.map { it.name })
        assertTrue(groups[0].recipients.isEmpty())
    }
}
