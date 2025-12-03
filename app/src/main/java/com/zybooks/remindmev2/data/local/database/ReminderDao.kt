package com.zybooks.remindmev2.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Transaction
    @Query("SELECT * FROM reminders")
    fun getRemindersWithTags(): Flow<List<ReminderWithTags>>

    @Transaction
    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getReminderWithTagsById(id: Long): ReminderWithTags?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: Reminder): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTag(tag: Tag): Long

    @Query("SELECT * FROM tags WHERE name = :name")
    suspend fun getTagByName(name: String): Tag?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertReminderTagCrossRef(crossRef: ReminderTagCrossRef)

    @Delete
    suspend fun deleteReminder(reminder: Reminder)

    @Query("DELETE FROM reminder_tag_cross_ref WHERE reminderId = :reminderId")
    suspend fun deleteReminderTags(reminderId: Long)

    @Query("SELECT * FROM tags")
    fun getAllTags(): Flow<List<Tag>>
}

