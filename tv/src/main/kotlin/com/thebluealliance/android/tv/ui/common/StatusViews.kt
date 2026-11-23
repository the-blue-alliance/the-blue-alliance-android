package com.thebluealliance.android.tv.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.thebluealliance.android.tv.R

/**
 * The single retry affordance shared by every load-failure state. Stock Compose for TV button
 * colors, like the webcast picker's buttons: focus is a fill change rather than the card ring.
 */
@Composable
fun RetryButton(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onRetry,
        modifier = modifier,
    ) {
        Text(stringResource(R.string.retry))
    }
}

/**
 * A centred title + message for full-screen empty/error states. [action] is an optional trailing
 * slot (e.g. a [RetryButton]); callers that pass one add their own leading [Spacer] so the empty
 * case carries no dangling space.
 */
@Composable
fun StatusMessage(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    action: @Composable ColumnScope.() -> Unit = {},
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 3,
            textAlign = TextAlign.Center,
        )
        action()
    }
}
