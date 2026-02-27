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
import com.thebluealliance.android.domain.model.Favorite
import com.thebluealliance.android.domain.model.Subscription
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyTBARepository @Inject constructor(
    private val db: TBADatabase,
    private val clientApi: ClientApi,
    private val favoriteDao: FavoriteDao,
    private val subscriptionDao: SubscriptionDao,
    private val deviceRegistrationManager: com.thebluealliance.android.messaging.DeviceRegistrationManager,
) {
    fun observeFavorites(): Flow<List<Favorite>> =
        favoriteDao.observeAll().map { list ->
            list.map { Favorite(modelKey = it.modelKey, modelType = it.modelType) }
        }

    fun isFavorite(modelKey: String, modelType: Int): Flow<Boolean> =
        favoriteDao.isFavorite(modelKey, modelType)

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

    fun observeSubscription(modelKey: String, modelType: Int): Flow<Subscription?> =
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
        Log.d("MyTBARepository", "refreshFavorites: code=${response.code} message=${response.message} favorites=${response.favorites.size}")
        if (response.code == 401) {
            throw MyTBAServerException(response.code, response.message)
        }
        db.withTransaction {
            favoriteDao.deleteAll()
            favoriteDao.insertAll(response.favorites.map {
                FavoriteEntity(modelKey = it.modelKey, modelType = it.modelType)
            })
        }
    }

    suspend fun refreshSubscriptions() {
        val response = clientApi.listSubscriptions()
        if (response.code == 401) {
            throw MyTBAServerException(response.code, response.message)
        }
        db.withTransaction {
            subscriptionDao.deleteAll()
            subscriptionDao.insertAll(response.subscriptions.map {
                SubscriptionEntity(
                    modelKey = it.modelKey,
                    modelType = it.modelType,
                    notifications = it.notifications.joinToString(","),
                )
            })
        }
    }

    suspend fun addFavorite(modelKey: String, modelType: Int) {
        val response = clientApi.updateModelPreferences(
            ModelPreferenceRequestDto(
                modelKey = modelKey,
                modelType = modelType,
                deviceKey = deviceRegistrationManager.deviceUuid,
                favorite = true,
            )
        )
        Log.d("MyTBARepository", "addFavorite: code=${response.code} message=${response.message}")
        if (response.code == 401) {
            try { refreshFavorites() } catch (_: Exception) { }
            throw MyTBAServerException(response.code, response.message)
        }
        favoriteDao.insertAll(listOf(FavoriteEntity(modelKey = modelKey, modelType = modelType)))
    }

    suspend fun removeFavorite(modelKey: String, modelType: Int) {
        val response = clientApi.updateModelPreferences(
            ModelPreferenceRequestDto(
                modelKey = modelKey,
                modelType = modelType,
                deviceKey = deviceRegistrationManager.deviceUuid,
                favorite = false,
            )
        )
        if (response.code == 401) {
            try { refreshFavorites() } catch (_: Exception) { }
            throw MyTBAServerException(response.code, response.message)
        }
        favoriteDao.delete(modelKey, modelType)
    }

    suspend fun updatePreferences(
        modelKey: String,
        modelType: Int,
        favorite: Boolean,
        notifications: List<String>,
    ) {
        val response = clientApi.updateModelPreferences(
            ModelPreferenceRequestDto(
                modelKey = modelKey,
                modelType = modelType,
                deviceKey = deviceRegistrationManager.deviceUuid,
                favorite = favorite,
                notifications = notifications,
            )
        )
        Log.d("MyTBARepository", "updatePreferences: code=${response.code} message=${response.message}")
        if (response.code == 401) {
            throw MyTBAServerException(response.code, response.message)
        }
        // Refresh both to sync local state with server
        try { refreshFavorites() } catch (_: Exception) {}
        try { refreshSubscriptions() } catch (_: Exception) {}
    }

    suspend fun clearLocal() {
        favoriteDao.deleteAll()
        subscriptionDao.deleteAll()
    }
}

class MyTBAServerException(val code: Int, override val message: String) :
    Exception("MyTBA server error $code: $message")
