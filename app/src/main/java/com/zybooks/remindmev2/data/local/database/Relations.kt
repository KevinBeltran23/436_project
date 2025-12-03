package com.zybooks.remindmev2.data.local.database

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class ReminderWithTags(
    @Embedded val reminder: Reminder,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ReminderTagCrossRef::class,
            parentColumn = "reminderId",
            entityColumn = "tagId"
        )
    )
    val tags: List<Tag>
)

