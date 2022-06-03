package com.trusov.notionwidget.di

import android.app.Application
import com.trusov.notionwidget.data.NoteAppWidgetProvider
import com.trusov.notionwidget.presentation.MainActivity
import com.trusov.notionwidget.presentation.NotesFragment
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(
    modules = [
        DataModule::class,
        ViewModelModule::class
    ]
)
interface ApplicationComponent {

    fun inject(instance: MainActivity)
    fun inject(instance: NotesFragment)
    fun inject(instance: NoteAppWidgetProvider)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance
            application: Application
        ): ApplicationComponent
    }
}