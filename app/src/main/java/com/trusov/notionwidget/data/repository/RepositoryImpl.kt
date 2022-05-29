package com.trusov.notionwidget.data.repository

import com.trusov.notionwidget.data.dto.DbQueryDto
import com.trusov.notionwidget.data.dto.block.BlockResponseDto
import com.trusov.notionwidget.data.dto.db.DbDto
import com.trusov.notionwidget.data.retrofit.ApiService
import com.trusov.notionwidget.domain.repository.Repository
import retrofit2.Response
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : Repository {
    override suspend fun getPageIds(dbId: String): Response<DbQueryDto> {
        return apiService.getPageIds(dbId)
    }

    override suspend fun getPageBlocks(pageId: String): Response<BlockResponseDto> {
        return apiService.getPageBlocks(pageId)
    }

    override suspend fun getDatabase(dbId: String): Response<DbDto> {
        return apiService.getDatabase(dbId)
    }

}