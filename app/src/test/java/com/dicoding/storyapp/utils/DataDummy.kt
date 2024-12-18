package com.dicoding.storyapp.utils

import com.dicoding.storyapp.data.local.entity.StoryEntity

object DataDummy {
    fun generateDummyStoryEntity(): List<StoryEntity> {
        val storyList = ArrayList<StoryEntity>()

        for (i in 0..10) {
            val story = StoryEntity(
                id = i.toString(),
                name = "name + $i",
                photoUrl = "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                createdAt = "2023-01-01T00:00:00Z",
                description = "description + $i",
                lon = i.toDouble(),
                lat = i.toDouble()
            )
            storyList.add(story)
        }
        return storyList
    }
}