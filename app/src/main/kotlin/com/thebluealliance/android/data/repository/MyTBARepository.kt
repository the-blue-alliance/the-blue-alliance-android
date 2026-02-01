package com.thebluealliance.android.data.repository

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
        try {
            val response = clientApi.listFavorites()
            favoriteDao.deleteAll()
            favoriteDao.insertAll(response.favorites.map {
                FavoriteEntity(modelKey = it.modelKey, modelType = it.modelType)
            })
        } catch (_: Exception) { }
    }

    suspend fun refreshSubscriptions() {
        try {
            val response = clientApi.listSubscriptions()
            subscriptionDao.deleteAll()
            subscriptionDao.insertAll(response.subscriptions.map {
                SubscriptionEntity(
                    modelKey = it.modelKey,
                    modelType = it.modelType,
                    notifications = it.notifications.joinToString(","),
                )
            })
        } catch (_: Exception) { }
    }

    suspend fun addFavorite(modelKey: String, modelType: Int) {
        favoriteDao.insertAll(listOf(FavoriteEntity(modelKey = modelKey, modelType = modelType)))
        try {
            clientApi.updateModelPreferences(
                ModelPreferenceRequestDto(
                    modelKey = modelKey,
                    modelType = modelType,
                    favorite = true,
                )
            )
        } catch (_: Exception) { }
    }

    suspend fun removeFavorite(modelKey: String, modelType: Int) {
        favoriteDao.delete(modelKey, modelType)
        try {
            clientApi.updateModelPreferences(
                ModelPreferenceRequestDto(
                    modelKey = modelKey,
                    modelType = modelType,
                    favorite = false,
                )
            )
        } catch (_: Exception) { }
    }

    suspend fun clearLocal() {
        favoriteDao.deleteAll()
        subscriptionDao.deleteAll()
    }
}
