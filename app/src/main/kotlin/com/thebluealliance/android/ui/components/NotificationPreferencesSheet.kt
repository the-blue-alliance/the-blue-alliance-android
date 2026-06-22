package com.thebluealliance.android.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
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
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val availableTypes = NotificationType.forModelType(modelType)
    val selectedNotifications =
        remember {
            mutableStateListOf<String>().apply { addAll(currentNotifications) }
        }
    val favoriteState = remember { androidx.compose.runtime.mutableStateOf(isFavorite) }
    val context = LocalContext.current
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text(
                text = "Notifications for $displayName",
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
                text = "Notification types",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp),
            )

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
                            if (checked) {
                                selectedNotifications.add(type.serverKey)
                            } else {
                                selectedNotifications.remove(type.serverKey)
                            }
                        },
                    )
                }
            }

            TextButton(
                onClick = {
                    // Ask for POST_NOTIFICATIONS at the moment the user enables
                    // notification-bearing preferences — the sign-in prompt is the only
                    // other ask, and pushes are silently dropped without the grant.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                        selectedNotifications.isNotEmpty() &&
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS,
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    onSave(favoriteState.value, selectedNotifications.toList())
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 24.dp),
            ) {
                Text("Save")
            }
        }
    }
}
