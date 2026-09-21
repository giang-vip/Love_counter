package com.app.love_counter.ui.love_test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.love_counter.domain.model.NameTestResult
import com.app.love_counter.domain.repository.NameTestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NameTestResultViewModel(
    yourName: String,
    partnerName: String,
    repository: NameTestRepository
) : ViewModel() {

    private val _resultState = MutableStateFlow(
        repository.calculateNameTestResult(yourName, partnerName)
    )
    val resultState: StateFlow<NameTestResult> = _resultState.asStateFlow()

    class Factory(
        private val yourName: String,
        private val partnerName: String,
        private val repository: NameTestRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NameTestResultViewModel(yourName, partnerName, repository) as T
        }
    }
}