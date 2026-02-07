package com.thebluealliance.android.domain.model

enum class NotificationType(
    val serverKey: String,
    val displayName: String,
    val channelId: String,
    val isSilent: Boolean = false,
) {
    UPCOMING_MATCH("upcoming_match", "Upcoming Match", "match_alerts"),
    MATCH_SCORE("match_score", "Match Score", "match_alerts"),
    MATCH_VIDEO("match_video", "Match Video Added", "match_alerts"),
    EVENT_MATCH_VIDEO("event_match_video", "Match Video Added", "match_alerts"),
    LEVEL_STARTING("starting_comp_level", "Competition Level Starting", "event_updates"),
    ALLIANCE_SELECTION("alliance_selection", "Alliance Selection", "event_updates"),
    AWARDS("awards_posted", "Awards Posted", "event_updates"),
    SCHEDULE_UPDATED("schedule_updated", "Schedule Updated", "event_updates"),
    FINAL_RESULTS("final_results", "Final Results", "event_updates"),
    DISTRICT_POINTS_UPDATED("district_points_updated", "District Points Updated", "event_updates"),
    PING("ping", "Test Notification", "general"),
    BROADCAST("broadcast", "Broadcast", "general"),
    UPDATE_FAVORITES("update_favorites", "", "general", isSilent = true),
    UPDATE_SUBSCRIPTIONS("update_subscriptions", "", "general", isSilent = true),
    ;

    companion object {
        fun fromServerKey(key: String): NotificationType? = entries.find { it.serverKey == key }

        fun forModelType(modelType: Int): List<NotificationType> = when (modelType) {
            ModelType.EVENT -> listOf(
                UPCOMING_MATCH, MATCH_SCORE, LEVEL_STARTING, ALLIANCE_SELECTION,
                AWARDS, SCHEDULE_UPDATED, FINAL_RESULTS,
            )
            ModelType.TEAM -> listOf(UPCOMING_MATCH, MATCH_SCORE, AWARDS)
            ModelType.MATCH -> listOf(UPCOMING_MATCH, MATCH_SCORE, MATCH_VIDEO)
            else -> emptyList()
        }
    }
}
