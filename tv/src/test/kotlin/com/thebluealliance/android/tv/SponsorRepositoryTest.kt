package com.thebluealliance.android.tv

import com.thebluealliance.android.data.remote.dto.EventDto
import com.thebluealliance.android.data.remote.dto.SponsorDto
import com.thebluealliance.android.data.remote.dto.SponsorsResponseDto
import com.thebluealliance.android.tv.data.api.TbaApi
import com.thebluealliance.android.tv.data.repository.MAX_SPONSORS
import com.thebluealliance.android.tv.data.repository.NetworkSponsorRepository
import com.thebluealliance.android.tv.data.repository.NetworkSponsorRepository.Companion.CACHE_TTL_MILLIS
import com.thebluealliance.android.tv.data.repository.SponsorCache
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.IOException

class SponsorRepositoryTest {
    private val andyMark =
        SponsorsResponseDto(listOf(SponsorDto("AndyMark", "https://www.andymark.com/")))
    private val revRobotics =
        SponsorsResponseDto(listOf(SponsorDto("REV Robotics", "https://www.revrobotics.com/")))
    private val now = 10 * CACHE_TTL_MILLIS

    private class FakeApi(
        private val result: () -> SponsorsResponseDto,
    ) : TbaApi {
        var calls = 0

        override suspend fun getEvents(year: Int): List<EventDto> = error("unused")

        override suspend fun getSponsors(): SponsorsResponseDto {
            calls++
            return result()
        }
    }

    private class FakeCache(
        var entry: Pair<SponsorsResponseDto, Long>? = null,
    ) : SponsorCache {
        override fun read() = entry

        override fun write(
            response: SponsorsResponseDto,
            fetchedAtMillis: Long,
        ) {
            entry = response to fetchedAtMillis
        }
    }

    private fun names(
        api: FakeApi,
        cache: FakeCache,
    ) = runBlocking {
        NetworkSponsorRepository(
            api,
            cache,
            now = { now },
            onRefreshFailed = {},
        ).sponsors().toList().map { list ->
            list.map { it.name }
        }
    }

    @Test
    fun `fresh cache is served without fetching`() {
        val api = FakeApi { revRobotics }
        val cache = FakeCache(andyMark to now - 1)
        assertEquals(listOf(listOf("AndyMark")), names(api, cache))
        assertEquals(0, api.calls)
    }

    @Test
    fun `stale cache shows first, then the refetched list replaces it`() {
        val api = FakeApi { revRobotics }
        val cache = FakeCache(andyMark to now - CACHE_TTL_MILLIS)
        assertEquals(listOf(listOf("AndyMark"), listOf("REV Robotics")), names(api, cache))
        assertEquals(revRobotics to now, cache.entry)
    }

    @Test
    fun `unchanged refetch only bumps the timestamp`() {
        val cache = FakeCache(andyMark to now - CACHE_TTL_MILLIS)
        assertEquals(listOf(listOf("AndyMark")), names(FakeApi { andyMark }, cache))
        assertEquals(andyMark to now, cache.entry)
    }

    @Test
    fun `failed refetch keeps the stale cache`() {
        val cache = FakeCache(andyMark to now - CACHE_TTL_MILLIS)
        assertEquals(
            listOf(listOf("AndyMark")),
            names(FakeApi { throw IOException("offline") }, cache),
        )
        assertEquals(andyMark to now - CACHE_TTL_MILLIS, cache.entry)
    }

    @Test
    fun `empty cache fetches, and a failure shows nothing`() {
        assertEquals(listOf(listOf("REV Robotics")), names(FakeApi { revRobotics }, FakeCache()))
        assertEquals(
            emptyList<List<String>>(),
            names(FakeApi { throw IOException("404") }, FakeCache()),
        )
    }

    @Test
    fun `a clock that went backwards counts as stale`() {
        val api = FakeApi { revRobotics }
        names(api, FakeCache(andyMark to now + 1))
        assertEquals(1, api.calls)
    }

    @Test
    fun `caps how many sponsors are shown`() {
        val many = SponsorsResponseDto((1..20).map { SponsorDto("S$it", "https://x/$it") })
        assertEquals(listOf((1..MAX_SPONSORS).map { "S$it" }), names(FakeApi { many }, FakeCache()))
    }
}
