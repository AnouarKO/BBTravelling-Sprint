package com.example.bbtraveling.data.repository

import com.example.bbtraveling.data.local.dao.UserProfileDao
import com.example.bbtraveling.data.local.entity.UserProfileEntity
import com.example.bbtraveling.domain.UserProfile
import com.example.bbtraveling.domain.repository.UserProfileRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class RoomUserProfileRepository @Inject constructor(
    private val userProfileDao: UserProfileDao
) : UserProfileRepository {

    override suspend fun getUser(login: String): UserProfile? {
        return userProfileDao.getUser(login)?.toDomain()
    }

    override fun observeUser(login: String): Flow<UserProfile?> {
        return userProfileDao.observeUser(login).map { entity -> entity?.toDomain() }
    }

    override suspend fun upsertUser(profile: UserProfile) {
        userProfileDao.upsertUser(profile.toEntity())
    }

    override suspend fun deleteUser(login: String) {
        userProfileDao.deleteUser(login)
    }

    private fun UserProfileEntity.toDomain(): UserProfile {
        return UserProfile(
            login = login,
            username = username,
            birthdate = birthdate,
            address = address,
            country = country,
            phone = phone,
            acceptsReceiveEmails = acceptsReceiveEmails
        )
    }

    private fun UserProfile.toEntity(): UserProfileEntity {
        return UserProfileEntity(
            login = login,
            username = username,
            birthdate = birthdate,
            address = address,
            country = country,
            phone = phone,
            acceptsReceiveEmails = acceptsReceiveEmails
        )
    }
}
