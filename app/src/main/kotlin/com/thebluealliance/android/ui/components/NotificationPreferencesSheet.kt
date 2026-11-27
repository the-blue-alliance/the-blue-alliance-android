package com.thebluealliance.android.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.thebluealliance.android.domain.model.NotificationType
import com.thebluealliance.android.ui.theme.TbaIcons
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/** What the top-bar bell announces: whether any notifications are on for the item. */
internal fun notificationsStateDescription(notificationsOn: Boolean): String =
    if (notificationsOn) "Notifications on" else "Notifications off"

/** The top-bar bell that opens [NotificationPreferencesSheet], filled while [notificationsOn]. */
@Composable
fun notificationPreferencesAction(
    notificationsOn: Boolean,
    onClick: () -> Unit,
): TopBarAction =
    TopBarAction(
        label = "Notifications",
        icon = if (notificationsOn) TbaIcons.Notifications else TbaIcons.NotificationsOutlined,
        onClick = onClick,
        stateDescription = notificationsStateDescription(notificationsOn),
    )

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
    val sheetState =
        rememberBottomSheetState(
            initialValue = SheetValue.Hidden,
            enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded),
        )
    val availableTypes = NotificationType.forModelType(modelType)
    // Saveable so a panel resize or rotation keeps unsaved toggles.
    val selectedNotifications =
        rememberSaveable(saver = StringStateListSaver) { currentNotifications.toMutableStateList() }
    val favoriteState = rememberSaveable { mutableStateOf(isFavorite) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        // The sheet's window drops Esc, so dismiss it here, animating out like Cancel.
        EscapeHandler { scope.hideThen({ sheetState.hide() }, onDismiss) }
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            // Scrolls so Save stays reachable when the sheet is taller than the window.
            Column(
                modifier =
                    Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
            ) {
                Text(
                    text = "Notifications for $displayName",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp).semantics { heading() },
                )

                SwitchRow(
                    text = "Favorite",
                    style = MaterialTheme.typography.bodyLarge,
                    checked = favoriteState.value,
                    onCheckedChange = { favoriteState.value = it },
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Text(
                    text = "Notification types",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp).semantics { heading() },
                )

                availableTypes.forEach { type ->
                    SwitchRow(
                        text = type.displayName,
                        style = MaterialTheme.typography.bodyMedium,
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

            // An explicit Cancel: on gaze input an empty spot to dismiss on is hard to find.
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 24.dp),
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp, Alignment.End),
            ) {
                TextButton(
                    onClick = {
                        scope.hideThen({ sheetState.hide() }, onDismiss)
                    },
                    modifier = Modifier.handCursor(),
                ) {
                    Text("Cancel")
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
                        val favorite = favoriteState.value
                        val notifications = selectedNotifications.toList()
                        // Animate out like Cancel; the caller removes the sheet in onSave.
                        scope.hideThen({ sheetState.hide() }) { onSave(favorite, notifications) }
                    },
                    modifier = Modifier.handCursor(),
                ) {
                    Text("Save")
                }
            }
        }
    }
}

/** Runs [hide], then [then] once it ends, even if the hide animation is interrupted. */
internal fun CoroutineScope.hideThen(
    hide: suspend () -> Unit,
    then: () -> Unit,
): Job = launch { hide() }.also { job -> job.invokeOnCompletion { then() } }

// Not listSaver: it saves an empty list as null, which would restore the server values.
internal val StringStateListSaver =
    Saver<SnapshotStateList<String>, ArrayList<String>>(
        save = { ArrayList(it) },
        restore = { it.toMutableStateList() },
    )

/**
 * Space above and below the 32dp switch inside its row's highlight: the row stays 48dp tall, and
 * the hover band and focus ring get room around the switch instead of running along its edge.
 */
internal val SwitchRowVerticalPadding = 8.dp

private val SwitchRowHighlightPadding = 12.dp

/** A full-width row that toggles [checked]; the label and the switch are one target. */
@Composable
private fun SwitchRow(
    text: String,
    style: TextStyle,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                // The highlight bleeds into the sheet's 16dp margin, so the label and switch keep
                // their place but no longer sit against its edges.
                .paddedToggleable(
                    value = checked,
                    shape = MaterialTheme.shapes.small,
                    horizontal = SwitchRowHighlightPadding,
                    vertical = SwitchRowVerticalPadding,
                    keepContentInPlace = true,
                    onValueChange = onCheckedChange,
                ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = text, style = style, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = null)
    }
}
