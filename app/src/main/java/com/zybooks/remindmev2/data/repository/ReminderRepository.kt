package com.zybooks.remindmev2.data.repository

import com.zybooks.remindmev2.data.local.database.Reminder
import com.zybooks.remindmev2.data.local.database.ReminderDao
import com.zybooks.remindmev2.data.local.database.ReminderTagCrossRef
import com.zybooks.remindmev2.data.local.database.ReminderWithTags
import com.zybooks.remindmev2.data.local.database.Tag
import kotlinx.coroutines.flow.Flow

class ReminderRepository(private val reminderDao: ReminderDao) {
    val allReminders: Flow<List<ReminderWithTags>> = reminderDao.getRemindersWithTags()
    val allTags: Flow<List<Tag>> = reminderDao.getAllTags()

    suspend fun getReminder(id: Long): ReminderWithTags? {
        return reminderDao.getReminderWithTagsById(id)
    }

    suspend fun addReminder(reminder: Reminder, tags: List<String>): Long {
        val reminderId = reminderDao.insertReminder(reminder)
        
        tags.forEach { tagName ->
            var tag = reminderDao.getTagByName(tagName)
            if (tag == null) {
                val tagId = reminderDao.insertTag(Tag(name = tagName))
                tag = Tag(tagId, tagName)
            }
            
            reminderDao.insertReminderTagCrossRef(ReminderTagCrossRef(reminderId, tag!!.id))
        }
        return reminderId
    }
    
    suspend fun updateReminder(reminder: Reminder, tags: List<String>) {
        // If using REPLACE strategy in DAO, insert acts as update if ID exists.
        // However, we need to handle tags.
        // Easiest is to delete old cross-refs and add new ones.
        
        val reminderId = reminderDao.insertReminder(reminder) // Returns ID.
        
        // We should probably clear old tags for this reminder first.
        // But if we used REPLACE, did it cascade delete? 
        // If ID is same, REPLACE updates the row. It does NOT delete and re-insert in a way that triggers CASCADE DELETE of foreign keys usually, unless the primary key is modified?
        // Actually, REPLACE on SQLite corresponds to DELETE + INSERT. So yes, it might trigger cascade if foreign keys are enabled.
        // But Room's @Insert(onConflict = REPLACE) might behave differently depending on implementation.
        // Safer to manually clear tags.
        
        reminderDao.deleteReminderTags(reminderId)
        
        tags.forEach { tagName ->
            var tag = reminderDao.getTagByName(tagName)
            if (tag == null) {
                val tagId = reminderDao.insertTag(Tag(name = tagName))
                tag = Tag(tagId, tagName)
            }
            reminderDao.insertReminderTagCrossRef(ReminderTagCrossRef(reminderId, tag!!.id))
        }
    }

    suspend fun deleteReminder(reminder: Reminder) {
        reminderDao.deleteReminder(reminder)
    }
}

