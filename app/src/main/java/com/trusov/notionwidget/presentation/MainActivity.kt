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
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.d("MainActivityTag", "exceptionHandler ${throwable.message}")
    }
    @Inject
    lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as App).component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = apiService.getPageIds(resources.getString(R.string.zettel_db_id))
            if (response.isSuccessful) {
                val dbQueryDto = response.body()

                val ids = dbQueryDto?.results?.map { it.id }

                val builder = StringBuilder()
                ids?.forEach {
                    val blocksResponse = apiService.getPageBlocks(it)
                    val text = blocksResponse.body()?.results?.get(0)?.paragraph?.rich_text?.get(0)?.plain_text
                    builder.append(text).append("\n \n")
                }

                withContext(Dispatchers.Main) {
                    binding.tvText.text = builder.toString()
                }

            } else {
                Log.d("MainActivityTag", "not successful: ${response.message()}")
            }

        }

    }
}