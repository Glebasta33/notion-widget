package com.trusov.notionwidget.presentation

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun setupDrawer() {
        val disposable = getFiltersUseCase()
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val filters = it.map { it.name }
                val defaultFilter = filters[0]
                updateFragment(defaultFilter, R.id.action_notesFragment_self)
                setToolbarTitle(defaultFilter)
                if (binding.nvView.menu.hasVisibleItems()) {
                    binding.nvView.menu.clear()
                }
                for (filter in filters) {
                    binding.nvView.menu.add(filter)
                }
                binding.nvView.menu.add(ADD_FILTER_DRAWER_ITEM).setIcon(R.drawable.ic_add)
                    .setOnMenuItemClickListener {
                        updateFragment(
                            it.title.toString(),
                            R.id.action_notesFragment_to_filterEditorFragment
                        )
                        true
                    }
            }
        compositeDisposable.add(disposable)

        binding.nvView.setNavigationItemSelectedListener {
            updateFragment(it.title.toString(), R.id.action_notesFragment_self)
            true
        }
    }

    private fun updateFragment(filterName: String, action: Int) {
        currentFilterName = filterName
        setToolbarTitle(filterName)
        navigateToFragment(filterName, action)
    }

    private fun navigateToFragment(filterName: String, action: Int) {
        val args = Bundle().apply {
            putString("FilterName", filterName)
        }
        val label = findNavController(R.id.nav_host_fragment).currentDestination?.label
        if (label == "fragment_filter_editor") {
            if (action == R.id.action_notesFragment_self) {
                findNavController(R.id.nav_host_fragment).navigate(
                    R.id.action_filterEditorFragment_to_notesFragment,
                    args
                )
            }
            if (action == R.id.action_notesFragment_to_filterEditorFragment) {
                findNavController(R.id.nav_host_fragment).navigate(
                    R.id.action_filterEditorFragment_self,
                    args
                )
            }
        } else {
            findNavController(R.id.nav_host_fragment).navigate(action, args)
        }
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    private fun setToolbarTitle(filterName: String) {
        if (filterName != ADD_FILTER_DRAWER_ITEM) {
            binding.toolbar.title = filterName
        } else {
            binding.toolbar.title = " "
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
            R.id.menu_edit -> {
                updateFragment(currentFilterName!!, R.id.action_notesFragment_to_filterEditorFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    companion object {
        private const val ADD_FILTER_DRAWER_ITEM = "Add filter"
        private var currentFilterName: String? = null
    }

}