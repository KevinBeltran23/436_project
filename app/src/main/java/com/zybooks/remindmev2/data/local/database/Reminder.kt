package com.zybooks.remindmev2.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val locationName: String?,
    val latitude: Double,
    val longitude: Double,
    val geofenceRadius: Float = 100f,
    val triggerOnArrival: Boolean = true,
    val triggerOnDeparture: Boolean = false,
    val notes: String?,
    val isActive: Boolean = true
)
