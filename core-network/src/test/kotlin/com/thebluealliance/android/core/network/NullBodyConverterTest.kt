package com.thebluealliance.android.core.network

import kotlinx.serialization.SerializationException
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.OkHttpClient
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import retrofit2.Call
import retrofit2.http.GET

class NullBodyConverterTest {
    private interface Api {
        @GET("list")
        fun list(): Call<List<String>>

        @GET("map")
        fun map(): Call<Map<String, Int>>

        @GET("text")
        fun text(): Call<String>
    }

    private val server = MockWebServer()

    @BeforeEach
    fun start() = server.start()

    @AfterEach
    fun stop() = server.close()

    private fun api(): Api =
        TbaClientFactory
            .retrofit(server.url("/").toString(), OkHttpClient())
            .create(Api::class.java)

    @Test
    fun `a null list body reads as an empty list`() {
        server.enqueue(MockResponse(code = 200, body = "null\n"))
        assertEquals(emptyList<String>(), api().list().execute().body())
    }

    @Test
    fun `a null map body reads as an empty map`() {
        server.enqueue(MockResponse(code = 200, body = "null"))
        assertEquals(emptyMap<String, Int>(), api().map().execute().body())
    }

    @Test
    fun `a null object body reads as null`() {
        server.enqueue(MockResponse(code = 200, body = "null"))
        assertNull(api().text().execute().body())
    }

    @Test
    fun `a JSON body still parses`() {
        server.enqueue(MockResponse(code = 200, body = "[\"a\",\"b\"]"))
        assertEquals(listOf("a", "b"), api().list().execute().body())
    }

    @Test
    fun `a short malformed body still throws`() {
        server.enqueue(MockResponse(code = 200, body = "nul"))
        assertThrows<SerializationException> { api().list().execute() }
    }
}
