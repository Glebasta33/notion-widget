package com.trusov.notionwidget.data.dto.db

data class Domain(
    val id: String,
    val multi_select: MultiSelect,
    val name: String,
    val type: String
)