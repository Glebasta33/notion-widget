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

    private var filterName: String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity?.application as App).component.inject(this)
        arguments?.let { args ->
            filterName = args.getString("FilterName")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {8
        super.onViewCreated(view, savedInstanceState)
        val adapter = NoteAdapter()
        binding.rvNotes.adapter = adapter
        binding.rvNotes.layoutManager = GridLayoutManager(activity, 2)
        adapter.submitList(listOf(Note("1"), Note("2"), Note("3"), Note("4")))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}