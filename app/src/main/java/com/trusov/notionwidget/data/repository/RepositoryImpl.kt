package com.trusov.notionwidget.data.repository

import com.trusov.notionwidget.data.dto.DbQueryDto
import com.trusov.notionwidget.data.dto.block.BlockResponseDto
import com.trusov.notionwidget.data.dto.db.DbDto
import com.trusov.notionwidget.data.dto.filter.FilterWrapperDto
import com.trusov.notionwidget.data.retrofit.ApiService
import com.trusov.notionwidget.domain.repository.Repository
import io.reactivex.rxjava3.core.Observable
import retrofit2.Response
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : Repository {

    override fun getPageIds(dbId: String, filter: FilterWrapperDto): Observable<DbQueryDto> {
        return apiService.getPageIds(dbId, filter)
    }

    override fun getPageBlocks(pageId: String): Observable<BlockResponseDto> {
        return apiService.getPageBlocks(pageId)
    }

    override fun getDatabase(dbId: String): Observable<DbDto> {
        return apiService.getDatabase(dbId)
    }

}