package com.trusov.notionwidget.domain.entity

data class FilterRule(
    val property: Property,
    val condition: Condition = Condition.CONTAINS,
    val option: Option
)
