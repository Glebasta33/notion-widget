package com.trusov.notionwidget.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.trusov.notionwidget.R
import com.trusov.notionwidget.data.local.NotesDao
import com.trusov.notionwidget.data.retrofit.ApiService
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

    fun getContent() {
//        val ids = getPageIdsUseCase(application.resources.getString(R.string.zettel_db_id))
//        ids.subscribeOn(Schedulers.io())
//            .observeOn(Schedulers.io())
//            .subscribe({
//                        notesDao.clear()
//                       it.results.forEach { idDto ->
//                            getPageBlocksUseCase(idDto.id)
//                                .subscribeOn(Schedulers.io())
//                                .observeOn(Schedulers.io())
//                                .subscribe { blockDto ->
//                                    val text = blockDto.results[0].paragraph.rich_text[0].plain_text
//                                    notesDao.insertNote(NoteDbModel(text, 0))
//                                    Log.d(TAG, text)
//                                }
//                       }
//            }, {
//                Log.d(TAG, it.message ?: "Error")
//            }, {
//                Log.d(TAG, "Completed")
//            })

        apiService.getDatabaseJson(application.resources.getString(R.string.zettel_db_id))
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({
                if (it.isSuccessful) {
                    val resultJson = it.body()?.asJsonObject
                    val properties = resultJson?.getAsJsonObject("properties")
                    val resultString = it.body().toString()
                    parseProperties(resultString).forEach {
                        val p = properties?.getAsJsonObject(it)
                        val options = p?.getAsJsonObject("multi_select")?.getAsJsonArray("options")
                        Log.d(TAG, "=== PROPERTY: $it ===")
                        options?.forEach {
                            val option = it as JsonObject
                            Log.d(TAG, option.get("name").asString)
                        }

                    }
                } else {
                    Log.d(TAG, "not successful: ${it.code()}")
                }
            }, {
                Log.d(TAG, it.message ?: "onError")
            })
    }

    private fun parseProperties(body: String): List<String> {
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