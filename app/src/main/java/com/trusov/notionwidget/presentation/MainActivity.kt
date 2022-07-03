package com.trusov.notionwidget.presentation

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import com.trusov.notionwidget.App
import com.trusov.notionwidget.R
import com.trusov.notionwidget.databinding.ActivityMainBinding
import com.trusov.notionwidget.domain.use_case.GetFiltersUseCase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivityTag"
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

        setupDrawer()

    }

    private fun setupDrawer() {
        val disposable = getFiltersUseCase()
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val filters = it.map { it.name }
                for (filter in filters) {
                    binding.nvView.menu.add(filter)
                }
            }
        compositeDisposable.add(disposable)
        binding.nvView.setNavigationItemSelectedListener {
            val args = Bundle().apply {
                putString("FilterName", it.title.toString())
            }
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_notesFragment_self, args)
            true
        }
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