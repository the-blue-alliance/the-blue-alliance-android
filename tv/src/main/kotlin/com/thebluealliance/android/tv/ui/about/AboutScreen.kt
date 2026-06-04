@file:OptIn(ExperimentalTvMaterial3Api::class)

package com.thebluealliance.android.tv.ui.about

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.thebluealliance.android.tv.BuildConfig
import com.thebluealliance.android.tv.R
import com.thebluealliance.android.tv.ui.common.PositionFocusedItemInLazyLayout
import com.thebluealliance.android.tv.ui.common.RetryButton
import com.thebluealliance.android.tv.ui.common.focusOnInitialVisibility
import com.thebluealliance.android.tv.ui.theme.TbaFocusBorderWidth
import com.thebluealliance.android.tv.ui.theme.TbaListBottomPadding
import com.thebluealliance.android.tv.ui.theme.TbaOverscanTopPadding
import com.thebluealliance.android.tv.ui.theme.TbaRowShape

private data class OssLicense(
    val library: String,
    val license: String,
)

// Everything we link against is Apache-2.0 except the OpenJDK-derived desugar lib.
private val OssLicenses =
    listOf(
        OssLicense("Jetpack Compose & AndroidX", "Apache License 2.0 · Google"),
        OssLicense("Compose for TV (androidx.tv)", "Apache License 2.0 · Google"),
        OssLicense("Kotlin & kotlinx.coroutines", "Apache License 2.0 · JetBrains"),
        OssLicense("kotlinx.serialization", "Apache License 2.0 · JetBrains"),
        OssLicense("Retrofit", "Apache License 2.0 · Square"),
        OssLicense("OkHttp", "Apache License 2.0 · Square"),
        OssLicense("Android core-library desugaring", "GPL 2.0 w/ Classpath Exception · Google"),
    )

// This TV app's own public repo. Shown for reference, not as a link: the app never opens a browser
// (TV-WB), so this is a "type it on your laptop" pointer.
private const val PROJECT_GITHUB_URL = "github.com/the-blue-alliance/the-blue-alliance-android"

@Composable
fun AboutScreen(
    onBack: () -> Unit,
    viewModel: AboutViewModel = viewModel(factory = AboutViewModel.Factory),
) {
    // The remote's Back button is the only way out — there's no on-screen back arrow (the JetStream
    // idiom for a secondary screen). Back returns to the feed, not the launcher.
    BackHandler(onBack = onBack)

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // The screen opens at rest: focus parks on the header, which is focusable but draws no highlight,
    // so nothing looks selected and the branding stays at the top (item 0 clamps the list at offset
    // 0, so the pivot can't scroll it away). Pressing DOWN moves focus into the list — the first
    // contributor, or the first license when there are no contributors — and pressing UP past it
    // returns to this resting state. The guard is hoisted here, not on a recycling row, so the
    // one-shot focus request never re-fires when items scroll off and back.
    val initialFocusDone = remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        // Pivot focused rows inward so focus never sits flush against the bottom edge — there's
        // always a peek of the next row, matching the feed.
        PositionFocusedItemInLazyLayout(parentFraction = 0.3f) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                // Content lands on the same 48dp edge as the feed: 32dp here + each row's own 16dp inner
                // padding = 48dp. The focus highlight on the full-width rows bleeds that extra 16dp into
                // the overscan margin (the standard TV settings-list look). The top inset rides as
                // contentPadding (not outer padding) so the list scrolls full-bleed to the screen's top
                // edge instead of clipping at a band; the top margin matches the feed's resting inset.
                contentPadding =
                    PaddingValues(
                        start = 32.dp,
                        top = TbaOverscanTopPadding,
                        end = 32.dp,
                        bottom = TbaListBottomPadding,
                    ),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                // The header (logo + wordmark) rides as the first list item, not a pinned bar: pressing
                // UP from the top row scrolls the whole list back to offset 0, bringing the title fully
                // into view.
                item(key = "header") {
                    AboutHeader(Modifier.focusOnInitialVisibility(initialFocusDone).focusable())
                }

                // The version sits up here, above Thanks, so it's visible without scrolling.
                item(key = "version") {
                    Text(
                        stringResource(R.string.about_version, BuildConfig.VERSION_NAME),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp),
                    )
                }

                item(
                    key = "thanks-header",
                ) { SectionHeader(stringResource(R.string.thanks_header)) }
                // Sits directly under the section header — the same note-under-header shape as the
                // "Open-source licenses" section below.
                item(key = "oss-thanks") {
                    Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Text(
                            stringResource(R.string.thanks_blurb),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            PROJECT_GITHUB_URL,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    }
                }
                when (val s = state) {
                    ThanksUiState.Loading ->
                        item(
                            key = "thanks-loading",
                        ) { Hint(stringResource(R.string.thanks_loading)) }
                    is ThanksUiState.Error ->
                        item(key = "thanks-error") {
                            ErrorRow(stringResource(s.messageRes), viewModel::retry)
                        }
                    is ThanksUiState.Success ->
                        if (s.contributors.isEmpty()) {
                            item(
                                key = "thanks-empty",
                            ) { Hint(stringResource(R.string.thanks_empty)) }
                        } else {
                            itemsIndexed(s.contributors.take(30), key = {
                                _,
                                c,
                                ->
                                c.login
                            }) { _, c ->
                                InfoRow(
                                    c.login,
                                    pluralStringResource(
                                        R.plurals.contribution_count,
                                        c.contributions,
                                        c.contributions,
                                    ),
                                )
                            }
                        }
                }

                item(key = "licenses-header") {
                    SectionHeader(stringResource(R.string.licenses_header))
                }
                item(key = "licenses-note") {
                    Hint(stringResource(R.string.licenses_note))
                }
                itemsIndexed(OssLicenses, key = { _, l -> l.library }) { _, l ->
                    InfoRow(l.library, l.license)
                }
            }
        }
    }
}

/**
 * The page header on a single row: the TBA lamp and wordmark. No back arrow (Back is the remote
 * button) and no description text — the feed already says what the app does, so this screen stays
 * just Thanks + licenses. The 16dp start inset puts the lamp's left edge on the same column as the
 * rows below it (contentPadding 32dp + 16dp = 48dp); the bottom padding sets the header off from
 * the version line that follows.
 */
@Composable
private fun AboutHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(start = 16.dp, bottom = 20.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_tba_lamp),
                contentDescription = null,
                modifier = Modifier.size(26.dp),
            )
        }
        Spacer(Modifier.width(16.dp))
        Text(
            stringResource(R.string.app_name),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp),
    )
}

@Composable
private fun Hint(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    )
}

/**
 * A focusable, non-actionable list row. The onClick is intentionally empty: rows need to take
 * D-pad focus so the list scrolls, but they must not open anything (the app never launches a
 * web browser — TV-WB). Focus shows as a filled highlight + bright border, no scale (a
 * full-width scale would clip at the viewport edges) — the same border language as the feed cards.
 */
@Composable
private fun InfoRow(
    primary: String,
    secondary: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = {},
        modifier = modifier.fillMaxWidth(),
        shape = ClickableSurfaceDefaults.shape(TbaRowShape),
        colors =
            ClickableSurfaceDefaults.colors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContentColor = MaterialTheme.colorScheme.onSurface,
            ),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1f),
        border =
            ClickableSurfaceDefaults.border(
                focusedBorder =
                    Border(
                        border =
                            BorderStroke(
                                TbaFocusBorderWidth,
                                MaterialTheme.colorScheme.secondary,
                            ),
                        shape = TbaRowShape,
                    ),
            ),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                primary,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(16.dp))
            Text(
                secondary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ErrorRow(
    message: String,
    onRetry: () -> Unit,
) {
    Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 3,
        )
        Spacer(Modifier.height(12.dp))
        RetryButton(onRetry = onRetry)
    }
}
