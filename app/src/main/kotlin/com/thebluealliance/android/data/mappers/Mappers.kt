package com.thebluealliance.android.data.mappers

import com.thebluealliance.android.data.local.entity.*
import com.thebluealliance.android.data.remote.dto.*
import com.thebluealliance.android.domain.model.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

// ── Team ──

fun TeamDto.toEntity() = TeamEntity(
    key = key,
    number = teamNumber,
    name = name,
    nickname = nickname,
    city = city,
    state = stateProv,
    country = country,
    rookieYear = rookieYear,
)

fun TeamEntity.toDomain() = Team(
    key = key,
    number = number,
    name = name,
    nickname = nickname,
    city = city,
    state = state,
    country = country,
    rookieYear = rookieYear,
)

// ── Event ──

fun EventDto.toEntity() = EventEntity(
    key = key,
    name = name,
    eventCode = eventCode,
    year = year,
    type = eventType,
    district = district?.key,
    city = city,
    state = stateProv,
    country = country,
    startDate = startDate,
    endDate = endDate,
    week = week,
    shortName = shortName,
    website = website,
    timezone = timezone,
    webcasts = webcasts?.let { json.encodeToString(it) },
    locationName = locationName,
    address = address,
    gmapsUrl = gmapsUrl,
)

fun EventEntity.toDomain() = Event(
    key = key,
    name = name,
    eventCode = eventCode,
    year = year,
    type = type,
    district = district,
    city = city,
    state = state,
    country = country,
    startDate = startDate,
    endDate = endDate,
    week = week,
    shortName = shortName,
    website = website,
    timezone = timezone,
    locationName = locationName,
    address = address,
    gmapsUrl = gmapsUrl,
    webcasts = webcasts?.let { raw ->
        try {
            json.decodeFromString<List<WebcastDto>>(raw).map {
                Webcast(type = it.type, channel = it.channel, file = it.file)
            }
        } catch (_: Exception) { emptyList() }
    } ?: emptyList(),
)

// ── Match ──

fun MatchDto.toEntity() = MatchEntity(
    key = key,
    eventKey = eventKey,
    compLevel = compLevel,
    matchNumber = matchNumber,
    setNumber = setNumber,
    time = time,
    actualTime = actualTime,
    redTeamKeys = alliances?.red?.teamKeys?.joinToString(",") ?: "",
    redScore = alliances?.red?.score ?: -1,
    blueTeamKeys = alliances?.blue?.teamKeys?.joinToString(",") ?: "",
    blueScore = alliances?.blue?.score ?: -1,
    winningAlliance = winningAlliance,
    scoreBreakdown = scoreBreakdown?.toString(),
    videos = videos?.let { json.encodeToString(it) },
)

fun MatchEntity.toDomain() = Match(
    key = key,
    eventKey = eventKey,
    compLevel = compLevel,
    matchNumber = matchNumber,
    setNumber = setNumber,
    time = time,
    actualTime = actualTime,
    redTeamKeys = redTeamKeys.split(",").filter { it.isNotEmpty() },
    redScore = redScore,
    blueTeamKeys = blueTeamKeys.split(",").filter { it.isNotEmpty() },
    blueScore = blueScore,
    winningAlliance = winningAlliance,
    scoreBreakdown = scoreBreakdown,
    videos = videos,
)

// ── Award ──

fun AwardDto.toEntities(): List<AwardEntity> {
    val recipientJson = json.encodeToString(recipientList)
    return recipientList.map { recipient ->
        AwardEntity(
            eventKey = eventKey,
            awardType = awardType,
            teamKey = recipient.teamKey ?: "",
            awardee = recipient.awardee ?: "",
            name = name,
            year = year,
            recipientList = recipientJson,
        )
    }.ifEmpty {
        listOf(
            AwardEntity(
                eventKey = eventKey,
                awardType = awardType,
                teamKey = "",
                awardee = "",
                name = name,
                year = year,
                recipientList = recipientJson,
            )
        )
    }
}

fun AwardEntity.toDomain() = Award(
    eventKey = eventKey,
    awardType = awardType,
    teamKey = teamKey,
    awardee = awardee.ifEmpty { null },
    name = name,
    year = year,
)

// ── Ranking ──

fun RankingItemDto.toEntity(eventKey: String) = RankingEntity(
    eventKey = eventKey,
    teamKey = teamKey,
    rank = rank,
    dq = dq,
    matchesPlayed = matchesPlayed,
    wins = record?.wins ?: 0,
    losses = record?.losses ?: 0,
    ties = record?.ties ?: 0,
    sortOrders = json.encodeToString(sortOrders),
    qualAverage = qualAverage,
)

fun RankingEntity.toDomain() = Ranking(
    eventKey = eventKey,
    teamKey = teamKey,
    rank = rank,
    dq = dq,
    matchesPlayed = matchesPlayed,
    wins = wins,
    losses = losses,
    ties = ties,
    qualAverage = qualAverage,
)

// ── Alliance ──

fun EventAllianceDto.toEntity(eventKey: String, number: Int) = AllianceEntity(
    eventKey = eventKey,
    number = number,
    name = name,
    picks = json.encodeToString(picks),
    declines = json.encodeToString(declines),
    backupIn = backup?.`in`,
    backupOut = backup?.out,
)

fun AllianceEntity.toDomain() = Alliance(
    eventKey = eventKey,
    number = number,
    name = name,
    picks = json.decodeFromString(picks),
    declines = json.decodeFromString(declines),
    backupIn = backupIn,
    backupOut = backupOut,
)

// ── District ──

fun DistrictDto.toEntity() = DistrictEntity(
    key = key,
    abbreviation = abbreviation,
    displayName = displayName,
    year = year,
)

fun DistrictEntity.toDomain() = District(
    key = key,
    abbreviation = abbreviation,
    displayName = displayName,
    year = year,
)

// ── DistrictRanking ──

fun DistrictRankingDto.toEntity(districtKey: String) = DistrictRankingEntity(
    districtKey = districtKey,
    teamKey = teamKey,
    rank = rank,
    pointTotal = pointTotal,
    rookieBonus = rookieBonus,
    eventPoints = json.encodeToString(eventPoints),
)

fun DistrictRankingEntity.toDomain() = DistrictRanking(
    districtKey = districtKey,
    teamKey = teamKey,
    rank = rank,
    pointTotal = pointTotal,
    rookieBonus = rookieBonus,
)

// ── Media ──

fun MediaDto.toEntity(teamKey: String, year: Int) = MediaEntity(
    teamKey = teamKey,
    type = type,
    foreignKey = foreignKey,
    year = year,
    preferred = preferred,
    details = details?.toString(),
)

fun MediaEntity.toDomain() = Media(
    teamKey = teamKey,
    type = type,
    foreignKey = foreignKey,
    year = year,
    preferred = preferred,
    details = details,
)
