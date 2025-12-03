package com.zybooks.remindmev2

import android.app.Application
import androidx.room.Room
import com.zybooks.remindmev2.data.local.database.AppDatabase
import com.zybooks.remindmev2.data.repository.ReminderRepository
import com.zybooks.remindmev2.data.repository.SettingsRepository

class RemindMeApplication : Application() {
    lateinit var database: AppDatabase
    lateinit var repository: ReminderRepository
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "remindme_db"
        ).build()
        repository = ReminderRepository(database.reminderDao())
        settingsRepository = SettingsRepository(database.settingsDao())
    }
}

