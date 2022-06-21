package com.trusov.notionwidget.presentation

import com.trusov.notionwidget.domain.entity.Option
import com.trusov.notionwidget.domain.entity.Property

sealed class State

object Loading : State()

class Error(
    val value: String
) : State()

class NotesResult(
    val value: List<String>
) : State()

class PropertiesResult(
    val value: Map<Property, List<Option>>
) : State()