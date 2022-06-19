package com.trusov.notionwidget.domain.repository

import com.trusov.notionwidget.data.dto.DbQueryDto
import com.trusov.notionwidget.data.dto.block.BlockResponseDto
import com.trusov.notionwidget.data.dto.db.DbDto
import com.trusov.notionwidget.data.dto.filter.FilterDto
import com.trusov.notionwidget.data.dto.filter.db_model.FilterDbModel
import com.trusov.notionwidget.domain.entity.Filter
import io.reactivex.rxjava3.core.Observable

interface Repository {
    fun loadPageIds(dbId: String, filter: Filter): Observable<DbQueryDto>
    fun loadPageBlocks(pageId: String): Observable<BlockResponseDto>
    fun loadDatabase(dbId: String): Observable<DbDto>
    fun createFilter(filter: Filter)
    fun getFilters(): Observable<List<FilterDbModel>>
    fun getFilterByName(name: String): Observable<FilterDbModel>
//    fun chooseFilter
}