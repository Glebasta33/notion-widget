package com.trusov.notionwidget.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.trusov.notionwidget.App
import com.trusov.notionwidget.R
import com.trusov.notionwidget.data.retrofit.ApiFactory
import com.trusov.notionwidget.data.retrofit.ApiService
import com.trusov.notionwidget.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as App).component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

}