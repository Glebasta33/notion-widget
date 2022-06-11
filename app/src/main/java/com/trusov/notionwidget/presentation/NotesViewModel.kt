package com.trusov.notionwidget.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.trusov.notionwidget.R
import com.trusov.notionwidget.data.dto.IdDto
import com.trusov.notionwidget.data.local.NoteDbModel
import com.trusov.notionwidget.data.local.NotesDao
import com.trusov.notionwidget.domain.use_case.GetDatabaseUseCase
import com.trusov.notionwidget.domain.use_case.GetPageBlocksUseCase
import com.trusov.notionwidget.domain.use_case.GetPageIdsUseCase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class NotesViewModel @Inject constructor(
    private val getPageIdsUseCase: GetPageIdsUseCase,
    private val getPageBlocksUseCase: GetPageBlocksUseCase,
    private val getDatabaseUseCase: GetDatabaseUseCase,
    private val application: Application,
    private val notesDao: NotesDao
) : ViewModel() {

    private val TAG = "NotesViewModelTag"
    private val ids = getPageIdsUseCase(application.resources.getString(R.string.zettel_db_id))

    private val _texts = MutableLiveData<List<String>>()
    val texts: LiveData<List<String>> = _texts

    val db = notesDao.getNotes()

    fun getContent() {
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

}