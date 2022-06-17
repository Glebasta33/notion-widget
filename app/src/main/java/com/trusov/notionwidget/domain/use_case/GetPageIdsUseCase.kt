package com.trusov.notionwidget.domain.use_case

import com.trusov.notionwidget.data.dto.filter.FilterWrapperDto
import com.trusov.notionwidget.domain.repository.Repository
import javax.inject.Inject

class GetPageIdsUseCase @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke(dbId: String, filter: FilterWrapperDto) = repository.getPageIds(dbId, filter)
}