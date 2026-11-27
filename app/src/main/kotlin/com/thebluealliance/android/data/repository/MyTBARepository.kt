package com.thebluealliance.android.data.repository

import android.util.Log
import androidx.room.withTransaction
import com.thebluealliance.android.data.local.TBADatabase
import com.thebluealliance.android.data.local.dao.FavoriteDao
import com.thebluealliance.android.data.local.dao.SubscriptionDao
import com.thebluealliance.android.data.local.entity.FavoriteEntity
import com.thebluealliance.android.data.local.entity.SubscriptionEntity
import com.thebluealliance.android.data.remote.ClientApi
import com.thebluealliance.android.data.remote.dto.ModelPreferenceRequestDto
import com.thebluealliance.android.data.remote.dto.SubscriptionDto
import com.thebluealliance.android.domain.model.Favorite
import com.thebluealliance.android.domain.model.Subscription
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.net.HttpURLConnection
import javax.inject.Inject
import javax.inject.Singleton

private val HTTP_SUCCESS = 200..299

// The setPreferences endpoint reports success as code 0 with per-model results
// nested in the message JSON; the list endpoints use HTTP-style body codes
// (see backend src/backend/api/handlers/client_api.py).
private const val SET_PREFERENCES_SUCCESS = 0

@Singleton
class MyTBARepository
    @Inject
    constructor(
        private val db: TBADatabase,
        private val clientApi: ClientApi,
        private val favoriteDao: FavoriteDao,
        private val subscriptionDao: SubscriptionDao,
        private val deviceRegistrationManager:
            com.thebluealliance.android.messaging.DeviceRegistrationManager,
    ) {
        fun observeFavorites(): Flow<List<Favorite>> =
            favoriteDao.observeAll().map { list ->
                list.map { Favorite(modelKey = it.modelKey, modelType = it.modelType) }
            }

        fun isFavorite(
            modelKey: String,
            modelType: Int,
        ): Flow<Boolean> = favoriteDao.isFavorite(modelKey, modelType)

        fun observeSubscriptions(): Flow<List<Subscription>> =
            subscriptionDao.observeAll().map { list ->
                list.map {
                    Subscription(
                        modelKey = it.modelKey,
                        modelType = it.modelType,
                        notifications = it.notifications.split(",").filter { n -> n.isNotEmpty() },
                    )
                }
            }

        fun observeSubscription(
            modelKey: String,
            modelType: Int,
        ): Flow<Subscription?> =
            subscriptionDao.observe(modelKey, modelType).map { entity ->
                entity?.let {
                    Subscription(
                        modelKey = it.modelKey,
                        modelType = it.modelType,
                        notifications = it.notifications.split(",").filter { n -> n.isNotEmpty() },
                    )
                }
            }

        suspend fun refreshFavorites() {
            val response = clientApi.listFavorites()
            Log.d(
                "MyTBARepository",
                "refreshFavorites: code=${response.code} message=${response.message} favorites=${response.favorites.size}",
            )
            ensureListSuccess(response.code, response.message)
            db.withTransaction {
                favoriteDao.deleteAll()
                favoriteDao.insertAll(
                    response.favorites.map {
                        FavoriteEntity(modelKey = it.modelKey, modelType = it.modelType)
                    },
                )
            }
        }

        suspend fun refreshSubscriptions() {
            storeSubscriptions(fetchSubscriptions())
        }

        suspend fun addFavorite(
            modelKey: String,
            modelType: Int,
        ) {
            setFavorite(modelKey, modelType, favorite = true)
            favoriteDao.insertAll(
                listOf(FavoriteEntity(modelKey = modelKey, modelType = modelType)),
            )
        }

        suspend fun removeFavorite(
            modelKey: String,
            modelType: Int,
        ) {
            setFavorite(modelKey, modelType, favorite = false)
            favoriteDao.delete(modelKey, modelType)
        }

        // setPreferences replaces both the favorite and the subscription, and an empty
        // notification list deletes the subscription, so send the server's current types.
        private suspend fun setFavorite(
            modelKey: String,
            modelType: Int,
            favorite: Boolean,
        ) {
            val subscriptions = fetchSubscriptions()
            val notifications =
                subscriptions
                    .firstOrNull { it.modelKey == modelKey && it.modelType == modelType }
                    ?.notifications
                    .orEmpty()
            val response =
                clientApi.updateModelPreferences(
                    ModelPreferenceRequestDto(
                        modelKey = modelKey,
                        modelType = modelType,
                        deviceKey = deviceRegistrationManager.deviceUuid,
                        favorite = favorite,
                        notifications = notifications,
                    ),
                )
            Log.d(
                "MyTBARepository",
                "setFavorite($favorite): code=${response.code} message=${response.message}",
            )
            if (response.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
                try {
                    refreshFavorites()
                } catch (_: Exception) {
                }
                throw MyTBAServerException(response.code, response.message)
            }
            ensureUpdateSuccess(response.code, response.message)
            storeSubscriptions(subscriptions)
        }

        private suspend fun fetchSubscriptions(): List<SubscriptionDto> {
            val response = clientApi.listSubscriptions()
            ensureListSuccess(response.code, response.message)
            return response.subscriptions
        }

        private suspend fun storeSubscriptions(subscriptions: List<SubscriptionDto>) {
            db.withTransaction {
                subscriptionDao.deleteAll()
                subscriptionDao.insertAll(
                    subscriptions.map {
                        SubscriptionEntity(
                            modelKey = it.modelKey,
                            modelType = it.modelType,
                            notifications = it.notifications.joinToString(","),
                        )
                    },
                )
            }
        }

        suspend fun updatePreferences(
            modelKey: String,
            modelType: Int,
            favorite: Boolean,
            notifications: List<String>,
        ) {
            val response =
                clientApi.updateModelPreferences(
                    ModelPreferenceRequestDto(
                        modelKey = modelKey,
                        modelType = modelType,
                        deviceKey = deviceRegistrationManager.deviceUuid,
                        favorite = favorite,
                        notifications = notifications,
                    ),
                )
            Log.d(
                "MyTBARepository",
                "updatePreferences: code=${response.code} message=${response.message}",
            )
            ensureUpdateSuccess(response.code, response.message)
            // Refresh both to sync local state with server
            try {
                refreshFavorites()
            } catch (_: Exception) {
            }
            try {
                refreshSubscriptions()
            } catch (_: Exception) {
            }
        }

        suspend fun clearLocal() {
            favoriteDao.deleteAll()
            subscriptionDao.deleteAll()
        }

        private fun ensureListSuccess(
            code: Int,
            message: String,
        ) {
            if (code !in HTTP_SUCCESS) {
                throw MyTBAServerException(code, message)
            }
        }

        private fun ensureUpdateSuccess(
            code: Int,
            message: String,
        ) {
            if (code != SET_PREFERENCES_SUCCESS && code !in HTTP_SUCCESS) {
                throw MyTBAServerException(code, message)
            }
        }
    }

class MyTBAServerException(
    val code: Int,
    override val message: String,
) : Exception("MyTBA server error $code: $message")
