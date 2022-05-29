package com.trusov.notionwidget.domain.repository

import com.trusov.notionwidget.data.dto.DbQueryDto
import com.trusov.notionwidget.data.dto.block.BlockResponseDto
import com.trusov.notionwidget.data.dto.db.DbDto
import retrofit2.Response
import retrofit2.http.Path

interface Repository {
    suspend fun getPageIds(dbId: String): Response<DbQueryDto>
    suspend fun getPageBlocks(pageId: String): Response<BlockResponseDto>
    suspend fun getDatabase(dbId: String): Response<DbDto>
}