package com.example.giao_dien.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.giao_dien.domain.model.CoupleInfo
import com.example.giao_dien.domain.repository.CoupleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HomeUiState(
    val coupleInfo: CoupleInfo? = null,
    val loveDaysCount: Int = 0,
    val backgroundUri: String? = null
)

class HomeViewModel(
    private val repository: CoupleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun loadHomeData() {
        val couple = repository.getCoupleInfo()
        val days = repository.getLoveDaysCount()
        val bgUri = repository.getBackgroundUri()
        _uiState.value = HomeUiState(
            coupleInfo = couple,
            loveDaysCount = days,
            backgroundUri = bgUri
        )
    }

    class Factory(
        private val repository: CoupleRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(repository) as T
        }
    }
}