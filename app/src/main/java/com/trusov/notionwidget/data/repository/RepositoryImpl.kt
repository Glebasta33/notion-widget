package com.trusov.notionwidget.data.repository

import com.trusov.notionwidget.data.dto.DbQueryDto
import com.trusov.notionwidget.data.dto.block.BlockResponseDto
import com.trusov.notionwidget.data.dto.db.DbDto
import com.trusov.notionwidget.data.dto.filter.FilterDto
import com.trusov.notionwidget.data.mapper.FilterMapper
import com.trusov.notionwidget.data.retrofit.ApiService
import com.trusov.notionwidget.domain.entity.Filter
import com.trusov.notionwidget.domain.repository.Repository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val mapper: FilterMapper
) : Repository {

    override fun loadPageIds(dbId: String, filter: Filter): Observable<DbQueryDto> {
        val filterDto = mapper.mapEntityToDto(filter)
        return apiService.getPageIds(dbId, filterDto)
    }

    override fun loadPageBlocks(pageId: String): Observable<BlockResponseDto> {
        return apiService.getPageBlocks(pageId)
    }

    override fun loadDatabase(dbId: String): Observable<DbDto> {
        return apiService.getDatabase(dbId)
    }

    override fun createFilter(filter: Filter) {
        TODO("Not yet implemented")
    }

}