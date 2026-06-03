@file:OptIn(ExperimentalTvMaterial3Api::class)

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
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.thebluealliance.android.tv.R

/**
 * The single retry affordance shared by every load-failure state. Colours come from the theme so
 * focus reads the same here as on the feed cards (primary fill → secondary on focus).
 */
@Composable
fun RetryButton(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onRetry,
        modifier = modifier,
        colors =
            ButtonDefaults.colors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onSurface,
                focusedContainerColor = MaterialTheme.colorScheme.secondary,
                focusedContentColor = MaterialTheme.colorScheme.onSurface,
            ),
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
