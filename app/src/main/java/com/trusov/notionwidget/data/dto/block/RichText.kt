package com.trusov.notionwidget.data.dto.block

import com.google.gson.annotations.SerializedName

data class RichText(
    @SerializedName("plain_text")
    val plain_text: String,
    @SerializedName("type")
    val type: String
)