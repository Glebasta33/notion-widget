package com.trusov.notionwidget.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NotesDao {

    @Insert
    fun insertNotes(notes: List<NoteDbModel>)

    @Query("SELECT * FROM notes")
    suspend fun getNotes(): List<NoteDbModel>

    @Query("DELETE FROM notes")
    fun clear()
}