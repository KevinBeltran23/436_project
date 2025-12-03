package com.zybooks.remindmev2.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zybooks.remindmev2.data.local.database.UserSettings
import com.zybooks.remindmev2.data.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {

    val uiState: StateFlow<UserSettings> = repository.settings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserSettings()
        )

    fun updateDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateSettings(uiState.value.copy(darkMode = enabled))
        }
    }

    fun updatePushNotifications(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateSettings(uiState.value.copy(pushNotifications = enabled))
        }
    }
}

