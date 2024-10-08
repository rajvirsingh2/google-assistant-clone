package com.example.googleassistantclone.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface AssistantDao {

    @Insert
    fun insert(assistant: Assistant)

    @Update
    fun update(assistant: Assistant)

    @Query("DELETE FROM assistant_message_table")
    fun clear()

    @Query("SELECT * FROM assistant_message_table ORDER BY assistantId DESC")
    fun getAllMessages(): LiveData<List<Assistant>>

    @Query("SELECT * FROM assistant_message_table WHERE assistantId=:key")
    fun getKey(key:Long): Assistant?

    @Query("SELECT * FROM assistant_message_table ORDER BY assistantId DESC LIMIT 1")
    fun getCurrentMessage(): Assistant?

}