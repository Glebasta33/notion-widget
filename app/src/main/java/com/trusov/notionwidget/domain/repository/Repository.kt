package com.trusov.notionwidget.domain.repository

// TODO: Создать маперы для типов возвращаемых значений (Dto)
//  (domain-слой не должен ничего знать о data-слое)

import com.trusov.notionwidget.data.dto.NoteIdsDto
import com.trusov.notionwidget.data.dto.block.BlockResponseDto
import com.trusov.notionwidget.data.dto.filter.db_model.FilterDbModel
import com.trusov.notionwidget.domain.entity.Filter
import com.trusov.notionwidget.domain.entity.Property
import io.reactivex.rxjava3.core.Observable

interface Repository {
    fun loadPageIds(dbId: String, filter: Filter): Observable<NoteIdsDto>
    fun loadPageBlocks(pageId: String): Observable<BlockResponseDto>
    fun createFilter(filter: Filter)
    fun getFilters(): Observable<List<FilterDbModel>>
    fun getFilterWithNotesByName(name: String): Observable<Filter>
    fun getProperties(dbId: String): Observable<List<Property>>
}