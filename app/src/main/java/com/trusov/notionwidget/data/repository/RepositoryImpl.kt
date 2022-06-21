package com.trusov.notionwidget.data.repository

import com.trusov.notionwidget.data.dto.NoteIdsDto
import com.trusov.notionwidget.data.dto.block.BlockResponseDto
import com.trusov.notionwidget.data.dto.db.DbDto
import com.trusov.notionwidget.data.dto.filter.db_model.FilterDbModel
import com.trusov.notionwidget.data.local.FiltersDao
import com.trusov.notionwidget.data.mapper.FilterMapper
import com.trusov.notionwidget.data.mapper.NoteMapper
import com.trusov.notionwidget.data.retrofit.ApiService
import com.trusov.notionwidget.domain.entity.Filter
import com.trusov.notionwidget.domain.repository.Repository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val filterMapper: FilterMapper,
    private val noteMapper: NoteMapper,
    private val filtersDao: FiltersDao
) : Repository {

    override fun loadPageIds(dbId: String, filter: Filter): Observable<NoteIdsDto> {
        val filterDto = filterMapper.mapEntityToDto(filter)
        return apiService.getPageIds(dbId, filterDto)
    }

    override fun loadPageBlocks(pageId: String): Observable<BlockResponseDto> {
        return apiService.getPageBlocks(pageId)
    }

    override fun loadDatabase(dbId: String): Observable<DbDto> {
        return apiService.getDatabase(dbId)
    }

    override fun createFilter(filter: Filter) {
        val filterDbModel = filterMapper.mapEntityToDbModel(filter)
        filtersDao.insert(filterDbModel)
    }

    override fun getFilters(): Observable<List<FilterDbModel>> {
        return filtersDao.getFilters()
    }

    override fun getFilterWithNotesByName(name: String): Observable<Filter> {
        return filtersDao.findFilterWithNotesByName(name).map {
            val notes = it.notes.map { noteMapper.mapDbModelToEntity(it) }
            filterMapper.mapFilterWithNotesToEntity(it, notes)
        }
    }

}