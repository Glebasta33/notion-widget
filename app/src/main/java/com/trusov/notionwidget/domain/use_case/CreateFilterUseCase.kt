package com.trusov.notionwidget.domain.use_case

import com.trusov.notionwidget.domain.entity.Filter
import com.trusov.notionwidget.domain.repository.Repository
import javax.inject.Inject

class CreateFilterUseCase @Inject constructor(
    private val repository: Repository
){
    operator fun invoke(filter: Filter) = repository.createFilter(filter)
}