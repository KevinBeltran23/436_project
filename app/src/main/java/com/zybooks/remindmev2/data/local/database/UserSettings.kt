package com.zybooks.remindmev2.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey
    val id: Long = 0,
    val darkMode: Boolean = false,
    val pushNotifications: Boolean = true,
    val locationPermissions: Boolean = false
)

