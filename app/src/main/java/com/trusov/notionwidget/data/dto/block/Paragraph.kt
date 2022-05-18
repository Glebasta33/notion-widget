package com.trusov.notionwidget.data.dto.block

import com.google.gson.annotations.SerializedName

data class Paragraph(
    @SerializedName("color")
    val color: String,
    @SerializedName("rich_text")
    val rich_text: List<RichText>
)