package com.thebluealliance.android.ui.events.detail.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.domain.model.Award
import com.thebluealliance.android.domain.model.Team
import com.thebluealliance.android.ui.common.EmptyBox
import com.thebluealliance.android.ui.common.LoadingBox
import com.thebluealliance.android.ui.components.ReadingWidthBox
import com.thebluealliance.android.ui.components.paddedClickable
import com.thebluealliance.android.ui.components.verticalScrollbar
import com.thebluealliance.android.util.teamNumber

/** One award as shown on the Awards tab: its title once, then each recipient in data order. */
internal data class AwardGroup(
    val key: String,
    val name: String,
    val recipients: List<AwardRecipient>,
)

/**
 * One recipient line. [lead] is drawn first and [trail] after a " · "; whichever of them is the
 * team number is link-coloured ([leadIsTeamNumber]). [teamKey] is null when nothing opens.
 */
internal data class AwardRecipient(
    val teamKey: String?,
    val lead: String,
    val trail: String?,
    val leadIsTeamNumber: Boolean,
    val spoken: String,
)

/**
 * Groups the per-recipient [awards] rows by award as recorded (type and name), keeping first-seen
 * order. Recipients with neither a team nor an awardee name are dropped.
 */
internal fun groupAwards(
    awards: List<Award>,
    nicknamesByTeamKey: Map<String, String>,
): List<AwardGroup> =
    awards
        .groupBy { it.awardType to it.name }
        .map { (typeAndName, rows) ->
            AwardGroup(
                key = "${typeAndName.first}_${typeAndName.second}",
                name = typeAndName.second,
                recipients = rows.mapNotNull { awardRecipient(it, nicknamesByTeamKey) },
            )
        }

internal fun awardRecipient(
    award: Award,
    nicknamesByTeamKey: Map<String, String>,
): AwardRecipient? {
    val teamKey = award.teamKey.takeIf { it.isNotBlank() }
    val number = teamKey?.teamNumber
    val awardee = award.awardee?.trim()?.takeIf { it.isNotEmpty() }
    return when {
        awardee != null ->
            AwardRecipient(
                teamKey = teamKey,
                lead = awardee,
                trail = number,
                leadIsTeamNumber = false,
                spoken = if (number != null) "$awardee, team $number" else awardee,
            )
        number != null -> {
            val nickname = nicknamesByTeamKey[teamKey]?.trim()?.takeIf { it.isNotEmpty() }
            AwardRecipient(
                teamKey = teamKey,
                lead = number,
                trail = nickname,
                leadIsTeamNumber = true,
                spoken = if (nickname != null) "Team $number, $nickname" else "Team $number",
            )
        }
        else -> null
    }
}

@Composable
fun EventAwardsTab(
    awards: List<Award>?,
    teams: List<Team>? = null,
    onTeamClick: (String) -> Unit = {},
    isRefreshing: Boolean = false,
    innerPadding: PaddingValues = PaddingValues.Zero,
) {
    if (awards == null) {
        LoadingBox(
            modifier = Modifier.padding(innerPadding),
        )
        return
    }
    if (awards.isEmpty()) {
        // An empty list while the first fetch is still in flight is loading, not empty.
        if (isRefreshing) {
            LoadingBox(modifier = Modifier.padding(innerPadding))
        } else {
            EmptyBox(modifier = Modifier.padding(innerPadding), message = "No awards")
        }
        return
    }
    val groups =
        remember(awards, teams) {
            val nicknames =
                teams
                    ?.mapNotNull { t ->
                        t.nickname?.let { t.key to it }
                    }?.toMap()
                    .orEmpty()
            groupAwards(awards, nicknames)
        }
    ReadingWidthBox(innerPadding, Modifier.fillMaxSize()) { readingPadding ->
        val scrollbarState = rememberLazyListState()
        LazyColumn(
            state = scrollbarState,
            modifier = Modifier.fillMaxSize().verticalScrollbar(scrollbarState, readingPadding),
            contentPadding = readingPadding,
        ) {
            items(groups, key = { it.key }) { group ->
                AwardGroupItem(group, onTeamClick)
            }
        }
    }
}

@Composable
private fun AwardGroupItem(
    group: AwardGroup,
    onTeamClick: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            text = group.name,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        group.recipients.forEach { recipient ->
            AwardRecipientLine(recipient, onTeamClick)
        }
    }
}

@Composable
private fun AwardRecipientLine(
    recipient: AwardRecipient,
    onTeamClick: (String) -> Unit,
) {
    val link = MaterialTheme.colorScheme.primary
    val text =
        buildAnnotatedString {
            if (recipient.leadIsTeamNumber) {
                withStyle(SpanStyle(color = link)) { append(recipient.lead) }
            } else {
                append(recipient.lead)
            }
            if (recipient.trail != null) {
                append(" · ")
                if (recipient.leadIsTeamNumber) {
                    append(recipient.trail)
                } else {
                    withStyle(SpanStyle(color = link)) { append(recipient.trail) }
                }
            }
        }
    val teamKey = recipient.teamKey
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier =
            if (teamKey != null) {
                Modifier.paddedClickable(
                    MaterialTheme.shapes.small,
                    vertical = 4.dp,
                    keepContentInPlace = true,
                    onClickLabel = "View team ${teamKey.teamNumber}",
                ) { onTeamClick(teamKey) }
            } else {
                Modifier.padding(vertical = 4.dp)
            }.semantics { contentDescription = recipient.spoken },
    )
}
