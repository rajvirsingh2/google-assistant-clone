package com.example.googleassistantclone.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assistant_message_table")
data class Assistant(
    @PrimaryKey(autoGenerate = true)
    var assistantId: Long = 0L,

    @ColumnInfo(name = "assistant_message")
    var assistantMessage: String = "DEFAULT_MESSAGE",

    @ColumnInfo(name = "human_message")
    var humanMessage: String = "DEFAULT_MESSAGE"
)