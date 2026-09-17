package com.example.giao_dien.ui.start_date

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.giao_dien.domain.repository.CoupleRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class StartDateEvent {
    object SavedSuccessfully : StartDateEvent()
}

class StartDateViewModel(
    private val repository: CoupleRepository
) : ViewModel() {

    private val _startDateState = MutableStateFlow(repository.getLoveStartDate())
    val startDateState: StateFlow<String> = _startDateState.asStateFlow()

    private val _event = MutableSharedFlow<StartDateEvent>(extraBufferCapacity = 1)
    val event: SharedFlow<StartDateEvent> = _event.asSharedFlow()

    fun updateStartDate(date: String) {
        _startDateState.value = date
    }

    fun save() {
        repository.saveLoveStartDate(_startDateState.value)
        viewModelScope.launch {
            _event.emit(StartDateEvent.SavedSuccessfully)
        }
    }

    class Factory(
        private val repository: CoupleRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return StartDateViewModel(repository) as T
        }
    }
}