package com.trusov.notionwidget.data.mapper

import com.trusov.notionwidget.data.dto.filter.FilterDto
import com.trusov.notionwidget.data.dto.filter.FilterRuleDto
import com.trusov.notionwidget.data.dto.filter.MultiSelectDto
import com.trusov.notionwidget.domain.entity.Filter
import javax.inject.Inject

class FilterMapper @Inject constructor() {
    fun mapEntityToDto(filterEntity: Filter): FilterDto {
        val filterDto = FilterDto(
            filter = FilterRuleDto(
                multi_select = MultiSelectDto(
                   contains = filterEntity.rules[0].option.name
                ),
                property = filterEntity.rules[0].property.name
            )
        )
        return filterDto
    }
}