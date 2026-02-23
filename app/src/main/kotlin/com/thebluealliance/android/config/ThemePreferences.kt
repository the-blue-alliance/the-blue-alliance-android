package com.thebluealliance.android.config

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.thebluealliance.android.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemePreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    private val themeModeKey = stringPreferencesKey("theme_mode")

    val themeModeFlow: Flow<ThemeMode> = dataStore.data.map { prefs ->
        val name = prefs[themeModeKey]
        ThemeMode.entries.firstOrNull { it.name == name } ?: ThemeMode.AUTO
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { prefs ->
            prefs[themeModeKey] = mode.name
        }
    }
}
