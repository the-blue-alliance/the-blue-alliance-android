package com.thebluealliance.android.ui.more

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.BuildConfig

@Composable
fun MoreScreen(
    onNavigateToMyTBA: () -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        MoreItem(icon = Icons.Filled.Star, label = "myTBA", onClick = onNavigateToMyTBA)
        HorizontalDivider()
        MoreItem(icon = Icons.Filled.Settings, label = "Settings", onClick = onNavigateToSettings)
        HorizontalDivider()

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "v${BuildConfig.VERSION_NAME} (${BuildConfig.GIT_HASH})\nBuilt ${BuildConfig.BUILD_TIME}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Composable
private fun MoreItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null)
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge)
    }
}
