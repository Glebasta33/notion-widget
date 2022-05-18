package com.trusov.notionwidget.data.dto.filter

import com.google.gson.annotations.SerializedName

data class Filter(
    @SerializedName("created_time")
    val created_time: CreatedTime,
    @SerializedName("timestamp")
    val timestamp: String
)