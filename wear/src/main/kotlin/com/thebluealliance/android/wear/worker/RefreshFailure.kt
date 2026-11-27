package com.thebluealliance.android.wear.worker

import retrofit2.HttpException

/** Why a refresh failed, which decides whether WorkManager should try it again. */
internal enum class RefreshFailure {
    /** The API has no such team. Retrying can't fix it; the user has to change team. */
    TEAM_NOT_FOUND,

    /** Network or server trouble. A later attempt may succeed. */
    TRANSIENT,
    ;

    companion object {
        fun of(error: Throwable): RefreshFailure =
            if (error is HttpException && error.code() == 404) TEAM_NOT_FOUND else TRANSIENT
    }
}
