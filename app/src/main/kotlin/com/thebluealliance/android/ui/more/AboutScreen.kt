package com.thebluealliance.android.ui.more

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer

@Composable
fun AboutScreen() {
    @Suppress("DEPRECATION")
    LibrariesContainer(modifier = Modifier.fillMaxSize())
}
