package com.trusov.notionwidget.presentation

sealed class State

object Loading : State()
class Result(
    val texts: List<String>
) : State()
