package com.trusov.notionwidget.data.dto.filter.db_model

data class FilterRuleDbModel(
    val property: PropertyDbModel,
    val condition: String,
    val option: OptionDbModel
)
