package com.zybooks.remindmev2.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zybooks.remindmev2.data.local.database.Reminder
import com.zybooks.remindmev2.data.local.database.ReminderWithTags
import com.zybooks.remindmev2.data.repository.ReminderRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ReminderListUiState(
    val reminders: List<ReminderWithTags> = emptyList()
)

class ReminderListViewModel(
    private val repository: ReminderRepository
) : ViewModel() {

    val uiState: StateFlow<ReminderListUiState> = repository.allReminders
        .map { ReminderListUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ReminderListUiState()
        )
        
    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.deleteReminder(reminder)
        }
    }
}

