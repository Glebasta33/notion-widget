package com.trusov.notionwidget.domain.entity

import com.trusov.notionwidget.domain.entity.note.Note

data class Filter(
    var name: String,
    val rules: List<FilterRule>,
    val notes: List<Note>? = null
)
