package com.zybooks.remindmev2.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Reminder::class, Tag::class, ReminderTagCrossRef::class, UserSettings::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao
    abstract fun settingsDao(): SettingsDao
}

