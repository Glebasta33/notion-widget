package com.trusov.notionwidget.data.dto

import com.google.gson.annotations.SerializedName

data class NoteIdsDto(
    @SerializedName("results")
    val results: List<IdDto>
)