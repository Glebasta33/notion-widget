package com.trusov.notionwidget.data.dto.filter

import com.google.gson.annotations.SerializedName

data class CreatedTime(
    @SerializedName("past_month")
    val past_month: PastMonth
)