package com.example.bbtraveling.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.bbtraveling.data.local.TravelDatabase
import com.example.bbtraveling.domain.UserProfile
import java.time.LocalDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class RoomUserProfileRepositoryTest {

    private lateinit var database: TravelDatabase
    private lateinit var repository: RoomUserProfileRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, TravelDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = RoomUserProfileRepository(database.userProfileDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun upsertUser_persistsAndMapsDomainProfile() = runBlocking {
        repository.upsertUser(profile(username = "test01"))

        val storedProfile = repository.getUser("test@example.com")

        assertEquals("test01", storedProfile?.username)
        assertEquals(LocalDate.of(2000, 1, 1), storedProfile?.birthdate)
        assertEquals("Spain", storedProfile?.country)
    }

    @Test
    fun observeUser_emitsUpdatesForProfile() = runBlocking {
        repository.upsertUser(profile(username = "test01"))
        repository.upsertUser(profile(username = "test02"))

        val observedProfile = repository.observeUser("test@example.com").first()

        assertEquals("test02", observedProfile?.username)
    }

    @Test
    fun deleteUser_removesProfile() = runBlocking {
        repository.upsertUser(profile(username = "test01"))

        repository.deleteUser("test@example.com")

        assertNull(repository.getUser("test@example.com"))
    }

    private fun profile(username: String): UserProfile {
        return UserProfile(
            login = "test@example.com",
            username = username,
            birthdate = LocalDate.of(2000, 1, 1),
            address = "Test street 1",
            country = "Spain",
            phone = "600000000",
            acceptsReceiveEmails = true
        )
    }
}

