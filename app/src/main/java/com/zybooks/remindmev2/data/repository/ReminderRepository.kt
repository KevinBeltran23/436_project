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
        val reminderId = reminderDao.insertReminder(reminder) 
        
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
    
    suspend fun updateReminderStatus(reminder: Reminder) {
        // Using insert with REPLACE strategy updates the row without affecting foreign keys if the ID is the same
        // and we don't touch the tags table or cross-ref table here.
        // WARNING: If onConflict = REPLACE actually does DELETE+INSERT, it WILL cascade delete tags.
        // To be safe, we should use a specific @Update query in DAO.
        reminderDao.updateReminder(reminder)
    }

    suspend fun deleteReminder(reminder: Reminder) {
        reminderDao.deleteReminder(reminder)
    }
}
