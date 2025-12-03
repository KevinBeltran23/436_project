package com.zybooks.remindmev2.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zybooks.remindmev2.data.local.database.Reminder
import com.zybooks.remindmev2.data.repository.ReminderRepository
import com.zybooks.remindmev2.util.GeofenceHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddEditReminderUiState(
    val title: String = "",
    val locationName: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val geofenceRadius: Float = 100f,
    val isProximityOnArrival: Boolean = true,
    val notes: String = "",
    val tags: String = "", // Comma separated for input
    val isActive: Boolean = true,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false
)

class AddEditReminderViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: ReminderRepository,
    private val geofenceHelper: GeofenceHelper
) : ViewModel() {

    private val reminderId: Long? = savedStateHandle.get<Long>("reminderId")?.let { if (it == -1L) null else it }

    private val _uiState = MutableStateFlow(AddEditReminderUiState())
    val uiState: StateFlow<AddEditReminderUiState> = _uiState.asStateFlow()

    init {
        if (reminderId != null) {
            loadReminder(reminderId)
        }
    }

    private fun loadReminder(id: Long) {
        viewModelScope.launch {
            val reminderWithTags = repository.getReminder(id)
            if (reminderWithTags != null) {
                _uiState.update {
                    it.copy(
                        title = reminderWithTags.reminder.title,
                        locationName = reminderWithTags.reminder.locationName,
                        latitude = reminderWithTags.reminder.latitude,
                        longitude = reminderWithTags.reminder.longitude,
                        geofenceRadius = reminderWithTags.reminder.geofenceRadius,
                        isProximityOnArrival = reminderWithTags.reminder.proximityType,
                        notes = reminderWithTags.reminder.notes ?: "",
                        tags = reminderWithTags.tags.joinToString(", ") { tag -> tag.name },
                        isActive = reminderWithTags.reminder.isActive
                    )
                }
            }
        }
    }

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun updateLocation(lat: Double, lng: Double, name: String) {
        _uiState.update {
            it.copy(latitude = lat, longitude = lng, locationName = name)
        }
    }
    
    fun updateNotes(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }

    fun updateTags(tags: String) {
        _uiState.update { it.copy(tags = tags) }
    }

    fun updateRadius(radius: Float) {
        _uiState.update { it.copy(geofenceRadius = radius) }
    }

    fun updateProximityType(onArrival: Boolean) {
        _uiState.update { it.copy(isProximityOnArrival = onArrival) }
    }

    fun saveReminder() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val state = uiState.value
            val reminder = Reminder(
                id = reminderId ?: 0,
                title = state.title,
                locationName = state.locationName,
                latitude = state.latitude,
                longitude = state.longitude,
                geofenceRadius = state.geofenceRadius,
                proximityType = state.isProximityOnArrival,
                notes = state.notes,
                isActive = state.isActive
            )
            
            val tagList = state.tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            
            val savedId: Long
            if (reminderId == null) {
                savedId = repository.addReminder(reminder, tagList)
            } else {
                repository.updateReminder(reminder, tagList)
                savedId = reminderId
            }
            
            if (state.isActive) {
                geofenceHelper.addGeofence(reminder.copy(id = savedId))
            } else {
                geofenceHelper.removeGeofence(reminder.copy(id = savedId))
            }
            
            _uiState.update { it.copy(isSaving = false, isSaved = true) }
        }
    }
}

