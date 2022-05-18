package com.trusov.notionwidget.data.dto.block

import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("created_time")
    val created_time: String,
    @SerializedName("has_children")
    val has_children: Boolean,
    @SerializedName("id")
    val id: String,
    @SerializedName("paragraph")
    val paragraph: Paragraph,
    @SerializedName("type")
    val type: String
)