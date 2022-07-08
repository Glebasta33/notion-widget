package com.trusov.notionwidget.data.mapper

import com.trusov.notionwidget.data.dto.filter.filter_dto.FilterWrapperDto
import com.trusov.notionwidget.data.dto.filter.filter_dto.FilterDto
import com.trusov.notionwidget.data.dto.filter.filter_dto.ConditionDto
import com.trusov.notionwidget.data.dto.filter.db_model.*
import com.trusov.notionwidget.domain.entity.*
import com.trusov.notionwidget.domain.entity.note.Note
import javax.inject.Inject

class FilterMapper @Inject constructor() {
//    fun mapEntityToDto(entity: Filter): FilterWrapperDto {
//        val filterDto = FilterWrapperDto(
//            filter = FilterDto(
//                multi_select = ConditionDto(
//                    contains = entity.rules[0].option.name
//                ),
//                property = entity.rules[0].property.name
//            )
//        )
//        return filterDto
//    }

    fun mapEntityToDbModel(entity: Filter) = FilterDbModel(
        name = entity.name ?: "",
        rules = entity.rules.map { rule ->
            FilterRuleDbModel(
                property = PropertyDbModel(
                    name = rule.property.name,
                    type = rule.property.type.value
                ),
                condition = rule.condition.value,
                option = OptionDbModel(
                    name = rule.option.name,
                    color = rule.option.color,
                    isChecked = rule.option.isChecked
                )
            )
        }
    )

    fun mapFilterWithNotesToEntity(dbModel: FilterWithNotes, notes: List<Note>) = Filter(
        name = dbModel.filter.name,
        rules = dbModel.filter.rules.map { rule ->
            FilterRule(
                property = Property(
                    name = rule.property.name,
                    type = Type.values().find { it.value == rule.property.type }!!,
                    options = null //временно!
                ),
                condition = Condition.values().find { it.value == rule.condition }!!,
                option = Option(
                    name = rule.option.name,
                    color = rule.option.color,
                    isChecked = rule.option.isChecked
                )
            )
        },
        notes = notes
    )

}