package com.trusov.notionwidget.domain.entity

enum class Condition(
    val value: String
) {
    CONTAINS("contains"),
    DOES_NOT_CONTAIN("does_not_contain"),
    IS_EMPTY("is_empty"),
    IS_NOT_EMPTY("is_not_empty");

    companion object {
        fun mapStringToCondition(string: String): Condition {
            return values().find { it.value == string }!!
        }
    }
}