package com.trusov.notionwidget.data.dto

import com.google.gson.annotations.SerializedName

data class DbQueryDto(
    @SerializedName("results")
    val results: List<IdDto>
)