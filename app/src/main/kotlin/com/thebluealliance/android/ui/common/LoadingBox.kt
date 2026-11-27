package com.thebluealliance.android.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.thebluealliance.android.ui.components.messageBackground

@Composable
fun LoadingBox(modifier: Modifier = Modifier) {
    Box(
        // The fill sits outside the caller's padding, so it also covers the strip behind the gesture bar.
        Modifier
            .fillMaxSize()
            .messageBackground()
            .then(modifier)
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}
