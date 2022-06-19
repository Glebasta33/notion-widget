package com.trusov.notionwidget.domain.entity

data class Filter(
    var name: String,
    val rules: MutableList<FilterRule>
)
