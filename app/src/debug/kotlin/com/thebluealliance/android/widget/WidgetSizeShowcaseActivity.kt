package com.thebluealliance.android.widget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thebluealliance.android.ui.theme.TBATheme

/**
 * Debug-only activity that shows mock widget layouts at all 5 supported sizes.
 *
 * Usage:
 *   adb shell am start -n com.thebluealliance.androidclient.development/com.thebluealliance.android.widget.WidgetSizeShowcaseActivity
 *
 * Optional: pass --es scenario "upcoming" for the upcoming-events state (default: "match")
 */
class WidgetSizeShowcaseActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val scenario = intent?.getStringExtra("scenario") ?: "match"
        setContent {
            TBATheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    WidgetShowcase(scenario)
                }
            }
        }
    }
}

private val Red = Color(0xFFC62828)
private val Blue = Color(0xFF1565C0)
private val LightGray = Color(0xFFF5F5F5)
private val DarkText = Color(0xFF1C1B1F)
private val SubText = Color(0xFF49454F)
private val ScrimBg = Color(0x80000000)

@Composable
private fun WidgetShowcase(scenario: String) {
    val isMatch = scenario != "upcoming"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "Widget Size Showcase",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            if (isMatch) "Scenario: At event with matches" else "Scenario: Upcoming events",
            style = MaterialTheme.typography.bodySmall,
            color = SubText,
        )
        Spacer(Modifier.height(16.dp))

        // 4x2 Full
        SizeLabel("4x2 Full (250x110dp)")
        WidgetFrame(250.dp, 110.dp) {
            if (isMatch) Mock4x2Match() else Mock4x2Upcoming()
        }
        Spacer(Modifier.height(16.dp))

        // 4x1 Compact
        SizeLabel("4x1 Compact (250x60dp)")
        WidgetFrame(250.dp, 60.dp) {
            if (isMatch) Mock4x1Match() else Mock4x1Upcoming()
        }
        Spacer(Modifier.height(16.dp))

        // 2x2 and 2x1 side by side
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SizeLabel("2x2 Square (110x110dp)")
                WidgetFrame(110.dp, 110.dp) {
                    if (isMatch) Mock2x2Match() else Mock2x2Upcoming()
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SizeLabel("1x1 Tiny (60x60dp)")
                WidgetFrame(60.dp, 60.dp) {
                    if (isMatch) Mock1x1Match() else Mock1x1Upcoming()
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        // 2x1 Minimal
        SizeLabel("2x1 Minimal (110x60dp)")
        WidgetFrame(110.dp, 60.dp) {
            if (isMatch) Mock2x1Match() else Mock2x1Upcoming()
        }
        Spacer(Modifier.height(24.dp))

        // Also show the long match label case
        Text(
            "Edge case: R1-M12 @ 12:34 PM",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SizeLabel("1x1")
                WidgetFrame(60.dp, 60.dp) { Mock1x1Long() }
            }
            Spacer(Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SizeLabel("2x1")
                WidgetFrame(110.dp, 60.dp) { Mock2x1Long() }
            }
        }
    }
}

@Composable
private fun SizeLabel(text: String) {
    Text(text, fontSize = 11.sp, color = SubText, modifier = Modifier.padding(bottom = 4.dp))
}

@Composable
private fun WidgetFrame(width: Dp, height: Dp, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(width, height)
            .border(1.dp, Color.Gray.copy(alpha = 0.3f))
            .background(LightGray),
    ) {
        content()
    }
}

// ─── 4x2 Full ───────────────────────────────────────────────────────────────

@Composable
private fun Mock4x2Match() {
    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(24.dp).background(Blue)) // avatar placeholder
            Spacer(Modifier.width(6.dp))
            Column(Modifier.weight(1f)) {
                Text("177 — Bobcat Robotics", fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, color = DarkText)
                Text("5-2-0 at Hartford", fontSize = 9.sp, color = SubText, maxLines = 1)
            }
        }
        Spacer(Modifier.height(3.dp))
        Text("Last match", fontSize = 8.sp, color = SubText)
        MatchRow("Q22", "177, 175, 2067", "558, 1699, 3146", "68", "45", isPlayed = true, winRed = true)
        Spacer(Modifier.height(4.dp))
        Text("Next match", fontSize = 8.sp, color = SubText)
        MatchRow("Q29", "236, 3719, 429", "177, 2170, 571", time = "2:45 PM")
        Spacer(Modifier.weight(1f))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("🏮", fontSize = 8.sp)
            Spacer(Modifier.weight(1f))
            Text("↻ Updated 9:14 AM", fontSize = 8.sp, color = Blue)
        }
    }
}

@Composable
private fun Mock4x2Upcoming() {
    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(24.dp).background(Blue))
            Spacer(Modifier.width(6.dp))
            Column(Modifier.weight(1f)) {
                Text("177 — Bobcat Robotics", fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, color = DarkText)
            }
        }
        Spacer(Modifier.height(3.dp))
        Text("Upcoming events", fontSize = 8.sp, color = SubText)
        Spacer(Modifier.height(2.dp))
        UpcomingRow("Hartford", "Hartford, CT", "Mar 27")
        UpcomingRow("New England", "Boston, MA", "Apr 10")
        Spacer(Modifier.weight(1f))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("🏮", fontSize = 8.sp)
            Spacer(Modifier.weight(1f))
            Text("↻ Updated 9:14 AM", fontSize = 8.sp, color = Blue)
        }
    }
}

// ─── 4x1 Compact ────────────────────────────────────────────────────────────

@Composable
private fun Mock4x1Match() {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("177 — Bobcat Robotics", fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, color = DarkText, modifier = Modifier.weight(1f))
            Text("5-2-0 at Hartford", fontSize = 9.sp, color = SubText)
        }
        Spacer(Modifier.height(2.dp))
        MatchRow("Q29", "236, 3719, 429", "177, 2170, 571", time = "2:45 PM")
    }
}

@Composable
private fun Mock4x1Upcoming() {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("177 — Bobcat Robotics", fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, color = DarkText, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(2.dp))
        Text("Next event: Hartford — Mar 27", fontSize = 10.sp, color = SubText)
    }
}

// ─── 2x2 Square ─────────────────────────────────────────────────────────────

@Composable
private fun Mock2x2Match() {
    Column(modifier = Modifier.fillMaxSize().padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(20.dp).background(Blue))
            Spacer(Modifier.width(4.dp))
            Text("177", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkText)
        }
        Text("5-2-0", fontSize = 9.sp, color = SubText)
        Spacer(Modifier.height(4.dp))
        Text("Next: Q29", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DarkText)
        Text("2:45 PM (est.)", fontSize = 9.sp, color = SubText)
        Spacer(Modifier.weight(1f))
        Text("Hartford", fontSize = 8.sp, color = SubText, maxLines = 1)
    }
}

@Composable
private fun Mock2x2Upcoming() {
    Column(modifier = Modifier.fillMaxSize().padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(20.dp).background(Blue))
            Spacer(Modifier.width(4.dp))
            Text("177", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkText)
        }
        Spacer(Modifier.height(4.dp))
        Text("Hartford", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DarkText)
        Text("Mar 27", fontSize = 9.sp, color = SubText)
    }
}

// ─── 2x1 Minimal ────────────────────────────────────────────────────────────

@Composable
private fun Mock2x1Match() {
    Box(
        modifier = Modifier.fillMaxSize().padding(6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            "177 — Q29 2:45 PM",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText,
            maxLines = 1,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun Mock2x1Upcoming() {
    Box(
        modifier = Modifier.fillMaxSize().padding(6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            "177 — Hartford Mar 27",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText,
            maxLines = 1,
            textAlign = TextAlign.Center,
        )
    }
}

// ─── 1x1 Tiny ───────────────────────────────────────────────────────────────

@Composable
private fun Mock1x1Match() {
    Box(
        modifier = Modifier.fillMaxSize().background(Blue),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(ScrimBg),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("177", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Q29 2:45p", fontSize = 10.sp, color = Color.White, maxLines = 1)
            }
        }
    }
}

@Composable
private fun Mock1x1Upcoming() {
    Box(
        modifier = Modifier.fillMaxSize().background(Blue),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(ScrimBg),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("177", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Mar 27", fontSize = 10.sp, color = Color.White)
            }
        }
    }
}

// ─── Edge case: long match label ────────────────────────────────────────────

@Composable
private fun Mock1x1Long() {
    Box(
        modifier = Modifier.fillMaxSize().background(Blue),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(ScrimBg),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("177", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("R1-M12 12:34p", fontSize = 9.sp, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun Mock2x1Long() {
    Box(
        modifier = Modifier.fillMaxSize().padding(6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            "177 — R1-M12 12:34 PM",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText,
            maxLines = 1,
            textAlign = TextAlign.Center,
        )
    }
}

// ─── Shared mock components ─────────────────────────────────────────────────

@Composable
private fun MatchRow(
    label: String,
    redTeams: String,
    blueTeams: String,
    redScore: String? = null,
    blueScore: String? = null,
    isPlayed: Boolean = false,
    winRed: Boolean = false,
    time: String? = null,
) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, fontSize = 9.sp, fontWeight = FontWeight.Medium, color = DarkText, modifier = Modifier.width(24.dp))
        Column(Modifier.weight(1f)) {
            Text(redTeams, fontSize = 9.sp, color = Red, fontWeight = if (winRed) FontWeight.Bold else FontWeight.Normal)
            Text(blueTeams, fontSize = 9.sp, color = Blue, fontWeight = if (!winRed && isPlayed) FontWeight.Bold else FontWeight.Normal)
        }
        if (isPlayed) {
            Column(horizontalAlignment = Alignment.End) {
                Text(redScore ?: "", fontSize = 9.sp, color = Red, fontWeight = if (winRed) FontWeight.Bold else FontWeight.Normal)
                Text(blueScore ?: "", fontSize = 9.sp, color = Blue, fontWeight = if (!winRed) FontWeight.Bold else FontWeight.Normal)
            }
        } else if (time != null) {
            Text(time, fontSize = 9.sp, color = SubText)
        }
    }
}

@Composable
private fun UpcomingRow(name: String, city: String, date: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 1.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(name, fontSize = 9.sp, fontWeight = FontWeight.Medium, color = DarkText, maxLines = 1)
            Text(city, fontSize = 8.sp, color = SubText, maxLines = 1)
        }
        Text(date, fontSize = 9.sp, color = SubText)
    }
}
