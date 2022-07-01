package com.trusov.notionwidget.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.trusov.notionwidget.App
import com.trusov.notionwidget.R
import com.trusov.notionwidget.data.retrofit.ApiFactory
import com.trusov.notionwidget.data.retrofit.ApiService
import com.trusov.notionwidget.databinding.ActivityMainBinding
import com.trusov.notionwidget.domain.use_case.GetFiltersUseCase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var getFiltersUseCase: GetFiltersUseCase
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as App).component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.toolbar.apply {
            setSupportActionBar(this)
            navigationIcon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_menu)
        }

        val disposable = getFiltersUseCase()
            .observeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val filters = it.map { it.name }
                for (filter in filters) {
                    binding.nvView.menu.add(filter)
                }
            }
        compositeDisposable.add(disposable)


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                binding.drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}