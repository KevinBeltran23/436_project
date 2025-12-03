package com.zybooks.remindmev2.data.repository

import com.zybooks.remindmev2.data.local.database.SettingsDao
import com.zybooks.remindmev2.data.local.database.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(private val settingsDao: SettingsDao) {
    val settings: Flow<UserSettings> = settingsDao.getSettings().map { it ?: UserSettings() }

    suspend fun updateSettings(settings: UserSettings) {
        settingsDao.insertSettings(settings)
    }
}

