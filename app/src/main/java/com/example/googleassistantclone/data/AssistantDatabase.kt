package com.example.googleassistantclone.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [Assistant::class], version = 1, exportSchema = false)
abstract class AssistantDatabase: RoomDatabase() {

    abstract val assistantDao: AssistantDao
    companion object{

        @Volatile
        private var INSTANCE: AssistantDatabase?= null
        fun getInstance(context: Context): AssistantDatabase{
            synchronized(this){
                var instance = INSTANCE
                if(instance == null){
                    instance = Room.databaseBuilder(context,
                        AssistantDatabase::class.java,
                        "assistant_message_database"
                    ).allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }

    }

}