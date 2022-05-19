package com.trusov.notionwidget.di

import androidx.lifecycle.ViewModel
import com.trusov.notionwidget.presentation.NotesViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @IntoMap
    @Binds
    @ViewModelKey(NotesViewModel::class)
    fun bindNotesViewModel(viewModel: NotesViewModel): ViewModel

}