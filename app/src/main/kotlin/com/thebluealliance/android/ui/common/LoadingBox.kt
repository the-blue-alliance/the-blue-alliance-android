package com.thebluealliance.android.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun LoadingBox(
    modifier: Modifier = Modifier,
) {
  Box(
      modifier.fillMaxSize().verticalScroll(rememberScrollState()),
      contentAlignment = Alignment.Center,
  ) {
    CircularProgressIndicator()
  }
}
