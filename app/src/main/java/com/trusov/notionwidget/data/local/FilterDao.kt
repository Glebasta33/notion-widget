package com.trusov.notionwidget.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.trusov.notionwidget.data.dto.filter.db_model.FilterDbModel

@Dao
interface FiltersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(filter: FilterDbModel)

    @Query("SELECT * FROM filters WHERE name == :name LIMIT 1")
    fun findFilterByName(name: String): FilterDbModel?

}