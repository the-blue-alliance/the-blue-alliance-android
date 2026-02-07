package com.thebluealliance.android.data.repository

import com.thebluealliance.android.data.local.dao.FavoriteDao
import com.thebluealliance.android.data.local.dao.SubscriptionDao
import com.thebluealliance.android.data.local.entity.FavoriteEntity
import com.thebluealliance.android.data.local.entity.SubscriptionEntity
import com.thebluealliance.android.data.remote.ClientApi
import com.thebluealliance.android.data.remote.dto.ModelPreferenceRequestDto
import com.thebluealliance.android.domain.model.Favorite
import com.thebluealliance.android.domain.model.Subscription
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyTBARepository @Inject constructor(
    private val clientApi: ClientApi,
    private val favoriteDao: FavoriteDao,
    private val subscriptionDao: SubscriptionDao,
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

    suspend fun refreshFavorites() {
        val response = clientApi.listFavorites()
        Log.d("MyTBARepository", "refreshFavorites: code=${response.code} message=${response.message} favorites=${response.favorites.size}")
        if (response.code == 401) {
            throw MyTBAServerException(response.code, response.message)
        }
        favoriteDao.deleteAll()
        favoriteDao.insertAll(response.favorites.map {
            FavoriteEntity(modelKey = it.modelKey, modelType = it.modelType)
        })
    }

    suspend fun refreshSubscriptions() {
        val response = clientApi.listSubscriptions()
        if (response.code == 401) {
            throw MyTBAServerException(response.code, response.message)
        }
        subscriptionDao.deleteAll()
        subscriptionDao.insertAll(response.subscriptions.map {
            SubscriptionEntity(
                modelKey = it.modelKey,
                modelType = it.modelType,
                notifications = it.notifications.joinToString(","),
            )
        })
    }

    suspend fun addFavorite(modelKey: String, modelType: Int) {
        val response = clientApi.updateModelPreferences(
            ModelPreferenceRequestDto(
                modelKey = modelKey,
                modelType = modelType,
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
                favorite = false,
            )
        )
        if (response.code == 401) {
            try { refreshFavorites() } catch (_: Exception) { }
            throw MyTBAServerException(response.code, response.message)
        }
        favoriteDao.delete(modelKey, modelType)
    }

    suspend fun clearLocal() {
        favoriteDao.deleteAll()
        subscriptionDao.deleteAll()
    }
}

class MyTBAServerException(val code: Int, override val message: String) :
    Exception("MyTBA server error $code: $message")
