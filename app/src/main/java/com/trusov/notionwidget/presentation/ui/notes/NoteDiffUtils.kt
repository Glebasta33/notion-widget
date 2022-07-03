package com.trusov.notionwidget.presentation.ui.notes

import androidx.recyclerview.widget.DiffUtil
import com.trusov.notionwidget.domain.entity.note.Note

class NoteDiffUtils : DiffUtil.ItemCallback<Note>() {
    override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem == oldItem
    }

    override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem == oldItem
    }
}