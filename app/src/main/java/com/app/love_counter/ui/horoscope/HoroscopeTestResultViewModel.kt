package com.app.love_counter.ui.horoscope

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.love_counter.domain.model.HoroscopeResult
import com.app.love_counter.domain.repository.HoroscopeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HoroscopeTestResultViewModel(
    h1Id: String,
    h2Id: String,
    repository: HoroscopeRepository
) : ViewModel() {

    private val _resultState = MutableStateFlow(
        repository.calculateHoroscopeResult(h1Id, h2Id)
    )
    val resultState: StateFlow<HoroscopeResult> = _resultState.asStateFlow()

    class Factory(
        private val h1Id: String,
        private val h2Id: String,
        private val repository: HoroscopeRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HoroscopeTestResultViewModel(h1Id, h2Id, repository) as T
        }
    }
}