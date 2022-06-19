package com.trusov.notionwidget.domain.entity

data class FilterRule(
    var property: Property,
    var condition: Condition = Condition.CONTAINS,
    var option: Option
)
