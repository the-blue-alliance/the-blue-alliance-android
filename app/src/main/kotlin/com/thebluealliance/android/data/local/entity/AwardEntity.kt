package com.thebluealliance.android.data.local.entity

import androidx.room.Entity

@Entity(tableName = "awards", primaryKeys = ["eventKey", "awardType", "teamKey", "awardee"])
data class AwardEntity(
    val eventKey: String,
    val awardType: Int,
    val teamKey: String,
    val awardee: String,
    val name: String,
    val year: Int,
    val recipientList: String,
)
