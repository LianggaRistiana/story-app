package com.dicoding.storyapp.data.local.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.storyapp.data.local.entity.StoryEntity


@Dao
interface StoryDao {
    @Query("SELECT * FROM story")
    fun getStory(): PagingSource<Int, StoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(quote: List<StoryEntity>)

    @Query("DELETE FROM story")
    suspend fun deleteAll()
}