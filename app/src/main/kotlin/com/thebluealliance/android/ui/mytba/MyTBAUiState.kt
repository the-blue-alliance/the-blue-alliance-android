package com.thebluealliance.android.ui.mytba

import com.thebluealliance.android.domain.model.Favorite
import com.thebluealliance.android.domain.model.Subscription

data class MyTBAUiState(
    val isSignedIn: Boolean = false,
    val userName: String? = null,
    val userEmail: String? = null,
    val userPhotoUrl: String? = null,
    val favorites: List<Favorite> = emptyList(),
    val subscriptions: List<Subscription> = emptyList(),
)
