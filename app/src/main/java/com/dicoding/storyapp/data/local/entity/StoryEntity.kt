package com.dicoding.storyapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "story")
data class StoryEntity(
    @PrimaryKey
    @ColumnInfo("id")
    val id: String,

    @ColumnInfo("name")
    val name: String? = null,

    @ColumnInfo("photoUrl")
    val photoUrl: String? = null,

    @ColumnInfo("createdAt")
    val createdAt: String? = null,

    @ColumnInfo("description")
    val description: String? = null,

    @ColumnInfo("lon")
    val lon: Double? = null,

    @ColumnInfo("lat")
    val lat: Double? = null
)