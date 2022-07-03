package com.trusov.notionwidget.presentation

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
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
            setTitleTextColor(Color.WHITE)
        }
        setupDrawer()

    }

    private fun setupDrawer() {
        val disposable = getFiltersUseCase()
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val filters = it.map { it.name }
                val defaultFilter = filters[0]
                updateNotesByFilter(defaultFilter)
                setToolbarTitle(defaultFilter)
                for (filter in filters) {
                    binding.nvView.menu.add(filter)
                }
            }
        compositeDisposable.add(disposable)

        binding.nvView.setNavigationItemSelectedListener {
            updateNotesByFilter(it.title.toString())
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            true
        }
    }

    private fun updateNotesByFilter(filterName: String) {
        setToolbarTitle(filterName)
        val args = Bundle().apply {
            putString("FilterName", filterName)
        }
        findNavController(R.id.nav_host_fragment).navigate(R.id.action_notesFragment_self, args)
    }

    private fun setToolbarTitle(filterName: String) {
        binding.toolbar.apply {
            title = filterName
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