package com.earthgee.kotlin.mvi

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow

abstract class BaseViewModel<State: UiState, Event: UiEvent, Effect: UiEffect> {

    private val initialState: State by lazy { createInitialState() }

    abstract fun createInitialState(): State

    //uiState主要处理状态(ui)
    private val _uiState = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()

    //event主要处理事件
    private val _event = MutableSharedFlow<Event>()
    val event = _event.asSharedFlow()

    //effect为事件带来的副作用，通常是一对一的一次性事件
    private val _effect = Channel<Effect>()
    val effect = _effect.receiveAsFlow()

    init {
        subscribeEvents()
    }

    private fun subscribeEvents() {
        viewModelScope.launch {
            event.collect {
                handleEvent(it)
            }
        }
    }

    protected abstract fun handleEvent(event: Event)

    fun sendEvent(event: Event) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }

    fun setState(reduce: State.() -> State) {
        val newState = currenState.reduce()
        _uiState.value = newState
    }

    fun setEffect(builder: () -> Effect) {
        val newEffect = builder()
        viewModelScope.launch {
            _effect.send(newEffect)
        }
    }

}

interface UiState
interface UiEvent
interface UiEffect