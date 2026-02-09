package com.thebluealliance.android.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.domain.model.Event
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun EventRow(
    event: Event,
    onClick: () -> Unit,
    showYear: Boolean = false,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        val name = if (showYear) "${event.year} ${event.name}" else event.name
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
        )
        val location = listOfNotNull(event.city, event.state, event.country)
            .joinToString(", ")
        if (location.isNotEmpty()) {
            Text(
                text = location,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        val dateRange = formatEventDateRange(event.startDate, event.endDate)
        if (dateRange != null) {
            Text(
                text = dateRange,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

internal val fullFormat: DateTimeFormatter =
    DateTimeFormatter.ofPattern("EEE, MMM d, yyyy", Locale.US)
internal val noYearFormat: DateTimeFormatter =
    DateTimeFormatter.ofPattern("EEE, MMM d", Locale.US)

internal fun formatEventDateRange(startDate: String?, endDate: String?): String? {
    if (startDate == null) return null
    val start = LocalDate.parse(startDate)
    val end = endDate?.let { LocalDate.parse(it) }
    if (end == null || start == end) return start.format(fullFormat)
    return if (start.year == end.year) {
        "${start.format(noYearFormat)} - ${end.format(fullFormat)}"
    } else {
        "${start.format(fullFormat)} - ${end.format(fullFormat)}"
    }
}
