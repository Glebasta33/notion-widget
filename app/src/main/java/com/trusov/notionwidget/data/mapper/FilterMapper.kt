package com.trusov.notionwidget.data.mapper

import com.trusov.notionwidget.data.dto.filter.FilterDto
import com.trusov.notionwidget.data.dto.filter.FilterRuleDto
import com.trusov.notionwidget.data.dto.filter.MultiSelectDto
import com.trusov.notionwidget.data.dto.filter.db_model.FilterDbModel
import com.trusov.notionwidget.data.dto.filter.db_model.FilterRuleDbModel
import com.trusov.notionwidget.data.dto.filter.db_model.OptionDbModel
import com.trusov.notionwidget.data.dto.filter.db_model.PropertyDbModel
import com.trusov.notionwidget.domain.entity.Filter
import com.trusov.notionwidget.domain.entity.FilterRule
import javax.inject.Inject

class FilterMapper @Inject constructor() {
    fun mapEntityToDto(entity: Filter): FilterDto {
        val filterDto = FilterDto(
            filter = FilterRuleDto(
                multi_select = MultiSelectDto(
                   contains = entity.rules[0].option.name
                ),
                property = entity.rules[0].property.name
            )
        )
        return filterDto
    }

    fun mapEntityToDbModel(entity: Filter) = FilterDbModel(
        name = entity.name,
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

}