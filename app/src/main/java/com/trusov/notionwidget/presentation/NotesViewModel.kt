package com.trusov.notionwidget.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.trusov.notionwidget.R
import com.trusov.notionwidget.data.local.NoteDbModel
import com.trusov.notionwidget.data.local.NotesDao
import com.trusov.notionwidget.data.retrofit.ApiService
import com.trusov.notionwidget.domain.entity.*
import com.trusov.notionwidget.domain.use_case.CreateFilterUseCase
import com.trusov.notionwidget.domain.use_case.GetDatabaseUseCase
import com.trusov.notionwidget.domain.use_case.GetPageBlocksUseCase
import com.trusov.notionwidget.domain.use_case.LoadPageIdsUseCase
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.regex.Pattern
import javax.inject.Inject

class NotesViewModel @Inject constructor(
    private val getPageIdsUseCase: LoadPageIdsUseCase,
    private val getPageBlocksUseCase: GetPageBlocksUseCase,
    private val getDatabaseUseCase: GetDatabaseUseCase,
    private val application: Application,
    private val notesDao: NotesDao,
    private val apiService: ApiService,
    private val createFilterUseCase: CreateFilterUseCase
) : ViewModel() {

    private val TAG = "NotesViewModelTag"
    val db = notesDao.getNotes()

    private val _properties = MutableLiveData<Map<Property, List<Option>>>()
    val properties: LiveData<Map<Property, List<Option>>> = _properties

    fun saveFilter(option: String) {
        Observable.fromCallable {
            createFilterUseCase(
                Filter(
                    name = "Filter 1",
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

    fun loadContent(option: String) {
        val ids = getPageIdsUseCase(
            application.resources.getString(R.string.zettel_db_id),
            Filter(
                name = "Filter 1",
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

    fun loadDbProperties() {
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
                    _properties.postValue(map)
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