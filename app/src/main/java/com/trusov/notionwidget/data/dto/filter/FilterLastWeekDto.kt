package com.trusov.notionwidget.data.dto.filter

import com.google.gson.annotations.SerializedName

data class FilterLastWeekDto(
    @SerializedName("filter")
    val filter: Filter
)