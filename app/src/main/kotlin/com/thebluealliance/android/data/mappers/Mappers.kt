package com.thebluealliance.android.data.mappers

import com.thebluealliance.android.data.local.entity.AllianceEntity
import com.thebluealliance.android.data.local.entity.AwardEntity
import com.thebluealliance.android.data.local.entity.DistrictEntity
import com.thebluealliance.android.data.local.entity.DistrictRankingEntity
import com.thebluealliance.android.data.local.entity.EventAdvancementPointsEntity
import com.thebluealliance.android.data.local.entity.EventCOPRsEntity
import com.thebluealliance.android.data.local.entity.EventEntity
import com.thebluealliance.android.data.local.entity.EventInsightsEntity
import com.thebluealliance.android.data.local.entity.EventOPRsEntity
import com.thebluealliance.android.data.local.entity.EventRankingSortOrderEntity
import com.thebluealliance.android.data.local.entity.MatchEntity
import com.thebluealliance.android.data.local.entity.MediaEntity
import com.thebluealliance.android.data.local.entity.PointsSource
import com.thebluealliance.android.data.local.entity.RankingEntity
import com.thebluealliance.android.data.local.entity.TeamEntity
import com.thebluealliance.android.data.remote.dto.AwardDto
import com.thebluealliance.android.data.remote.dto.DistrictDto
import com.thebluealliance.android.data.remote.dto.DistrictEventPointsDto
import com.thebluealliance.android.data.remote.dto.DistrictRankingDto
import com.thebluealliance.android.data.remote.dto.EventAllianceDto
import com.thebluealliance.android.data.remote.dto.EventCOPRsDto
import com.thebluealliance.android.data.remote.dto.EventDistrictPointsEntryDto
import com.thebluealliance.android.data.remote.dto.EventDto
import com.thebluealliance.android.data.remote.dto.EventInsightsDto
import com.thebluealliance.android.data.remote.dto.EventOPRsDto
import com.thebluealliance.android.data.remote.dto.MatchDto
import com.thebluealliance.android.data.remote.dto.MediaDto
import com.thebluealliance.android.data.remote.dto.RankingItemDto
import com.thebluealliance.android.data.remote.dto.RankingResponseDto
import com.thebluealliance.android.data.remote.dto.RankingSortOrderDto
import com.thebluealliance.android.data.remote.dto.RegionalRankingDto
import com.thebluealliance.android.data.remote.dto.TeamDto
import com.thebluealliance.android.data.remote.dto.WebcastDto
import com.thebluealliance.android.domain.model.Alliance
import com.thebluealliance.android.domain.model.Award
import com.thebluealliance.android.domain.model.CompLevel
import com.thebluealliance.android.domain.model.District
import com.thebluealliance.android.domain.model.DistrictEventPoints
import com.thebluealliance.android.domain.model.DistrictRanking
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.EventAdvancementPoints
import com.thebluealliance.android.domain.model.EventCOPRs
import com.thebluealliance.android.domain.model.EventInsights
import com.thebluealliance.android.domain.model.EventOPRs
import com.thebluealliance.android.domain.model.Match
import com.thebluealliance.android.domain.model.Media
import com.thebluealliance.android.domain.model.PlayoffType
import com.thebluealliance.android.domain.model.Ranking
import com.thebluealliance.android.domain.model.RankingSortOrder
import com.thebluealliance.android.domain.model.RegionalEventPoints
import com.thebluealliance.android.domain.model.RegionalRanking
import com.thebluealliance.android.domain.model.Team
import com.thebluealliance.android.domain.model.Webcast
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

// ── Team ──

fun TeamDto.toEntity() =
    TeamEntity(
        key = key,
        number = teamNumber,
        name = name,
        nickname = nickname,
        city = city,
        state = stateProv,
        country = country,
        rookieYear = rookieYear,
    )

fun TeamEntity.toDomain() =
    Team(
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

fun EventDto.toEntity() =
    EventEntity(
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
        playoffType = playoffType ?: PlayoffType.OTHER.typeInt,
        firstEventCode = firstEventCode,
    )

fun EventEntity.toDomain() =
    Event(
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
        playoffType = PlayoffType.fromInt(playoffType),
        firstEventCode = firstEventCode,
        webcasts =
            webcasts?.let { raw ->
                try {
                    json.decodeFromString<List<WebcastDto>>(raw).map {
                        Webcast(
                            type = it.type,
                            channel = it.channel,
                            file = it.file,
                            date = it.date,
                        )
                    }
                } catch (_: Exception) {
                    emptyList()
                }
            } ?: emptyList(),
    )

// ── Match ──

fun MatchDto.toEntity() =
    MatchEntity(
        key = key,
        eventKey = eventKey,
        compLevel = compLevel,
        matchNumber = matchNumber,
        setNumber = setNumber,
        time = time,
        predictedTime = predictedTime,
        actualTime = actualTime,
        redTeamKeys = alliances?.red?.teamKeys?.joinToString(",") ?: "",
        redSurrogateTeamKeys = alliances?.red?.surrogateTeamKeys?.joinToString(",") ?: "",
        redScore = alliances?.red?.score ?: -1,
        blueTeamKeys = alliances?.blue?.teamKeys?.joinToString(",") ?: "",
        blueSurrogateTeamKeys = alliances?.blue?.surrogateTeamKeys?.joinToString(",") ?: "",
        blueScore = alliances?.blue?.score ?: -1,
        winningAlliance = winningAlliance,
        scoreBreakdown = scoreBreakdown?.toString(),
        videos = videos?.let { json.encodeToString(it) },
    )

fun MatchEntity.toDomain() =
    Match(
        key = key,
        eventKey = eventKey,
        compLevel = CompLevel.fromCode(compLevel),
        matchNumber = matchNumber,
        setNumber = setNumber,
        time = time,
        predictedTime = predictedTime,
        actualTime = actualTime,
        redTeamKeys = redTeamKeys.split(",").filter { it.isNotEmpty() },
        redSurrogateTeamKeys = redSurrogateTeamKeys.split(",").filter { it.isNotEmpty() },
        redScore = redScore,
        blueTeamKeys = blueTeamKeys.split(",").filter { it.isNotEmpty() },
        blueSurrogateTeamKeys = blueSurrogateTeamKeys.split(",").filter { it.isNotEmpty() },
        blueScore = blueScore,
        winningAlliance = winningAlliance,
        scoreBreakdown = scoreBreakdown,
        videos = videos,
    )

// ── Award ──

fun AwardDto.toEntities(): List<AwardEntity> {
    val recipientJson = json.encodeToString(recipientList)
    return recipientList
        .map { recipient ->
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
                ),
            )
        }
}

fun AwardEntity.toDomain() =
    Award(
        eventKey = eventKey,
        awardType = awardType,
        teamKey = teamKey,
        awardee = awardee.ifEmpty { null },
        name = name,
        year = year,
    )

// ── Ranking ──

fun RankingItemDto.toEntity(eventKey: String) =
    RankingEntity(
        eventKey = eventKey,
        teamKey = teamKey,
        rank = rank,
        dq = dq,
        matchesPlayed = matchesPlayed,
        wins = record?.wins ?: 0,
        losses = record?.losses ?: 0,
        ties = record?.ties ?: 0,
        sortOrders = json.encodeToString(sortOrders),
        extraStats = json.encodeToString(extraStats),
        qualAverage = qualAverage,
    )

fun RankingEntity.toDomain() =
    Ranking(
        eventKey = eventKey,
        teamKey = teamKey,
        rank = rank,
        dq = dq,
        matchesPlayed = matchesPlayed,
        wins = wins,
        losses = losses,
        ties = ties,
        qualAverage = qualAverage,
        sortOrders =
            try {
                json.decodeFromString(sortOrders)
            } catch (_: Exception) {
                emptyList()
            },
        extraStats =
            try {
                json.decodeFromString(extraStats)
            } catch (_: Exception) {
                emptyList()
            },
    )

fun RankingSortOrderDto.toDomain() =
    RankingSortOrder(
        name = name,
        precision = precision,
    )

fun RankingResponseDto.toSortOrderEntity(eventKey: String) =
    EventRankingSortOrderEntity(
        eventKey = eventKey,
        sortOrderInfo = json.encodeToString(sortOrderInfo ?: emptyList()),
        extraStatsInfo = json.encodeToString(extraStatsInfo ?: emptyList()),
    )

// A null element is kept in place (decoded as RankingSortOrderDto?) and coalesced to a
// blank-name placeholder, so this column stays index-aligned with its sort_orders values
// instead of shifting; the UI falls back to "Sort N" for a blank name. #1445.
private val placeholderSortOrder = RankingSortOrder(name = "", precision = 2)

fun EventRankingSortOrderEntity.getSortOrderInfo(): List<RankingSortOrder> =
    try {
        json
            .decodeFromString<List<RankingSortOrderDto?>>(sortOrderInfo)
            .map { it?.toDomain() ?: placeholderSortOrder }
    } catch (_: Exception) {
        emptyList()
    }

fun EventRankingSortOrderEntity.getExtraStatsInfo(): List<RankingSortOrder> =
    try {
        if (extraStatsInfo != null) {
            json
                .decodeFromString<List<RankingSortOrderDto?>>(extraStatsInfo)
                .map { it?.toDomain() ?: placeholderSortOrder }
        } else {
            emptyList()
        }
    } catch (_: Exception) {
        emptyList()
    }

// ── Alliance ──

fun EventAllianceDto.toEntity(
    eventKey: String,
    number: Int,
) = AllianceEntity(
    eventKey = eventKey,
    number = number,
    name = name,
    picks = json.encodeToString(picks),
    declines = json.encodeToString(declines),
    backupIn = backup?.`in`,
    backupOut = backup?.out,
    playoffStatus = status?.status,
    playoffLevel = status?.level,
    playoffDoubleElimRound = status?.doubleElimRound,
)

fun AllianceEntity.toDomain() =
    Alliance(
        eventKey = eventKey,
        number = number,
        name = name,
        picks = json.decodeFromString(picks),
        declines = json.decodeFromString(declines),
        backupIn = backupIn,
        backupOut = backupOut,
        playoffStatus = playoffStatus,
        playoffLevel = playoffLevel,
        playoffDoubleElimRound = playoffDoubleElimRound,
    )

// ── District ──

fun DistrictDto.toEntity() =
    DistrictEntity(
        key = key,
        abbreviation = abbreviation,
        displayName = displayName,
        year = year,
    )

fun DistrictEntity.toDomain() =
    District(
        key = key,
        abbreviation = abbreviation,
        displayName = displayName,
        year = year,
    )

// ── DistrictRanking ──

fun DistrictRankingDto.toEntity(districtKey: String) =
    DistrictRankingEntity(
        districtKey = districtKey,
        teamKey = teamKey,
        rank = rank,
        pointTotal = pointTotal,
        rookieBonus = rookieBonus,
        eventPoints = json.encodeToString(eventPoints),
    )

fun DistrictRankingEntity.toDomain() =
    DistrictRanking(
        districtKey = districtKey,
        teamKey = teamKey,
        rank = rank,
        pointTotal = pointTotal,
        rookieBonus = rookieBonus,
        eventPoints =
            try {
                json.decodeFromString<List<DistrictEventPointsDto>>(eventPoints).map {
                    DistrictEventPoints(
                        eventKey = it.eventKey,
                        total = it.total,
                        districtCmp = it.districtCmp,
                    )
                }
            } catch (_: Exception) {
                emptyList()
            },
    )

// ── RegionalRanking ──

fun RegionalRankingDto.toDomain(
    year: Int,
    advancementMethod: String? = null,
) = RegionalRanking(
    year = year,
    teamKey = teamKey,
    rank = rank,
    pointTotal = pointTotal,
    rookieBonus = rookieBonus,
    singleEventBonus = singleEventBonus,
    eventPoints =
        eventPoints.map { point ->
            RegionalEventPoints(
                eventKey = point.eventKey,
                total = point.total,
            )
        },
    advancementMethod = advancementMethod,
)

// ── EventDistrictPoints ──

fun EventDistrictPointsEntryDto.toEntity(
    eventKey: String,
    teamKey: String,
    source: PointsSource,
) = EventAdvancementPointsEntity(
    eventKey = eventKey,
    teamKey = teamKey,
    source = source,
    qualPoints = qualPoints,
    elimPoints = elimPoints,
    alliancePoints = alliancePoints,
    awardPoints = awardPoints,
    rookieBonus = rookieBonus,
    total = total,
)

fun EventAdvancementPointsEntity.toDomain() =
    EventAdvancementPoints(
        teamKey = teamKey,
        qualPoints = qualPoints,
        elimPoints = elimPoints,
        alliancePoints = alliancePoints,
        awardPoints = awardPoints,
        rookieBonus = rookieBonus,
        total = total,
    )

// ── Media ──

fun MediaDto.toEntity(
    teamKey: String,
    year: Int,
) = MediaEntity(
    teamKey = teamKey,
    type = type,
    foreignKey = foreignKey,
    year = year,
    preferred = preferred,
    details = details?.toString(),
    base64Image = base64Image,
)

fun MediaEntity.toDomain() =
    Media(
        teamKey = teamKey,
        type = type,
        foreignKey = foreignKey,
        year = year,
        preferred = preferred,
        details = details,
        base64Image = base64Image,
    )

// ── OPRs ──

fun EventOPRsDto.toEntity(eventKey: String) =
    EventOPRsEntity(
        eventKey = eventKey,
        oprs = json.encodeToString(oprs),
        dprs = json.encodeToString(dprs),
        ccwms = json.encodeToString(ccwms),
    )

fun EventOPRsEntity.toDomain() =
    EventOPRs(
        oprs = json.decodeFromString(oprs),
        dprs = json.decodeFromString(dprs),
        ccwms = json.decodeFromString(ccwms),
    )

// ── COPRs ──

fun EventCOPRsDto.toEntity(eventKey: String) =
    EventCOPRsEntity(
        eventKey = eventKey,
        coprs = json.encodeToString(coprs),
    )

fun EventCOPRsEntity.toDomain() =
    EventCOPRs(
        coprs = json.decodeFromString(coprs),
    )

// ── Insights ──

fun EventInsightsDto.toEntity(eventKey: String) =
    EventInsightsEntity(
        eventKey = eventKey,
        qualInsights = qual,
        playoffInsights = playoff,
    )

fun EventInsightsEntity.toDomain() =
    EventInsights(
        qual = qualInsights,
        playoff = playoffInsights,
    )
