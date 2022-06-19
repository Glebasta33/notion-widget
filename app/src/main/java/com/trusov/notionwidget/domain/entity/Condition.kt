package com.trusov.notionwidget.domain.entity

enum class Condition(
    val value: String
) {
    EQUALS("equals"),
    DOES_NOT_EQUAL("does_not_equal"),
    CONTAINS("contains"),
    DOES_NOT_CONTAIN("does_not_contain"),
    STARTS_WITH("starts_with"),
    ENDS_WITH("ends_with"),
    IS_EMPTY("is_empty"),
    IS_NOT_EMPTY("is_not_empty")
}