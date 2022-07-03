package com.trusov.notionwidget.presentation.ui.filter_editor

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.trusov.notionwidget.R
import com.trusov.notionwidget.data.dto.note.NoteDbModel
import com.trusov.notionwidget.data.local.NotesDao
import com.trusov.notionwidget.data.retrofit.ApiService
import com.trusov.notionwidget.domain.entity.*
import com.trusov.notionwidget.domain.entity.note.Note
import com.trusov.notionwidget.domain.use_case.*
import com.trusov.notionwidget.presentation.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.regex.Pattern
import javax.inject.Inject

class FilterEditorViewModel @Inject constructor(
    private val getPageIdsUseCase: LoadPageIdsUseCase,
    private val getPageBlocksUseCase: GetPageBlocksUseCase,
    private val getDatabaseUseCase: GetDatabaseUseCase,
    private val application: Application,
    private val notesDao: NotesDao,
    private val apiService: ApiService,
    private val createFilterUseCase: CreateFilterUseCase,
    private val getFiltersUseCase: GetFiltersUseCase,
    private val getFilterWithNotesByNameUseCase: GetFilterWithNotesByNameUseCase
) : ViewModel() {

    private val TAG = "NotesViewModelTag"
    val db = notesDao.getNotes()

//    private val _properties = MutableLiveData<Map<Property, List<Option>>>()
//    val properties: LiveData<Map<Property, List<Option>>> = _properties
//
//    private val _texts = MutableLiveData<List<String>>()
//    val texts: LiveData<List<String>> = _texts

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    fun getFilters() {
        getFiltersUseCase()
            .subscribeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d(TAG, it.toString())
            }, {
                Log.d(TAG, it.stackTraceToString())
            })
    }

    fun getFilterByName(name: String) {
        getFilterWithNotesByNameUseCase(name)
            .subscribeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d(TAG, "getFilterByName: \nname: ${it.name} \nnotes: ${it.notes}")
            }, {
                Log.d(TAG, it.stackTraceToString())
            })
    }

    fun getNotes(option: String) {
        val texts = mutableListOf<String>()
        loadNotesIds(option)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({
                _state.postValue(Loading)
                it.results.forEach { idDto ->
                    getPageBlocksUseCase(idDto.id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe ({ blockDto ->
                            val text = blockDto.results[0].paragraph.rich_text[0].plain_text
                            texts.add(text)
                            Log.d(TAG, "text: $text")
                        }, {}, {
                            _state.postValue(NotesResult(texts.map { Note(it) }))
                        })
                }
            }, {
                Log.d(TAG, "onError: ${it.message}")
            })
    }

    fun saveFilter(option: String, filterName: String?) {
        if (filterName.isNullOrEmpty()) {
            _state.postValue(Error("Input some name"))
            return
        }
        Observable.fromCallable {
            createFilterUseCase(
                Filter(
                    name = filterName,
                    rules = mutableListOf(
                        FilterRule(
                            property = Property(
                                name = "Topic",
                                type = Type.MULTI_SELECT
                            ),
                            condition = Condition.CONTAINS,
                            option = Option(
                                name = option,
                                color = "red",
                                isChecked = true
                            )
                        )
                    )
                )
            )
        }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun saveNotes(option: String, filterName: String?) {
        if (filterName.isNullOrEmpty()) {
            _state.postValue(Error("Input some name"))
            return
        }
        // TODO: Реализовать вставку из уже загруженного значения NotesResult
        val noteIds = loadNotesIds(option)
        noteIds.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({
                it.results.forEach { idDto ->
                    getPageBlocksUseCase(idDto.id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe { blockDto ->
                            val text = blockDto.results[0].paragraph.rich_text[0].plain_text
                            notesDao.insertNote(NoteDbModel(text, 0, filterName))
                            Log.d(TAG, text)
                        }
                }
            }, {
                Log.d(TAG, it.message ?: "Error")
            }, {
                Log.d(TAG, "Completed")
            })
    }

    private fun loadNotesIds(option: String) = getPageIdsUseCase(
        application.resources.getString(R.string.zettel_db_id),
        Filter(
            rules = mutableListOf(
                FilterRule(
                    property = Property(
                        name = "Topic",
                        type = Type.MULTI_SELECT
                    ),
                    condition = Condition.CONTAINS,
                    option = Option(
                        name = option,
                        color = "red",
                        isChecked = true
                    )
                )
            )
        )
    )

    // private fun buildFilter(propertyName, propertyType: Type, condition: Condition, optionName: String, color: Color, ...): Filter {  }

    fun loadDbProperties() {
        // TODO: перенести в логику в getDatabaseUseCase,
        //  реализовать сохранение в таблцу options(?)(id, option_name, option_type, color, property).
        apiService.getDatabaseJson(application.resources.getString(R.string.zettel_db_id))
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({
                if (it.isSuccessful) {
                    val dbAsString = it.body().toString()
                    val dbAsJson = it.body()?.asJsonObject
                    val propertiesJson = dbAsJson?.getAsJsonObject("properties")
                    val map = mutableMapOf<Property, List<Option>>()
                    parsePropertyNames(dbAsString).forEach { propertyName ->
                        val p = propertiesJson?.getAsJsonObject(propertyName)
                        val optionsJson =
                            p?.getAsJsonObject("multi_select")?.getAsJsonArray("options")
                        val options = mutableListOf<Option>()
                        optionsJson?.forEach {
                            val optionJson = it as JsonObject
                            val option = Option(
                                name = optionJson.get("name").asString,
                                color = optionJson.get("color").asString
                            )
                            options.add(option)
                        }
                        val property = Property(name = propertyName)
                        map.put(property, options)
                    }
                    _state.postValue(PropertiesResult(map))
                    Log.d(TAG, map.toString())
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