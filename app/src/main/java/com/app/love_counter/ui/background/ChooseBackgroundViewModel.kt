package com.app.love_counter.ui.background

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.app.love_counter.R
import com.app.love_counter.domain.repository.CoupleRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class BackgroundEvent {
    object BackgroundSaved : BackgroundEvent()
}

class ChooseBackgroundViewModel(
    private val repository: CoupleRepository
) : ViewModel() {

    private val _backgroundItems = MutableStateFlow<List<BackgroundItem>>(emptyList())
    val backgroundItems: StateFlow<List<BackgroundItem>> = _backgroundItems.asStateFlow()

    private val _event = MutableSharedFlow<BackgroundEvent>(extraBufferCapacity = 1)
    val event: SharedFlow<BackgroundEvent> = _event.asSharedFlow()

    init {
        loadBackgrounds()
    }

    private fun loadBackgrounds() {
        val list = mutableListOf<BackgroundItem>()
        // Item 0: Ô Dấu + để tải ảnh từ máy
        list.add(BackgroundItem(isAddButton = true))

        // Ảnh mẫu 1, 2
        list.add(BackgroundItem(drawableRes = R.drawable.backgroud_home1))
        list.add(BackgroundItem(drawableRes = R.drawable.backgroud_home2))

        _backgroundItems.value = list
    }

    fun selectBackgroundUri(uriString: String) {
        repository.saveBackgroundUri(uriString)
        viewModelScope.launch {
            _event.emit(BackgroundEvent.BackgroundSaved)
        }
    }

    class Factory(
        private val repository: CoupleRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ChooseBackgroundViewModel(repository) as T
        }
    }
}