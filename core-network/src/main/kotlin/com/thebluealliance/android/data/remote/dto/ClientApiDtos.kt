package com.thebluealliance.android.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class VoidRequestDto

@Serializable
data class BaseResponseDto(
    val code: Int = 0,
    val message: String = "",
)

@Serializable
data class FavoriteDto(
    @SerialName("model_key") val modelKey: String,
    @SerialName("model_type") val modelType: Int,
)

@Serializable
data class FavoriteCollectionDto(
    val code: Int = 0,
    val message: String = "",
    val favorites: List<FavoriteDto> = emptyList(),
)

@Serializable
data class SubscriptionDto(
    @SerialName("model_key") val modelKey: String,
    @SerialName("model_type") val modelType: Int,
    val notifications: List<String> = emptyList(),
)

@Serializable
data class SubscriptionCollectionDto(
    val code: Int = 0,
    val message: String = "",
    val subscriptions: List<SubscriptionDto> = emptyList(),
)

@Serializable
data class ModelPreferenceRequestDto(
    @SerialName("model_key") val modelKey: String,
    @SerialName("model_type") val modelType: Int,
    @SerialName("device_key") val deviceKey: String = "",
    val notifications: List<String> = emptyList(),
    val favorite: Boolean = false,
)

@Serializable
data class RegisterDeviceRequestDto(
    @SerialName("mobile_id") val mobileId: String,
    @SerialName("operating_system") val os: String = "android-fcm",
    val name: String,
    @SerialName("device_uuid") val deviceUuid: String,
)
