package com.trusov.notionwidget.data.dto.db

data class DbDto(
    val archived: Boolean,
    val cover: Any,
    val created_by: CreatedBy,
    val created_time: String,
    val icon: Icon,
    val id: String,
    val last_edited_by: LastEditedBy,
    val last_edited_time: String,
    val `object`: String,
    val parent: Parent,
    val properties: Properties,
    val title: List<TitleX>,
    val url: String
)