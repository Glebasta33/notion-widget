package com.trusov.notionwidget.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.trusov.notionwidget.R
import com.trusov.notionwidget.data.local.NoteDbModel
import com.trusov.notionwidget.data.local.NotesDao
import com.trusov.notionwidget.domain.use_case.GetPageBlocksUseCase
import com.trusov.notionwidget.domain.use_case.GetPageIdsUseCase
import kotlinx.coroutines.*
import javax.inject.Inject

class NotesViewModel @Inject constructor(
    private val getPageIdsUseCase: GetPageIdsUseCase,
    private val getPageBlocksUseCase: GetPageBlocksUseCase,
    private val application: Application,
    private val notesDao: NotesDao
) : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.d("MainActivityTag", "exceptionHandler ${throwable.message}")
    }
    private val scope = CoroutineScope(Dispatchers.IO + exceptionHandler)

    private val _blocks = MutableLiveData<List<String>>()
    val blocks: LiveData<List<String>> = _blocks

    fun loadBlocks() {
        scope.launch {
            val response = getPageIdsUseCase(application.getString(R.string.zettel_db_id))
            if (response.isSuccessful) {
                val ids: List<String>? = response.body()?.results?.map { it.id }
                val texts = mutableListOf<String>()
                val noteDbModels = mutableListOf<NoteDbModel>()
                ids?.forEach {
                    val text =
                        getPageBlocksUseCase(it)
                            .body()?.results?.get(0)?.paragraph?.rich_text?.get(0)?.plain_text
                            ?: ""
                    texts.add(text)
                    noteDbModels.add(NoteDbModel(text, 0))
                }
                _blocks.postValue(texts)
                notesDao.clear()
                notesDao.insertNotes(noteDbModels)
                Log.d("MainActivityTag", "content: ${texts.toString()}")
            } else {
                Log.d("MainActivityTag", "not successful: ${response.message()}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}