package com.trusov.notionwidget.data.dto.db

data class Topic(
    val id: String,
    val multi_select: MultiSelectX,
    val name: String,
    val type: String
)