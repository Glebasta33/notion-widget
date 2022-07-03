package com.trusov.notionwidget.presentation.ui.notes

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.trusov.notionwidget.App
import com.trusov.notionwidget.databinding.FragmentNotesBinding
import com.trusov.notionwidget.di.ViewModelFactory
import com.trusov.notionwidget.domain.entity.note.Note
import com.trusov.notionwidget.presentation.Loading
import com.trusov.notionwidget.presentation.NotesResult
import javax.inject.Inject

class NotesFragment : Fragment() {

    private var _binding: FragmentNotesBinding? = null
    private val binding: FragmentNotesBinding
        get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            viewModelFactory
        )[NotesViewModel::class.java]
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity?.application as App).component.inject(this)
        arguments?.let { args ->
            args.getString("FilterName").let { filterName ->
                viewModel.getFilterByName(filterName!!)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = NoteAdapter()
        binding.rvNotes.adapter = adapter
        binding.rvNotes.layoutManager = GridLayoutManager(activity, 2)
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is NotesResult -> {
                    adapter.submitList(state.value)
                }
                else -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}