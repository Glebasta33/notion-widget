package com.trusov.notionwidget.data.local

import androidx.room.*
import com.trusov.notionwidget.data.dto.filter.db_model.FilterDbModel
import com.trusov.notionwidget.data.dto.filter.db_model.FilterWithNotes
import io.reactivex.rxjava3.core.Observable

@Dao
interface FiltersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(filter: FilterDbModel)

    @Transaction
    @Query("SELECT * FROM filters WHERE name == :name LIMIT 1")
    fun findFilterWithNotesByName(name: String): Observable<FilterWithNotes>

    @Query("SELECT * FROM filters")
    fun getFilters(): Observable<List<FilterDbModel>>
}