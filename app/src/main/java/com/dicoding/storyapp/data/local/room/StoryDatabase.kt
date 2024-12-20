package com.dicoding.storyapp.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.storyapp.data.local.entity.RemoteKeys
import com.dicoding.storyapp.data.local.entity.StoryEntity

@Database(
    entities = [StoryEntity::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)

abstract class StoryDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        const val DATABASE_NAME = "story_database"

        @Volatile
        private var INSTANCE: StoryDatabase? = null

        @JvmStatic
        fun getInstance(context: Context): StoryDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    StoryDatabase::class.java, DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}