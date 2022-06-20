package com.trusov.notionwidget.data.dto.note

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteDbModel(
    val text: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val filterName: String
)