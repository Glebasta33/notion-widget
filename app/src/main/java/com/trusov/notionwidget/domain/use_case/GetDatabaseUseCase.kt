package com.trusov.notionwidget.domain.use_case

import com.trusov.notionwidget.domain.repository.Repository
import javax.inject.Inject

class GetDatabaseUseCase @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke(dbId: String) = repository.getDatabase(dbId)
}