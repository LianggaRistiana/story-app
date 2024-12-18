package com.dicoding.storyapp.data.paging

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dicoding.storyapp.data.local.UserModel
import com.dicoding.storyapp.data.local.UserPreference
import com.dicoding.storyapp.data.local.dataStore
import com.dicoding.storyapp.data.local.entity.StoryEntity
import com.dicoding.storyapp.data.local.room.StoryDatabase
import com.dicoding.storyapp.data.remote.request.LoginRequest
import com.dicoding.storyapp.data.remote.request.RegisterRequest
import com.dicoding.storyapp.data.remote.response.DetailStoryResponse
import com.dicoding.storyapp.data.remote.response.GeneralResponse
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.data.remote.response.LoginResponse
import com.dicoding.storyapp.data.remote.response.StoryResponse
import com.dicoding.storyapp.data.remote.retrofit.ApiService
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalPagingApi
@RunWith(AndroidJUnit4::class)
class StoryRemoteMediatorTest {

    private var mockApi: ApiService = FakeApiService()
    private var mockDb: StoryDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        StoryDatabase::class.java
    ).allowMainThreadQueries().build()
    private lateinit var mockUserPreference: UserPreference

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dataStore = context.dataStore
        mockUserPreference = UserPreference(dataStore)

        runTest {
            mockUserPreference.updateUserSession(
                UserModel(
                    userId = "dummy_id",
                    userName = "dummy_user",
                    token = "dummy_token",
                    isLogin = true
                )
            )
        }
    }


    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runTest {
        val remoteMediator = StoryRemoteMediator(
            mockDb,
            mockApi,
            mockUserPreference
        )
        val pagingState = PagingState<Int, StoryEntity>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }


    @After
    fun tearDown() {
        mockDb.clearAllTables()
    }
}


class FakeApiService : ApiService {
    override suspend fun login(request: LoginRequest): LoginResponse {
        TODO("Not yet implemented")
    }

    override suspend fun register(request: RegisterRequest): GeneralResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getStories(
        location: String,
        page: Int?,
        size: Int?,
        token: String
    ): StoryResponse {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 1..100) {
            val story = ListStoryItem(
                id = "story_$i",
                name = "Story Name $i",
                description = "Description for story $i",
                photoUrl = "https://example.com/photo_$i.jpg",
                createdAt = "2024-12-18T10:00:00Z",
                lat = -8.65 + i * 0.01,
                lon = 115.22 + i * 0.01
            )
            items.add(story)
        }

        // Jika page atau size null, kembalikan semua data
        if (page == null || size == null) {
            return StoryResponse(
                listStory = items,
                error = false,
                message = "Stories fetched successfully"
            )
        }
        val startIndex = (page - 1) * size
        val endIndex = (startIndex + size).coerceAtMost(items.size)

        val pagedItems = if (startIndex in items.indices) {
            items.subList(startIndex, endIndex)
        } else {
            emptyList()
        }

        return StoryResponse(
            listStory = pagedItems,
            error = false,
            message = "Stories fetched successfully"
        )
    }

    override suspend fun getDetailStoryById(id: String, token: String): DetailStoryResponse {
        TODO("Not yet implemented")
    }

    override suspend fun addStory(
        file: MultipartBody.Part,
        description: RequestBody,
        token: String
    ): GeneralResponse {
        TODO("Not yet implemented")
    }
}