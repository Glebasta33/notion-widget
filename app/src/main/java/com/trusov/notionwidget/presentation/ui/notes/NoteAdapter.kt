package com.trusov.notionwidget.presentation.ui.notes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.trusov.notionwidget.databinding.RvItemNoteBinding
import com.trusov.notionwidget.domain.entity.note.Note

class NoteAdapter : ListAdapter<Note, NoteViewHolder>(NoteDiffUtils()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = RvItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = currentList[position]
        holder.binding.tvNote.text = note.text
    }
}