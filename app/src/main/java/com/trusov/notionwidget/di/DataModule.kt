package com.trusov.notionwidget.di

import android.app.Application
import com.trusov.notionwidget.data.local.AppDatabase
import com.trusov.notionwidget.data.local.NotesDao
import com.trusov.notionwidget.data.repository.RepositoryImpl
import com.trusov.notionwidget.data.retrofit.ApiFactory
import com.trusov.notionwidget.data.retrofit.ApiService
import com.trusov.notionwidget.domain.repository.Repository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface DataModule {

    @Binds
    fun bindRepository(impl: RepositoryImpl): Repository

    companion object {
        @Provides
        @ApplicationScope
        fun provideApiService(application: Application): ApiService {
            return ApiFactory(application).service
        }

        @Provides
        @ApplicationScope
        fun provideNotesDao(application: Application): NotesDao {
            return AppDatabase.getInstance(application).notesDao()
        }
    }
}