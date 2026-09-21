package com.app.love_counter.ui.horoscope

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.love_counter.domain.model.HoroscopeItem
import com.app.love_counter.domain.repository.HoroscopeRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class HoroscopeTestEvent {
    data class NavigateToResult(val h1Id: String, val h2Id: String) : HoroscopeTestEvent()
}

class HoroscopeTestViewModel(
    repository: HoroscopeRepository
) : ViewModel() {

    val allHoroscopes: List<HoroscopeItem> = repository.getAllHoroscopes()

    private val _yourHoroscope = MutableStateFlow(allHoroscopes.first())
    val yourHoroscope: StateFlow<HoroscopeItem> = _yourHoroscope.asStateFlow()

    private val _partnerHoroscope = MutableStateFlow(allHoroscopes.first())
    val partnerHoroscope: StateFlow<HoroscopeItem> = _partnerHoroscope.asStateFlow()

    private val _event = MutableSharedFlow<HoroscopeTestEvent>(extraBufferCapacity = 1)
    val event: SharedFlow<HoroscopeTestEvent> = _event.asSharedFlow()

    fun selectYourHoroscope(item: HoroscopeItem) {
        _yourHoroscope.value = item
    }

    fun selectPartnerHoroscope(item: HoroscopeItem) {
        _partnerHoroscope.value = item
    }

    fun startLoveTest() {
        _event.tryEmit(
            HoroscopeTestEvent.NavigateToResult(
                yourHoroscope.value.id,
                partnerHoroscope.value.id
            )
        )
    }

    class Factory(
        private val repository: HoroscopeRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HoroscopeTestViewModel(repository) as T
        }
    }
}