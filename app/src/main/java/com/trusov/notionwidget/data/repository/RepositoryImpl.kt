package com.trusov.notionwidget.data.repository

import android.util.Log
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.trusov.notionwidget.data.dto.NoteIdsDto
import com.trusov.notionwidget.data.dto.block.BlockResponseDto
import com.trusov.notionwidget.data.dto.filter.db_model.FilterDbModel
import com.trusov.notionwidget.data.local.FiltersDao
import com.trusov.notionwidget.data.mapper.FilterMapper
import com.trusov.notionwidget.data.mapper.NoteMapper
import com.trusov.notionwidget.data.retrofit.ApiService
import com.trusov.notionwidget.domain.entity.Filter
import com.trusov.notionwidget.domain.entity.Option
import com.trusov.notionwidget.domain.entity.Property
import com.trusov.notionwidget.domain.repository.Repository
import io.reactivex.rxjava3.core.Observable
import retrofit2.Response
import java.util.regex.Pattern
import javax.inject.Inject

// TODO: Создать репозитории для источников данных: Local- и RemoteDataSource
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

    override fun getProperties(dbId: String): Observable<List<Property>> {
        return apiService.getDatabaseJson(dbId)
            .map {
                getListOfPropertiesFromJson(it)
            }
    }

    private fun getListOfPropertiesFromJson(response: Response<JsonElement>): List<Property> {
        val properties = mutableListOf<Property>()
        val options = mutableListOf<Option>()
        if (response.isSuccessful) {
            val dbAsString = response.body().toString()
            val dbAsJson = response.body()?.asJsonObject
            val propertiesJson = dbAsJson?.getAsJsonObject("properties")
            parsePropertyNames(dbAsString).forEach { propertyName ->
                val p = propertiesJson?.getAsJsonObject(propertyName)
                val optionsJson =
                    p?.getAsJsonObject("multi_select")?.getAsJsonArray("options")
                optionsJson?.forEach {
                    val optionJson = it as JsonObject
                    val option = Option(
                        name = optionJson.get("name").asString,
                        color = optionJson.get("color").asString
                    )
                    options.add(option)
                }
                val property = Property(name = propertyName, options = options)
                properties.add(property)
            }
        } else {
            Log.d(TAG, "not successful: ${response.code()}")
        }
        return properties
    }

    private fun parsePropertyNames(body: String): List<String> {
        val properties = mutableListOf<String>()
        val pattern = Pattern.compile("${PROPERTY_START}(\\w+)${PROPERTY_END}")
        val matcher = pattern.matcher(body)
        while (matcher.find()) {
            val property = matcher.group(1)?.toString()
            property?.let { property ->
                properties.add(property)
            }
        }
        return properties
    }

    companion object {
        private const val PROPERTY_START = "\"name\":\""
        private const val PROPERTY_END = "\",\"type"
        private const val TAG = "NotesViewModelTag"
    }

}