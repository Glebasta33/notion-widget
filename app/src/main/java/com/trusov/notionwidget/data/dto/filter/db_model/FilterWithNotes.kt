package com.trusov.notionwidget.data.dto.filter.db_model

import androidx.room.Embedded
import androidx.room.Relation
import com.trusov.notionwidget.data.dto.note.NoteDbModel

class FilterWithNotes(
    @Embedded
    val filter: FilterDbModel,
    @Relation(parentColumn = "name", entityColumn = "filterName")
    val notes: List<NoteDbModel>
)