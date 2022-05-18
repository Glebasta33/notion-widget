package com.trusov.notionwidget.di

import android.app.Application
import com.trusov.notionwidget.data.retrofit.ApiFactory
import com.trusov.notionwidget.data.retrofit.ApiService
import dagger.Module
import dagger.Provides

@Module
interface DataModule {

    companion object {
        @Provides
        fun provideApiService(application: Application): ApiService {
            return ApiFactory(application).service
        }
    }
}