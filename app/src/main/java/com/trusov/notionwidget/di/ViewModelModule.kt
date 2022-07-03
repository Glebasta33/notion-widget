package com.trusov.notionwidget.di

import androidx.lifecycle.ViewModel
import com.trusov.notionwidget.presentation.ui.filter_editor.FilterEditorViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @IntoMap
    @Binds
    @ViewModelKey(FilterEditorViewModel::class)
    fun bindNotesViewModel(viewModel: FilterEditorViewModel): ViewModel

}