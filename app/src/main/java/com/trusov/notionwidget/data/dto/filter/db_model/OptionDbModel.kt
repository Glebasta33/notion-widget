package com.trusov.notionwidget.data.dto.filter.db_model

data class OptionDbModel(
    val name: String,
    val color: String,
    var isChecked: Boolean = false
)
