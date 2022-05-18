package com.trusov.notionwidget.domain.use_case

import com.trusov.notionwidget.domain.repository.Repository
import javax.inject.Inject

class GetPageIdsUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(dbId: String) = repository.getPageIds(dbId)
}