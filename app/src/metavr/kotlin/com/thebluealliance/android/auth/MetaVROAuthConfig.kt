package com.thebluealliance.android.auth

/**
 * Derives the AppAuth wiring from the one configured value, the Google OAuth client id
 * (`tba.oauth.client.id.metavr` in `local.properties` → `BuildConfig.OAUTH_CLIENT_ID`).
 *
 * `app/build.gradle.kts` spells [redirectScheme]'s rule out a second time because AppAuth's
 * `RedirectUriReceiverActivity` intent filter needs the scheme as a manifest literal.
 */
object MetaVROAuthConfig {
    private const val CLIENT_ID_SUFFIX = ".apps.googleusercontent.com"

    /** No client id has been minted for this build, so sign-in can't run. */
    fun isConfigured(clientId: String): Boolean = clientId.isNotBlank()

    /**
     * A Google installed-app client redirects to its own client id reversed, e.g. client
     * `123-abc.apps.googleusercontent.com` → scheme `com.googleusercontent.apps.123-abc`.
     */
    fun redirectScheme(clientId: String): String =
        "com.googleusercontent.apps.${clientId.removeSuffix(CLIENT_ID_SUFFIX)}"

    fun redirectUri(clientId: String): String = "${redirectScheme(clientId)}:/oauth2redirect"
}
