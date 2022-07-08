package com.trusov.notionwidget.data.dto.filter.filter_dto

data class FilterDto(
    val propertyFieldName: String,
    val propertyValue: String,
    val conditionFieldName: String,
    val condition: ConditionDto
)