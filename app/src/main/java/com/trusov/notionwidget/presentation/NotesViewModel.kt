package com.trusov.notionwidget.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.trusov.notionwidget.R
import com.trusov.notionwidget.data.dto.filter.Filter
import com.trusov.notionwidget.data.dto.filter.FilterWrapperDto
import com.trusov.notionwidget.data.dto.filter.MultiSelect
import com.trusov.notionwidget.data.local.NoteDbModel
import com.trusov.notionwidget.data.local.NotesDao
import com.trusov.notionwidget.data.retrofit.ApiService
import com.trusov.notionwidget.domain.entity.Option
import com.trusov.notionwidget.domain.entity.Property
import com.trusov.notionwidget.domain.use_case.GetDatabaseUseCase
import com.trusov.notionwidget.domain.use_case.GetPageBlocksUseCase
import com.trusov.notionwidget.domain.use_case.GetPageIdsUseCase
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.regex.Pattern
import javax.inject.Inject

class NotesViewModel @Inject constructor(
    private val getPageIdsUseCase: GetPageIdsUseCase,
    private val getPageBlocksUseCase: GetPageBlocksUseCase,
    private val getDatabaseUseCase: GetDatabaseUseCase,
    private val application: Application,
    private val notesDao: NotesDao,
    private val apiService: ApiService
) : ViewModel() {

    private val TAG = "NotesViewModelTag"
    val db = notesDao.getNotes()

    private val _properties = MutableLiveData<List<Property>>()
    val properties: LiveData<List<Property>> = _properties

    fun getContent(tag: String) {
        val ids = getPageIdsUseCase(
            application.resources.getString(R.string.zettel_db_id),
            FilterWrapperDto(
                Filter(
                    multi_select = MultiSelect(tag),
                    property = "Topic"
                )
            )
        )
        ids.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({
                notesDao.clear()
                it.results.forEach { idDto ->
                    getPageBlocksUseCase(idDto.id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe { blockDto ->
                            val text = blockDto.results[0].paragraph.rich_text[0].plain_text
                            notesDao.insertNote(NoteDbModel(text, 0))
                            Log.d(TAG, text)
                        }
                }
            }, {
                Log.d(TAG, it.message ?: "Error")
            }, {
                Log.d(TAG, "Completed")
            })
    }

    fun getProperties() {
        apiService.getDatabaseJson(application.resources.getString(R.string.zettel_db_id))
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({
                if (it.isSuccessful) {
                    val dbAsString = it.body().toString()
                    val dbAsJson = it.body()?.asJsonObject
                    val propertiesJson = dbAsJson?.getAsJsonObject("properties")
                    val listOfProperties = mutableListOf<Property>()
                    parsePropertyNames(dbAsString).forEach { propertyName ->
                        val p = propertiesJson?.getAsJsonObject(propertyName)
                        val optionsJson =
                            p?.getAsJsonObject("multi_select")?.getAsJsonArray("options")
                        val optionNames = mutableListOf<Option>()
                        optionsJson?.forEach {
                            val optionJson = it as JsonObject
                            val option = Option(
                                name = optionJson.get("name").asString,
                                color = optionJson.get("color").asString
                            )
                            optionNames.add(option)
                        }
                        val property = Property(
                            name = propertyName,
                            options = optionNames
                        )
                        listOfProperties.add(property)
                    }
                    _properties.postValue(listOfProperties)
                } else {
                    Log.d(TAG, "not successful: ${it.code()}")
                }
            }, {
                Log.d(TAG, it.message ?: "onError")
            })
    }

    private fun parsePropertyNames(body: String): List<String> {
        val properties = mutableListOf<String>()
        val pattern = Pattern.compile("$PROPERTY_START(\\w+)$PROPERTY_END")
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
    }

}