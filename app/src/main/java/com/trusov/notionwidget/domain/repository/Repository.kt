package com.trusov.notionwidget.domain.repository

import com.trusov.notionwidget.data.dto.DbQueryDto
import com.trusov.notionwidget.data.dto.block.BlockResponseDto
import retrofit2.Response

interface Repository {
    suspend fun getPageIds(dbId: String): Response<DbQueryDto>
    suspend fun getPageBlocks(pageId: String): Response<BlockResponseDto>

}