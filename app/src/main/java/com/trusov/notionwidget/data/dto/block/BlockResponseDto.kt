package com.trusov.notionwidget.data.dto.block

import com.google.gson.annotations.SerializedName

data class BlockResponseDto(
    @SerializedName("results")
    val results: List<Result>
)