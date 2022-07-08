package com.trusov.notionwidget.domain.entity

data class Property(
    val name: String,
    val type: Type = Type.MULTI_SELECT,
    val options: List<Option>?
)
