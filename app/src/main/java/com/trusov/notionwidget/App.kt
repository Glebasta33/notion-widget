package com.trusov.notionwidget

import android.app.Application
import com.trusov.notionwidget.di.DaggerApplicationComponent

class App : Application() {

    val component by lazy {
        DaggerApplicationComponent.factory().create(this)
    }

}