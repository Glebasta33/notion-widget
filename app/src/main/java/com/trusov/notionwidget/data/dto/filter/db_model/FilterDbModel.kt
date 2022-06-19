package com.trusov.notionwidget.data.dto.filter.db_model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.trusov.notionwidget.data.converter.FilterConverter
import com.trusov.notionwidget.domain.entity.FilterRule

@Entity(tableName = "filters")
@TypeConverters(value = [FilterConverter::class])
data class FilterDbModel(
    @PrimaryKey
    val name: String,
    val rules: List<FilterRuleDbModel>
)
