package com.thebluealliance.android.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.domain.model.NotificationType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationPreferencesSheet(
    displayName: String,
    modelType: Int,
    isFavorite: Boolean,
    currentNotifications: List<String>,
    onSave: (favorite: Boolean, notifications: List<String>) -> Unit,
    onDismiss: () -> Unit,
    teamKey: String? = null,
    trackedTeamKey: String? = null,
    onStartTracking: (() -> Unit)? = null,
    onStopTracking: (() -> Unit)? = null,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val availableTypes = NotificationType.forModelType(modelType)
    val selectedNotifications = remember {
        mutableStateListOf<String>().apply { addAll(currentNotifications) }
    }
    val favoriteState = remember { androidx.compose.runtime.mutableStateOf(isFavorite) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text(
                text = displayName,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Favorite",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f),
                )
                Switch(
                    checked = favoriteState.value,
                    onCheckedChange = { favoriteState.value = it },
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "Notifications",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            if (onStartTracking != null) {
                val isTrackingThisTeam = trackedTeamKey != null && trackedTeamKey == teamKey
                val isTrackingOther = trackedTeamKey != null && trackedTeamKey != teamKey
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Live updates",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        if (isTrackingOther) {
                            val trackedNumber = trackedTeamKey.removePrefix("frc")
                            Text(
                                text = "Currently tracking Team $trackedNumber",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    when {
                        isTrackingThisTeam -> {
                            OutlinedButton(onClick = { onStopTracking?.invoke() }) {
                                Text("Stop")
                            }
                        }
                        isTrackingOther -> {
                            OutlinedButton(onClick = onStartTracking) {
                                Text("Switch")
                            }
                        }
                        else -> {
                            OutlinedButton(onClick = onStartTracking) {
                                Text("Track")
                            }
                        }
                    }
                }
            }

            availableTypes.forEach { type ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = type.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                    )
                    Switch(
                        checked = type.serverKey in selectedNotifications,
                        onCheckedChange = { checked ->
                            if (checked) selectedNotifications.add(type.serverKey)
                            else selectedNotifications.remove(type.serverKey)
                        },
                    )
                }
            }

            TextButton(
                onClick = { onSave(favoriteState.value, selectedNotifications.toList()) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 24.dp),
            ) {
                Text("Save")
            }
        }
    }
}
