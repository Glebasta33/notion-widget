package com.trusov.notionwidget.data.mapper

import com.trusov.notionwidget.data.dto.note.NoteDbModel
import com.trusov.notionwidget.domain.entity.note.Note
import javax.inject.Inject

class NoteMapper @Inject constructor() {
    fun mapDbModelToEntity(dbModel: NoteDbModel) = Note(
        text = dbModel.text
    )
}