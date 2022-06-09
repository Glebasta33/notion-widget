package com.trusov.notionwidget.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Observable

@Dao
interface NotesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNotes(notes: List<NoteDbModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNote(note: NoteDbModel)

    @Query("SELECT * FROM notes")
    fun getNotes(): Observable<List<NoteDbModel>>

    @Query("DELETE FROM notes")
    fun clear()
}