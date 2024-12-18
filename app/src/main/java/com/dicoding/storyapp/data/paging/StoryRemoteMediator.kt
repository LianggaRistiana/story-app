package com.dicoding.storyapp.data.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.dicoding.storyapp.data.local.pref.UserPreference
import com.dicoding.storyapp.data.local.entity.RemoteKeys
import com.dicoding.storyapp.data.local.entity.StoryEntity
import com.dicoding.storyapp.data.local.room.StoryDatabase
import com.dicoding.storyapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val database: StoryDatabase,
    private val apiService: ApiService,
    private val userPreference: UserPreference
) : RemoteMediator<Int, StoryEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH ->{
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val token = userPreference.getUserSession().first().token
            val responseData = apiService.getStories(
                location = "0",
                page = page,
                size = state.config.pageSize,
                token = "Bearer $token"
            )

            Log.d("StoryPagingSource", "Page: $page, Size: ${state.config.pageSize}")
            val endOfPaginationReached = responseData.listStory?.isEmpty() ?: true
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao().deleteRemoteKeys()
                    database.storyDao().deleteAll()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = responseData
                    .listStory?.map {
                        RemoteKeys(id = it!!.id, prevKey = prevKey, nextKey = nextKey)
                    }
                if (keys != null) {
                    database.remoteKeysDao().insertAll(keys)
                }

                val storyList = responseData.listStory?.map {
                    StoryEntity(
                        id = it?.id!!,
                        name = it.name,
                        photoUrl = it.photoUrl,
                        createdAt = it.createdAt,
                        description = it.description,
                        lon = it.lon,
                        lat = it.lat
                    )
                }

                if (storyList != null) {
                    database.storyDao().insertQuote(storyList)
                }
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }
    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }
    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }


    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}