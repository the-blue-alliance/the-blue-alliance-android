package com.thebluealliance.android.data.repository

import android.util.Log
import com.thebluealliance.android.data.local.TBADatabase
import com.thebluealliance.android.data.local.dao.FavoriteDao
import com.thebluealliance.android.data.local.dao.SubscriptionDao
import com.thebluealliance.android.data.local.entity.FavoriteEntity
import com.thebluealliance.android.data.local.entity.SubscriptionEntity
import com.thebluealliance.android.data.remote.ClientApi
import com.thebluealliance.android.data.remote.dto.BaseResponseDto
import com.thebluealliance.android.data.remote.dto.FavoriteCollectionDto
import com.thebluealliance.android.data.remote.dto.FavoriteDto
import com.thebluealliance.android.data.remote.dto.ModelPreferenceRequestDto
import com.thebluealliance.android.data.remote.dto.SubscriptionCollectionDto
import com.thebluealliance.android.data.remote.dto.SubscriptionDto
import com.thebluealliance.android.domain.model.ModelType
import com.thebluealliance.android.messaging.DeviceRegistrationManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MyTBARepositoryTest {
    private val clientApi: ClientApi = mockk()
    private val db: TBADatabase =
        mockk(relaxed = true) {
            val executor =
                java.util.concurrent.Executors
                    .newSingleThreadExecutor()
            every { queryExecutor } returns executor
            every { transactionExecutor } returns executor
        }
    private val favoriteDao: FavoriteDao = mockk(relaxUnitFun = true)
    private val subscriptionDao: SubscriptionDao = mockk(relaxUnitFun = true)
    private val deviceRegistrationManager: DeviceRegistrationManager =
        mockk {
            every { deviceUuid } returns "test-device-uuid"
        }

    private val repo =
        MyTBARepository(db, clientApi, favoriteDao, subscriptionDao, deviceRegistrationManager)

    @BeforeEach
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
    }

    @AfterEach
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    private suspend fun assertThrowsServerError(
        expectedCode: Int,
        block: suspend () -> Unit,
    ) {
        val thrown = runCatching { block() }.exceptionOrNull()
        assertTrue(thrown is MyTBAServerException, "expected MyTBAServerException, got $thrown")
        assertEquals(expectedCode, (thrown as MyTBAServerException).code)
    }

    @Test
    fun `addFavorite stores locally when server confirms with setPreferences code 0`() =
        runTest {
            coEvery { clientApi.updateModelPreferences(any()) } returns
                BaseResponseDto(code = 0, message = """{"favorite":{"code":200}}""")

            val insertedSlot = slot<List<FavoriteEntity>>()
            coEvery { favoriteDao.insertAll(capture(insertedSlot)) } returns Unit

            repo.addFavorite("frc254", ModelType.TEAM)

            assertEquals("frc254", insertedSlot.captured.single().modelKey)
            assertEquals(ModelType.TEAM, insertedSlot.captured.single().modelType)
        }

    @Test
    fun `addFavorite sends device key with favorite flag set`() =
        runTest {
            val requestSlot = slot<ModelPreferenceRequestDto>()
            coEvery { clientApi.updateModelPreferences(capture(requestSlot)) } returns
                BaseResponseDto(code = 0)
            coEvery { favoriteDao.insertAll(any()) } returns Unit

            repo.addFavorite("frc254", ModelType.TEAM)

            assertEquals("test-device-uuid", requestSlot.captured.deviceKey)
            assertEquals(true, requestSlot.captured.favorite)
        }

    @Test
    fun `addFavorite throws on server error and skips local write`() =
        runTest {
            coEvery { clientApi.updateModelPreferences(any()) } returns
                BaseResponseDto(code = 500, message = "Internal error")

            assertThrowsServerError(500) { repo.addFavorite("frc254", ModelType.TEAM) }

            coVerify(exactly = 0) { favoriteDao.insertAll(any()) }
        }

    @Test
    fun `addFavorite on auth failure attempts refresh and throws without local write`() =
        runTest {
            coEvery { clientApi.updateModelPreferences(any()) } returns
                BaseResponseDto(code = 401, message = "Unauthorized")
            coEvery { clientApi.listFavorites(any()) } returns
                FavoriteCollectionDto(code = 401, message = "Unauthorized")

            assertThrowsServerError(401) { repo.addFavorite("frc254", ModelType.TEAM) }

            coVerify(exactly = 1) { clientApi.listFavorites(any()) }
            coVerify(exactly = 0) { favoriteDao.insertAll(any()) }
        }

    @Test
    fun `removeFavorite deletes locally when server confirms`() =
        runTest {
            coEvery { clientApi.updateModelPreferences(any()) } returns
                BaseResponseDto(code = 0, message = """{"favorite":{"code":200}}""")

            repo.removeFavorite("2026nhgrs", ModelType.EVENT)

            coVerify(exactly = 1) { favoriteDao.delete("2026nhgrs", ModelType.EVENT) }
        }

    @Test
    fun `removeFavorite throws on server error and keeps local row`() =
        runTest {
            coEvery { clientApi.updateModelPreferences(any()) } returns
                BaseResponseDto(code = 500, message = "Internal error")

            assertThrowsServerError(500) { repo.removeFavorite("2026nhgrs", ModelType.EVENT) }

            coVerify(exactly = 0) { favoriteDao.delete(any(), any()) }
        }

    @Test
    fun `updatePreferences refreshes local state on success`() =
        runTest {
            coEvery { clientApi.updateModelPreferences(any()) } returns
                BaseResponseDto(code = 0, message = """{"subscription":{"code":200}}""")
            coEvery { clientApi.listFavorites(any()) } returns
                FavoriteCollectionDto(
                    code = 200,
                    favorites = listOf(FavoriteDto("frc254", ModelType.TEAM)),
                )
            coEvery { clientApi.listSubscriptions(any()) } returns
                SubscriptionCollectionDto(
                    code = 200,
                    subscriptions =
                        listOf(
                            SubscriptionDto("frc254", ModelType.TEAM, listOf("upcoming_match")),
                        ),
                )
            coEvery { favoriteDao.insertAll(any()) } returns Unit
            val subscriptionsSlot = slot<List<SubscriptionEntity>>()
            coEvery { subscriptionDao.insertAll(capture(subscriptionsSlot)) } returns Unit

            repo.updatePreferences("frc254", ModelType.TEAM, true, listOf("upcoming_match"))

            coVerify(exactly = 1) { favoriteDao.deleteAll() }
            coVerify(exactly = 1) { subscriptionDao.deleteAll() }
            assertEquals("upcoming_match", subscriptionsSlot.captured.single().notifications)
        }

    @Test
    fun `updatePreferences throws on server error without refreshing`() =
        runTest {
            coEvery { clientApi.updateModelPreferences(any()) } returns
                BaseResponseDto(code = 500, message = "Internal error")

            assertThrowsServerError(500) {
                repo.updatePreferences("frc254", ModelType.TEAM, true, listOf("upcoming_match"))
            }

            coVerify(exactly = 0) { clientApi.listFavorites(any()) }
            coVerify(exactly = 0) { clientApi.listSubscriptions(any()) }
        }

    @Test
    fun `refreshFavorites replaces local favorites on success`() =
        runTest {
            coEvery { clientApi.listFavorites(any()) } returns
                FavoriteCollectionDto(
                    code = 200,
                    favorites = listOf(FavoriteDto("2026nhgrs", ModelType.EVENT)),
                )
            val insertedSlot = slot<List<FavoriteEntity>>()
            coEvery { favoriteDao.insertAll(capture(insertedSlot)) } returns Unit

            repo.refreshFavorites()

            coVerify(exactly = 1) { favoriteDao.deleteAll() }
            assertEquals("2026nhgrs", insertedSlot.captured.single().modelKey)
        }

    @Test
    fun `refreshFavorites throws on server error without wiping local cache`() =
        runTest {
            coEvery { clientApi.listFavorites(any()) } returns
                FavoriteCollectionDto(code = 500, message = "Internal error")

            assertThrowsServerError(500) { repo.refreshFavorites() }

            coVerify(exactly = 0) { favoriteDao.deleteAll() }
            coVerify(exactly = 0) { favoriteDao.insertAll(any()) }
        }

    @Test
    fun `refreshSubscriptions throws on auth error without wiping local cache`() =
        runTest {
            coEvery { clientApi.listSubscriptions(any()) } returns
                SubscriptionCollectionDto(code = 401, message = "Unauthorized")

            assertThrowsServerError(401) { repo.refreshSubscriptions() }

            coVerify(exactly = 0) { subscriptionDao.deleteAll() }
            coVerify(exactly = 0) { subscriptionDao.insertAll(any()) }
        }
}
