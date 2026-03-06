package com.thebluealliance.android.data.remote.dto

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

// The API returns COPRs as a flat object where keys are stat names
// and values are maps of team key -> stat value
// Example: { "stat_name": { "frc1234": 15.5, ... }, ... }
@Serializable(with = EventCOPRsDtoSerializer::class)
data class EventCOPRsDto(
    val coprs: Map<String, Map<String, Double>> = emptyMap(),
)

object EventCOPRsDtoSerializer : KSerializer<EventCOPRsDto> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("EventCOPRsDto") {
        element<Map<String, Map<String, Double>>>("coprs")
    }

    override fun serialize(encoder: Encoder, value: EventCOPRsDto) {
        error("Serialization not supported")
    }

    override fun deserialize(decoder: Decoder): EventCOPRsDto {
        require(decoder is JsonDecoder)
        val json = decoder.decodeJsonElement() as JsonObject

        // The API returns the COPR data directly at the root level
        // Each key is a stat name, each value is a map of team key -> double
        val coprs = mutableMapOf<String, Map<String, Double>>()

        for ((statName, statData) in json) {
            if (statData is JsonObject) {
                val teamStats = mutableMapOf<String, Double>()
                for ((teamKey, value) in statData) {
                    if (value is JsonPrimitive && value.isString == false) {
                        try {
                            teamStats[teamKey] = value.jsonPrimitive.content.toDouble()
                        } catch (_: Exception) {
                            // Skip malformed values
                        }
                    }
                }
                coprs[statName] = teamStats
            }
        }

        return EventCOPRsDto(coprs)
    }
}
