package com.trusov.notionwidget.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.trusov.notionwidget.App
import com.trusov.notionwidget.databinding.NotesFragmentBinding
import com.trusov.notionwidget.di.ViewModelFactory
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
    private var size = 0

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
        viewModel.loadBlocks()

        viewModel.blocks.observe(viewLifecycleOwner) { texts ->
            binding.tvText.text = texts[0]
            binding.tvText.setOnClickListener {
                if (index in texts.indices) {
                    index++
                    binding.tvText.text = texts[index]
                } else {
                    index = 0
                    binding.tvText.text = texts[0]
                }
            }
        }

    }


}