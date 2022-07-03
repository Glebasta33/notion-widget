package com.trusov.notionwidget.di

import androidx.lifecycle.ViewModel
import com.trusov.notionwidget.presentation.ui.filter_editor.FilterEditorViewModel
import com.trusov.notionwidget.presentation.ui.notes.NotesViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @IntoMap
    @Binds
    @ViewModelKey(FilterEditorViewModel::class)
    fun bindFilterEditorViewModel(viewModel: FilterEditorViewModel): ViewModel


    @IntoMap
    @Binds
    @ViewModelKey(NotesViewModel::class)
    fun bindNotesViewModel(viewModel: NotesViewModel): ViewModel

}