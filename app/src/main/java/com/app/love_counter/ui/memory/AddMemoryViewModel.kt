package com.app.love_counter.ui.memory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.app.love_counter.domain.model.MemoryInfo
import com.app.love_counter.domain.repository.MemoryRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.app.love_counter.R

sealed class AddMemoryEvent {
    object SavedSuccessfully : AddMemoryEvent()
    data class ShowToast(val messageRes: Int) : AddMemoryEvent()
}

class AddMemoryViewModel(
    private val repository: MemoryRepository
) : ViewModel() {

    private val _imageUriState = MutableStateFlow<String?>(null)
    val imageUriState: StateFlow<String?> = _imageUriState.asStateFlow()

    private val _dateState = MutableStateFlow<String>("")
    val dateState: StateFlow<String> = _dateState.asStateFlow()

    private val _event = MutableSharedFlow<AddMemoryEvent>(extraBufferCapacity = 1)
    val event: SharedFlow<AddMemoryEvent> = _event.asSharedFlow()

    fun setImageUri(uri: String?) {
        _imageUriState.value = uri
    }

    fun setDate(date: String) {
        _dateState.value = date
    }

    fun saveMemory(title: String) {
        val trimmedTitle = title.trim()
        val date = _dateState.value.ifBlank { "08/09/2023" }

        if (trimmedTitle.isBlank()) {
            _event.tryEmit(AddMemoryEvent.ShowToast(R.string.msg_error_empty_title))
            return
        }

        val memory = MemoryInfo(
            title = trimmedTitle,
            date = date,
            imageUri = _imageUriState.value
        )

        viewModelScope.launch {
            repository.addMemory(memory)
            _event.emit(AddMemoryEvent.SavedSuccessfully)
        }
    }

    class Factory(
        private val repository: MemoryRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AddMemoryViewModel(repository) as T
        }
    }
}