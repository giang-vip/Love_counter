package com.example.giao_dien.ui.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.giao_dien.domain.model.IntroPage
import com.example.giao_dien.domain.repository.AppRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class IntroUiState(
    val pages: List<IntroPage> = emptyList(),
    val currentPageIndex: Int = 0
)

sealed class IntroEvent {
    object NavigateToPermission : IntroEvent()
}

class IntroViewModel(
    private val repository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IntroUiState())
    val uiState: StateFlow<IntroUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<IntroEvent>(extraBufferCapacity = 1)
    val event: SharedFlow<IntroEvent> = _event.asSharedFlow()

    init {
        loadIntroPages()
    }

    private fun loadIntroPages() {
        val pages = repository.getIntroPages()
        _uiState.value = IntroUiState(pages = pages, currentPageIndex = 0)
    }

    fun onPageChanged(position: Int) {
        _uiState.value = _uiState.value.copy(currentPageIndex = position)
    }

    fun onNextClicked() {
        val currentIndex = _uiState.value.currentPageIndex
        val totalPages = _uiState.value.pages.size
        if (currentIndex < totalPages - 1) {
            _uiState.value = _uiState.value.copy(currentPageIndex = currentIndex + 1)
        } else {
            repository.setIntroCompleted(true)
            viewModelScope.launch {
                _event.emit(IntroEvent.NavigateToPermission)
            }
        }
    }

    class Factory(
        private val repository: AppRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return IntroViewModel(repository) as T
        }
    }
}