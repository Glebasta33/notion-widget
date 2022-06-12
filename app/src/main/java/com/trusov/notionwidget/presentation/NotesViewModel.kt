package com.trusov.notionwidget.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import com.trusov.notionwidget.R
import com.trusov.notionwidget.data.local.NoteDbModel
import com.trusov.notionwidget.data.local.NotesDao
import com.trusov.notionwidget.data.retrofit.ApiService
import com.trusov.notionwidget.domain.use_case.GetDatabaseUseCase
import com.trusov.notionwidget.domain.use_case.GetPageBlocksUseCase
import com.trusov.notionwidget.domain.use_case.GetPageIdsUseCase
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.ResponseBody
import javax.inject.Inject
import javax.security.auth.callback.Callback

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
                    val result = it.body().toString().length.toString()
                    Log.d(TAG, result)
                } else {
                    Log.d(TAG, "not successful: ${it.code()}")
                }
            }, {
                Log.d(TAG, it.message ?: "onError")
            })
    }

}