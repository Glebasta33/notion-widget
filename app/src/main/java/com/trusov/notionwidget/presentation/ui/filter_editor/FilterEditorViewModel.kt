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
    private val loadPageIdsUseCase: LoadPageIdsUseCase,
    private val getPageBlocksUseCase: GetPageBlocksUseCase,
    private val application: Application,
    private val notesDao: NotesDao,
    private val apiService: ApiService,
    private val createFilterUseCase: CreateFilterUseCase,
    private val getFiltersUseCase: GetFiltersUseCase,
    private val getFilterWithNotesByNameUseCase: GetFilterWithNotesByNameUseCase,
    private val getPropertiesUseCase: GetPropertiesUseCase
) : ViewModel() {

    val db = notesDao.getNotes()

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

    fun getNotes(property: String, condition: String, option: String) {
        val texts = mutableListOf<String>()
        loadNotesIds(property, condition, option)
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

    fun saveFilter(property: String, condition: String, option: String, filterName: String?) {
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
                                type = Type.MULTI_SELECT,
                                null // временно!
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

    fun saveNotes(property: String, condition: String, option: String, filterName: String?) {
        if (filterName.isNullOrEmpty()) {
            _state.postValue(Error("Input some name"))
            return
        }
        val noteIds = loadNotesIds(property, condition, option)
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

    private fun loadNotesIds(property: String, condition: String, option: String) = loadPageIdsUseCase(
        application.resources.getString(R.string.zettel_db_id),
        Filter(
            rules = mutableListOf(
                FilterRule(
                    property = Property(
                        name = property,
                        type = Type.MULTI_SELECT,
                        null
                    ),
                    condition = Condition.mapStringToCondition(condition.lowercase()),
                    option = Option(
                        name = option,
                        color = "red",
                        isChecked = true
                    )
                )
            )
        )
    )

    fun getProperties(dbId: String) {
        getPropertiesUseCase(dbId)
            .subscribeOn(Schedulers.io())
            .subscribe {
                _state.postValue(PropertiesResult(it))
            }
    }

    companion object {
        private const val TAG = "NotesViewModelTag"
    }

}