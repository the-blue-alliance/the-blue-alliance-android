package com.thebluealliance.android.widget

import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class WidgetTeamInputTest {
    private val found: suspend (String) -> Unit = {}

    private fun failsWith(error: Exception): suspend (String) -> Unit = { throw error }

    private fun httpError(code: Int) = HttpException(Response.error<Any>(code, "".toResponseBody()))

    @Test
    fun `normalize keeps a plain team number`() {
        assertEquals("254", WidgetTeamInput.normalize("254"))
    }

    @Test
    fun `normalize strips leading zeros`() {
        assertEquals("254", WidgetTeamInput.normalize("0254"))
    }

    @Test
    fun `normalize rejects zero`() {
        assertNull(WidgetTeamInput.normalize("0"))
        assertNull(WidgetTeamInput.normalize("000"))
    }

    @Test
    fun `normalize rejects numbers longer than five digits`() {
        assertNull(WidgetTeamInput.normalize("123456"))
    }

    @Test
    fun `validate saves the normalized team`() =
        runTest {
            var looked: String? = null
            val result = WidgetTeamInput.validate("0254") { looked = it }
            assertEquals(TeamValidation.Valid("254"), result)
            assertEquals("frc254", looked)
        }

    @Test
    fun `validate rejects zero`() =
        runTest {
            assertEquals(TeamValidation.Invalid, WidgetTeamInput.validate("0", found))
        }

    @Test
    fun `validate reports a team the API does not have`() =
        runTest {
            assertEquals(
                TeamValidation.NotFound("99999"),
                WidgetTeamInput.validate("99999", failsWith(httpError(404))),
            )
        }

    @Test
    fun `validate saves when the lookup can't reach the server`() =
        runTest {
            assertEquals(
                TeamValidation.Valid("254"),
                WidgetTeamInput.validate("254", failsWith(IOException("offline"))),
            )
        }
}
