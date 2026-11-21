package com.thebluealliance.android.core.network

import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.Request
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.net.InetAddress

class TbaClientFactoryTest {
    @TempDir
    lateinit var cacheDir: File

    private val tba = MockWebServer()
    private val elsewhere = MockWebServer()

    @BeforeEach
    fun start() {
        // Same machine, different host names: the client must treat them as different hosts.
        tba.start(InetAddress.getByName("localhost"), 0)
        elsewhere.start(InetAddress.getByName("127.0.0.1"), 0)
    }

    @AfterEach
    fun stop() {
        tba.close()
        elsewhere.close()
    }

    private fun client() =
        TbaClientFactory.okHttpClient(
            apiKey = { "secret-key" },
            cacheDir = cacheDir,
            cacheSizeBytes = 1024 * 1024,
            isDebug = false,
        )

    // MockWebServer.url() names both servers "localhost", so spell the hosts out.
    private fun get(path: String) =
        client()
            .newCall(Request.Builder().url("http://localhost:${tba.port}$path").build())
            .execute()
            .use { it.code }

    @Test
    fun `sends the key to the TBA host`() {
        tba.enqueue(MockResponse(code = 200, body = "[]"))
        assertEquals(200, get("/api/v3/status"))
        assertEquals("secret-key", tba.takeRequest().headers["X-TBA-Auth-Key"])
    }

    @Test
    fun `drops the key when a redirect leaves the TBA host`() {
        tba.enqueue(
            MockResponse
                .Builder()
                .code(302)
                .addHeader("Location", "http://127.0.0.1:${elsewhere.port}/steal")
                .build(),
        )
        elsewhere.enqueue(MockResponse(code = 200, body = "[]"))

        assertEquals(200, get("/api/v3/status"))

        assertEquals("secret-key", tba.takeRequest().headers["X-TBA-Auth-Key"])
        assertNull(elsewhere.takeRequest().headers["X-TBA-Auth-Key"])
    }

    @Test
    fun `keeps the key on a same-host redirect`() {
        tba.enqueue(
            MockResponse
                .Builder()
                .code(301)
                .addHeader("Location", "http://localhost:${tba.port}/api/v3/status/")
                .build(),
        )
        tba.enqueue(MockResponse(code = 200, body = "[]"))

        assertEquals(200, get("/api/v3/status"))

        tba.takeRequest()
        assertEquals("secret-key", tba.takeRequest().headers["X-TBA-Auth-Key"])
    }
}
