package com.trusov.notionwidget

import android.app.Application
import android.util.Log
import com.trusov.notionwidget.di.DaggerApplicationComponent
import io.reactivex.rxjava3.exceptions.UndeliverableException
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import java.io.IOException
import java.net.SocketException

class App : Application() {

    val component by lazy {
        DaggerApplicationComponent.factory().create(this)
    }

    override fun onCreate() {
        super.onCreate()
        RxJavaPlugins.setErrorHandler { error ->
            var e = error
            if (e is UndeliverableException) {
                e = e.cause ?:let { e }
            }
            if (e is IOException || e is SocketException) {
                // fine, irrelevant network problem or API that throws on cancellation
                return@setErrorHandler
            }
            if (e is InterruptedException) {
                // fine, some blocking code was interrupted by a dispose call
                return@setErrorHandler
            }
            if (e is NullPointerException || e is IllegalArgumentException) {
                // that's likely a bug in the application
                Thread.currentThread().uncaughtExceptionHandler?.uncaughtException(Thread.currentThread(), e)?:let {
                    Log.d(TAG, "RxJavaPlugins uncaughtExceptionHandler is null but error is NullPointerException || error is IllegalArgumentException : $e")
                }
                return@setErrorHandler
            }
            if (e is IllegalStateException) {
                // that's a bug in RxJava or in a custom operator
                Thread.currentThread().uncaughtExceptionHandler?.uncaughtException(Thread.currentThread(), e)?:let {
                    Log.d(TAG, "RxJavaPlugins uncaughtExceptionHandler is null but error is IllegalStateException : $e")
                }
                return@setErrorHandler
            }
            Log.d(TAG, "Undeliverable exception received, not sure what to do $e")

        }
    }

    companion object {
        private const val TAG = "ErrorHandlerTag"
    }

}