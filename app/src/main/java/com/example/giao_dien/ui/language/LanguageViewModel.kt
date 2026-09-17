package com.example.giao_dien.ui.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.giao_dien.domain.model.Language
import com.example.giao_dien.domain.repository.AppRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LanguageUiState(
    val languages: List<Language> = emptyList(),
    val selectedCode: String? = null
)

sealed class LanguageEvent {
    data class NavigateToIntro(val languageCode: String) : LanguageEvent()
}

class LanguageViewModel(
    private val repository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LanguageUiState())
    val uiState: StateFlow<LanguageUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<LanguageEvent>(extraBufferCapacity = 1)
    val event: SharedFlow<LanguageEvent> = _event.asSharedFlow()

    init {
        loadLanguages()
    }

    private fun loadLanguages() {
        val allLanguages = repository.getLanguages()
        val initialList = allLanguages.map { lang ->
            lang.copy(isSelected = false)
        }
        _uiState.value = LanguageUiState(
            languages = initialList,
            selectedCode = null
        )
    }

    fun selectLanguage(code: String) {
        val updatedList = _uiState.value.languages.map { lang ->
            lang.copy(isSelected = lang.code == code)
        }
        _uiState.value = _uiState.value.copy(
            languages = updatedList,
            selectedCode = code
        )
    }

    fun confirmLanguage() {
        val code = _uiState.value.selectedCode ?: return
        repository.saveSelectedLanguage(code)
        viewModelScope.launch {
            _event.emit(LanguageEvent.NavigateToIntro(code))
        }
    }

    class Factory(
        private val repository: AppRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LanguageViewModel(repository) as T
        }
    }
}