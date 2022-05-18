package com.trusov.notionwidget.di

import android.app.Application
import com.trusov.notionwidget.presentation.MainActivity
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [DataModule::class]
)
interface ApplicationComponent {

    fun inject(instance: MainActivity)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance
            application: Application
        ): ApplicationComponent
    }
}