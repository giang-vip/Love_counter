package com.example.giao_dien.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.giao_dien.domain.repository.AppRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SplashState {
    data class Loading(val activeDotIndex: Int) : SplashState()
    object NavigateToLanguage : SplashState()
    object NavigateToIntro : SplashState()
    object NavigateToPermission : SplashState()
    object NavigateToHome : SplashState()
}

class SplashViewModel(
    private val repository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashState>(SplashState.Loading(0))
    val uiState: StateFlow<SplashState> = _uiState.asStateFlow()

    fun startLoadingAnimation() {
        viewModelScope.launch {
            // 3 giây = 12 bước x 250ms
            repeat(12) { step ->
                val activeIndex = step % 4
                _uiState.value = SplashState.Loading(activeIndex)
                delay(250)
            }

            when {
                repository.isPermissionGranted() -> {
                    _uiState.value = SplashState.NavigateToHome
                }
                repository.isIntroCompleted() -> {
                    _uiState.value = SplashState.NavigateToPermission
                }
                else -> {
                    _uiState.value = SplashState.NavigateToLanguage
                }
            }
        }
    }

    class Factory(
        private val repository: AppRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SplashViewModel(repository) as T
        }
    }
}