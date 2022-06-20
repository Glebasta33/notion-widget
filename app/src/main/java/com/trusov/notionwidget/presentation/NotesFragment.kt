package com.trusov.notionwidget.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.trusov.notionwidget.App
import com.trusov.notionwidget.databinding.NotesFragmentBinding
import com.trusov.notionwidget.di.ViewModelFactory
import com.trusov.notionwidget.domain.entity.Property
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class NotesFragment : Fragment() {

    private var _binding: NotesFragmentBinding? = null
    private val binding: NotesFragmentBinding
        get() = _binding ?: throw RuntimeException("NotesFragmentBinding == null")

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            viewModelFactory
        )[NotesViewModel::class.java]
    }

    private var index = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity?.application as App).component.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NotesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadDbProperties()
        viewModel.db
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ models ->
                if (models.isNotEmpty()) {
                    val texts = models.map { it.text }
                    setupTexts(texts)
                }
            }, {
                Toast.makeText(activity, "On Error: ${it.message}", Toast.LENGTH_SHORT).show()
            })

        viewModel.properties.observe(viewLifecycleOwner) {
            val options = it[Property("Topic")]?.map { p -> p.name }
            options?.let {
                ArrayAdapter(
                    requireActivity(),
                    android.R.layout.simple_spinner_item,
                    options
                ).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerTags.adapter = adapter
                }
            }

        }

        binding.buttonFilter.setOnClickListener {
            val option = binding.spinnerTags.selectedItem.toString()
            viewModel.loadContent(option)
        }

        binding.buttonSaveFilter.setOnClickListener {
            val option = binding.spinnerTags.selectedItem.toString()
            viewModel.saveFilter(option)
        }

        viewModel.getFilters()
        viewModel.getFilterByName("Filter 2")
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

}