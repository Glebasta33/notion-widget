package com.trusov.notionwidget.presentation.ui.filter_editor

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.trusov.notionwidget.App
import com.trusov.notionwidget.R
import com.trusov.notionwidget.databinding.FragmentFilterEditorBinding
import com.trusov.notionwidget.di.ViewModelFactory
import com.trusov.notionwidget.domain.entity.Condition
import com.trusov.notionwidget.domain.use_case.GetPageBlocksUseCase
import com.trusov.notionwidget.presentation.Error
import com.trusov.notionwidget.presentation.Loading
import com.trusov.notionwidget.presentation.NotesResult
import com.trusov.notionwidget.presentation.PropertiesResult
import java.util.*
import javax.inject.Inject

class FilterEditorFragment : Fragment() {

    private var _binding: FragmentFilterEditorBinding? = null
    private val binding: FragmentFilterEditorBinding
        get() = _binding ?: throw RuntimeException("FragmentFilterEditorBinding == null")

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            viewModelFactory
        )[FilterEditorViewModel::class.java]
    }

    // TODO: remove after testing:
    @Inject
    lateinit var getPageBlocksUseCase: GetPageBlocksUseCase

    private var index = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity?.application as App).component.inject(this)
        arguments?.let { args ->
            args.getString("FilterName").let { filterName ->
                //viewModel.getFilterByName(filterName!!)
                Toast.makeText(activity, filterName, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterEditorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getProperties(requireActivity().application.getString(R.string.zettel_db_id))
        setupSpinnerConditions()
        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.progressBar.isGone = true
            when (state) {
                is NotesResult -> {
                    setupTexts(state.value.map { it.text })
                }
                is PropertiesResult -> {
                    state.value.let { properties ->
                        ArrayAdapter(
                            requireActivity(),
                            android.R.layout.simple_spinner_item,
                            properties.map { it.name }
                        ).also { adapter ->
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            binding.spinnerProperties.adapter = adapter
                        }
                    }
                }
                is Error -> {
                    Toast.makeText(activity, state.value, Toast.LENGTH_SHORT).show()
                }
                is Loading -> {
                    binding.progressBar.isGone = false
                }
            }
        }

        binding.buttonFilter.setOnClickListener {
            val option = binding.spinnerProperties.selectedItem.toString()
            viewModel.getNotes(option)
        }

        binding.buttonSaveFilter.setOnClickListener {
            val option = binding.spinnerProperties.selectedItem.toString()
            val filterName = binding.etFilterName.text.toString()
            viewModel.saveFilter(option, filterName)
            viewModel.saveNotes(option, filterName)
            binding.etFilterName.text.clear()
            if (filterName.isNotEmpty()) {
                Toast.makeText(activity, "Filter \"$filterName\" saved", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSpinnerConditions() {
        ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            Condition.values().map {
                it.value.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }
            }
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerConditions.adapter = adapter
        }
    }

    private fun setupTexts(texts: List<String>) {
        binding.tvText.text = texts[0]
        binding.tvText.setOnClickListener {
            if (index in texts.indices) {
                binding.tvText.text = texts[index]
                index++
            } else {
                index = 0
                binding.tvText.text = texts[0]
            }
        }
    }

    companion object {
        private const val TAG = "NotesFragmentTag"
    }

}