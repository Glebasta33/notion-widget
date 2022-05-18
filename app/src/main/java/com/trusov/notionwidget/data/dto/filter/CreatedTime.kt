package com.trusov.notionwidget.data.dto.filter

import com.google.gson.annotations.SerializedName

data class CreatedTime(
    @SerializedName("past_week")
    val past_week: PastWeek
)