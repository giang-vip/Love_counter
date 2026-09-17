package com.example.giao_dien.ui.permission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.giao_dien.domain.repository.AppRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PermissionUiState(
    val isPermissionGranted: Boolean = false
)

sealed class PermissionEvent {
    object FinishPermissionScreen : PermissionEvent()
}

class PermissionViewModel(
    private val repository: AppRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(PermissionUiState())

    val uiState: StateFlow<PermissionUiState> =
        _uiState.asStateFlow()

    private val _event =
        MutableSharedFlow<PermissionEvent>(
            extraBufferCapacity = 1
        )

    val event: SharedFlow<PermissionEvent> =
        _event.asSharedFlow()

    init {
        loadPermissionState()
    }

    private fun loadPermissionState() {

        val granted =
            repository.isPermissionGranted()

        _uiState.value =
            PermissionUiState(
                isPermissionGranted = granted
            )
    }

    fun updatePermissionResult(isGranted: Boolean) {

        _uiState.value =
            _uiState.value.copy(
                isPermissionGranted = isGranted
            )

        repository.setPermissionGranted(isGranted)
    }

    fun onContinueClicked() {

        viewModelScope.launch {

            _event.emit(
                PermissionEvent.FinishPermissionScreen
            )
        }
    }

    class Factory(
        private val repository: AppRepository
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T {

            return PermissionViewModel(repository) as T
        }
    }
}