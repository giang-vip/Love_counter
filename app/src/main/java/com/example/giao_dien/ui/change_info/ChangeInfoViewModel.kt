package com.example.giao_dien.ui.change_info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.giao_dien.domain.model.GenderType
import com.example.giao_dien.domain.model.PersonInfo
import com.example.giao_dien.domain.repository.CoupleRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ChangeInfoEvent {
    object SavedSuccessfully : ChangeInfoEvent()
}

class ChangeInfoViewModel(
    private val isMale: Boolean,
    private val repository: CoupleRepository
) : ViewModel() {

    private val _personState = MutableStateFlow(
        repository.getPersonInfo(isMale)
    )
    val personState: StateFlow<PersonInfo> = _personState.asStateFlow()

    private val _event = MutableSharedFlow<ChangeInfoEvent>(extraBufferCapacity = 1)
    val event: SharedFlow<ChangeInfoEvent> = _event.asSharedFlow()

    fun updateName(name: String) {
        _personState.value = _personState.value.copy(name = name)
    }

    fun updateGender(gender: GenderType) {
        _personState.value = _personState.value.copy(gender = gender)
    }

    fun updateBirthday(birthday: String) {
        _personState.value = _personState.value.copy(birthday = birthday)
    }

    fun updateAvatarUri(uri: String) {
        _personState.value = _personState.value.copy(avatarUri = uri)
    }

    fun save() {
        repository.savePersonInfo(isMale, _personState.value)
        viewModelScope.launch {
            _event.emit(ChangeInfoEvent.SavedSuccessfully)
        }
    }

    class Factory(
        private val isMale: Boolean,
        private val repository: CoupleRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ChangeInfoViewModel(isMale, repository) as T
        }
    }
}