package com.zybooks.remindmev2.data.local.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "reminder_tag_cross_ref",
    primaryKeys = ["reminderId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = Reminder::class,
            parentColumns = ["id"],
            childColumns = ["reminderId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Tag::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["tagId"]), Index(value = ["reminderId"])]
)
data class ReminderTagCrossRef(
    val reminderId: Long,
    val tagId: Long
)

