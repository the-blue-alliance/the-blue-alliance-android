package com.thebluealliance.android.ui.more

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.thebluealliance.android.R
import com.thebluealliance.android.domain.model.PlatinumSponsor
import com.thebluealliance.android.domain.model.SponsorLogoStyle
import com.thebluealliance.android.ui.common.QuantityStrings
import com.thebluealliance.android.ui.common.rememberQuantityStrings
import com.thebluealliance.android.ui.components.paddedClickable
import com.thebluealliance.android.util.openUrl

internal fun sponsorCreditLabel(
    count: Int,
    strings: QuantityStrings,
): String = strings.get(R.plurals.sponsor_credit, count)

/** Pick the logo drawn for this background, falling back to the other variant, then to none. */
internal fun PlatinumSponsor.logoFor(darkBackground: Boolean): Any? =
    if (darkBackground) logoOnDark ?: logoOnLight else logoOnLight ?: logoOnDark

@Composable
fun SponsorCredits(
    sponsors: List<PlatinumSponsor>,
    modifier: Modifier = Modifier,
) {
    if (sponsors.isEmpty()) return
    val context = LocalContext.current
    // The app has its own theme setting, so ask the color scheme rather than the system.
    val darkBackground = MaterialTheme.colorScheme.background.luminance() < 0.5f
    // On dark, white logos outshine the grey text, so scale their white down to the label's color.
    val colors = MaterialTheme.colorScheme
    val ink = if (darkBackground) colors.onSurfaceVariant else colors.onSurface
    val logoFilter = if (darkBackground) ColorFilter.tint(ink, BlendMode.Modulate) else null
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Text(
            text = sponsorCreditLabel(sponsors.size, rememberQuantityStrings()),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        FlowRow(
            modifier = Modifier.padding(top = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            itemVerticalAlignment = Alignment.CenterVertically,
        ) {
            sponsors.forEach { sponsor ->
                val logo = sponsor.logoFor(darkBackground)
                val tap =
                    Modifier.paddedClickable(
                        MaterialTheme.shapes.small,
                        vertical = 6.dp,
                        keepContentInPlace = true,
                        onClickLabel = "Open in browser",
                        onClick = { context.openUrl(sponsor.url) },
                    )
                if (logo != null && sponsor.logoStyle == SponsorLogoStyle.Mark) {
                    Row(modifier = tap, verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = logo,
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            colorFilter = logoFilter,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = sponsor.name,
                            style = MaterialTheme.typography.labelLarge,
                            color = ink,
                        )
                    }
                } else if (logo != null) {
                    AsyncImage(
                        model = logo,
                        contentDescription = sponsor.name,
                        contentScale = ContentScale.Fit,
                        colorFilter = logoFilter,
                        modifier = tap.height(20.dp),
                    )
                } else {
                    Text(
                        text = sponsor.name,
                        style = MaterialTheme.typography.labelLarge,
                        color = ink,
                        modifier = tap,
                    )
                }
            }
        }
    }
}
