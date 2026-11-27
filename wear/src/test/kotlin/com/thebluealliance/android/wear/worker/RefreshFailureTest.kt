package com.thebluealliance.android.wear.worker

import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class RefreshFailureTest {
    private fun httpError(code: Int) = HttpException(Response.error<Any>(code, "".toResponseBody()))

    @Test
    fun `a 404 means the team does not exist`() {
        assertEquals(RefreshFailure.TEAM_NOT_FOUND, RefreshFailure.of(httpError(404)))
    }

    @Test
    fun `a server error is transient`() {
        assertEquals(RefreshFailure.TRANSIENT, RefreshFailure.of(httpError(500)))
    }

    @Test
    fun `a network error is transient`() {
        assertEquals(RefreshFailure.TRANSIENT, RefreshFailure.of(IOException("offline")))
    }
}
