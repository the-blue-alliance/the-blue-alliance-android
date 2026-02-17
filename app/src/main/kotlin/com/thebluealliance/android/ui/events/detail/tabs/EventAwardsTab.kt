package com.thebluealliance.android.ui.events.detail.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.domain.model.Award
import com.thebluealliance.android.ui.common.EmptyBox
import com.thebluealliance.android.ui.common.LoadingBox

@Composable
fun EventAwardsTab(awards: List<Award>?) {
    if (awards == null) {
        LoadingBox()
        return
    }
    if (awards.isEmpty()) {
        EmptyBox("No awards")
        return
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(awards, key = { "${it.eventKey}_${it.awardType}_${it.teamKey}_${it.awardee.orEmpty()}" }) { award ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text(
                    text = award.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                )
                val recipient = buildString {
                    if (award.awardee != null) append(award.awardee)
                    if (award.teamKey.isNotEmpty()) {
                        if (isNotEmpty()) append(" (${award.teamKey.removePrefix("frc")})")
                        else append(award.teamKey.removePrefix("frc"))
                    }
                }
                if (recipient.isNotEmpty()) {
                    Text(
                        text = recipient,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

