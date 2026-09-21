package com.app.love_counter.ui.love_test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

import com.app.love_counter.R

sealed class NameTestEvent {
    data class NavigateToResult(val yourName: String, val partnerName: String) : NameTestEvent()
    data class ShowToast(val messageRes: Int) : NameTestEvent()
}

class NameTestViewModel : ViewModel() {

    private val _event = MutableSharedFlow<NameTestEvent>(extraBufferCapacity = 1)
    val event: SharedFlow<NameTestEvent> = _event.asSharedFlow()

    fun startLoveTest(yourName: String, partnerName: String) {
        val name1 = yourName.trim()
        val name2 = partnerName.trim()

        if (name1.isBlank() || name2.isBlank()) {
            _event.tryEmit(NameTestEvent.ShowToast(R.string.msg_error_empty_names))
            return
        }

        _event.tryEmit(NameTestEvent.NavigateToResult(name1, name2))
    }

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NameTestViewModel() as T
        }
    }
}