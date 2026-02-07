package com.thebluealliance.android.data.remote

import com.thebluealliance.android.data.remote.dto.BaseResponseDto
import com.thebluealliance.android.data.remote.dto.FavoriteCollectionDto
import com.thebluealliance.android.data.remote.dto.ModelPreferenceRequestDto
import com.thebluealliance.android.data.remote.dto.RegisterDeviceRequestDto
import com.thebluealliance.android.data.remote.dto.SubscriptionCollectionDto
import com.thebluealliance.android.data.remote.dto.VoidRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

interface ClientApi {

    @POST("clientapi/tbaClient/v9/favorites/list")
    suspend fun listFavorites(@Body body: VoidRequestDto = VoidRequestDto()): FavoriteCollectionDto

    @POST("clientapi/tbaClient/v9/subscriptions/list")
    suspend fun listSubscriptions(@Body body: VoidRequestDto = VoidRequestDto()): SubscriptionCollectionDto

    @POST("clientapi/tbaClient/v9/model/setPreferences")
    suspend fun updateModelPreferences(@Body request: ModelPreferenceRequestDto): BaseResponseDto

    @POST("clientapi/tbaClient/v9/register")
    suspend fun registerDevice(@Body request: RegisterDeviceRequestDto): BaseResponseDto

    @POST("clientapi/tbaClient/v9/unregister")
    suspend fun unregisterDevice(@Body request: RegisterDeviceRequestDto): BaseResponseDto
}
