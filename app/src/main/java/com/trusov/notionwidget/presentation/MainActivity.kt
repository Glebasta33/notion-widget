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
    private val scope = CoroutineScope(Dispatchers.IO + exceptionHandler)
    @Inject
    lateinit var apiService: ApiService
    private val content = mutableListOf<String>()
    private var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as App).component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        scope.launch {
            val response = apiService.getPageIds(resources.getString(R.string.zettel_db_id))
            if (response.isSuccessful) {
                val dbQueryDto = response.body()
                val ids = dbQueryDto?.results?.map { it.id }
                ids?.forEach {
                    val blocksResponse = apiService.getPageBlocks(it)
                    val text =
                        blocksResponse.body()?.results?.get(0)?.paragraph?.rich_text?.get(0)?.plain_text
                            ?: ""
                    withContext(Dispatchers.Main) {
                        content.add(text)
                    }
                }
                withContext(Dispatchers.Main) {
                    binding.tvText.text = content[index++]
                    binding.tvText.setOnClickListener {
                        if (index in 0 until content.size) {
                            binding.tvText.text = content[index++]
                        } else {
                            index = 0
                            binding.tvText.text = content[index]
                        }
                    }
                }
                Log.d("MainActivityTag", "content: ${content.toString()}")
            } else {
                Log.d("MainActivityTag", "not successful: ${response.message()}")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}