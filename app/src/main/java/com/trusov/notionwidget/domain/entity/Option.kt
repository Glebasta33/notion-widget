package com.trusov.notionwidget.domain.entity

data class Option(
    val name: String,
    val color: String,
    var isChecked: Boolean = false
)