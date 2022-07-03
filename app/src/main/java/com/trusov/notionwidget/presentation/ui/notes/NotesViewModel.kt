package com.trusov.notionwidget.presentation.ui.notes

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.trusov.notionwidget.R
import com.trusov.notionwidget.domain.entity.*
import com.trusov.notionwidget.domain.use_case.GetFilterWithNotesByNameUseCase
import com.trusov.notionwidget.domain.use_case.GetPageBlocksUseCase
import com.trusov.notionwidget.domain.use_case.LoadPageIdsUseCase
import com.trusov.notionwidget.presentation.Loading
import com.trusov.notionwidget.presentation.NotesResult
import com.trusov.notionwidget.presentation.State
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class NotesViewModel @Inject constructor(
    private val getFilterWithNotesByNameUseCase: GetFilterWithNotesByNameUseCase
) : ViewModel() {

    private val TAG = "NotesViewModelTag"

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    fun getFilterByName(name: String) {
        getFilterWithNotesByNameUseCase(name)
            .subscribeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe({
                _state.postValue(NotesResult(it.notes ?: listOf()))
            }, {
                Log.d(TAG, it.stackTraceToString())
            })
    }


}