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
import com.zybooks.remindmev2.util.GeofenceHelper

data class ReminderListUiState(
    val reminders: List<ReminderWithTags> = emptyList()
)
class ReminderListViewModel(
    private val repository: ReminderRepository,
    private val geofenceHelper: GeofenceHelper
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
            geofenceHelper.removeGeofence(reminder)
        }
    }
    
    fun toggleReminderActive(reminder: Reminder) {
        viewModelScope.launch {
            val newActiveState = !reminder.isActive
            val updatedReminder = reminder.copy(isActive = newActiveState)
            
            // We use updateReminder in repository which expects tags too.
            // But wait, updateReminder in Repo currently requires list of tag strings to re-associate.
            // If we just update the reminder entity, we might lose tags if we use the Repo method that deletes cross refs.
            // Let's check Repository implementation again.
            
            // Repo implementation of updateReminder:
            // insertReminder(reminder) -> returns ID
            // deleteReminderTags(id)
            // insert new tags.
            
            // This is destructive for tags if we don't pass them back in. 
            // Ideally we should have a simpler update method in DAO just for Reminder entity if tags aren't changing.
            // Or we need to fetch tags first. 
            
            // Actually, we can just call dao.insertReminder(updatedReminder) directly if we expose it or add a specific method in Repo.
            // insertReminder uses OnConflictStrategy.REPLACE.
            // If we just replace the Reminder entity, the foreign keys in CrossRef should be fine assuming CASCADE isn't triggered by Update (it usually isn't, only Delete).
            // But Replace is technically a Delete + Insert. So Cascade Delete might trigger.
            
            // BETTER APPROACH: Add `updateReminderStatus` to Repo/DAO that uses @Update or specific query.
            
            repository.updateReminderStatus(updatedReminder)
            
            if (newActiveState) {
                geofenceHelper.addGeofence(updatedReminder)
            } else {
                geofenceHelper.removeGeofence(updatedReminder)
            }
        }
    }
}

