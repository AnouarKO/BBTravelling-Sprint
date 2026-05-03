package com.example.bbtraveling.ui.viewmodel

import com.example.bbtraveling.domain.AuthRegistration
import com.example.bbtraveling.domain.AuthResult
import com.example.bbtraveling.domain.AuthUser
import com.example.bbtraveling.domain.UserProfile
import com.example.bbtraveling.domain.UserSettings
import com.example.bbtraveling.domain.repository.AuthRepository
import com.example.bbtraveling.domain.repository.UserProfileRepository
import com.example.bbtraveling.domain.repository.UserSettingsRepository
import com.example.bbtraveling.testutil.MainDispatcherRule
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var settingsRepository: FakeUserSettingsRepository
    private lateinit var authRepository: FakeAuthRepository
    private lateinit var userProfileRepository: FakeUserProfileRepository
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        settingsRepository = FakeUserSettingsRepository()
        authRepository = FakeAuthRepository()
        userProfileRepository = FakeUserProfileRepository()
    }

    @Test
    fun settings_forAuthenticatedUserExposeLocalProfileData() {
        authRepository.currentUserFlow.value = AuthUser(
            userId = "uid",
            login = "test@example.com",
            email = "test@example.com"
        )
        userProfileRepository.upsertProfile(
            profile(
                login = "test@example.com",
                username = "test01",
                birthdate = LocalDate.of(2005, 5, 18)
            )
        )

        viewModel = createViewModel()

        assertEquals("test01", viewModel.settings.value.username)
        assertEquals("18/05/2005", viewModel.settings.value.dateOfBirth)
    }

    @Test
    fun updateDateOfBirth_updatesAuthenticatedUserProfile() {
        authRepository.currentUserFlow.value = AuthUser(
            userId = "uid",
            login = "test@example.com",
            email = "test@example.com"
        )
        userProfileRepository.upsertProfile(
            profile(
                login = "test@example.com",
                username = "test01",
                birthdate = LocalDate.of(2005, 5, 18)
            )
        )
        viewModel = createViewModel()

        viewModel.updateDateOfBirth("01/01/2000")

        assertEquals(LocalDate.of(2000, 1, 1), userProfileRepository.profiles.value["test@example.com"]?.birthdate)
        assertEquals("01/01/2000", viewModel.settings.value.dateOfBirth)
    }

    @Test
    fun updateLanguage_keepsAuthenticatedProfileFields() {
        authRepository.currentUserFlow.value = AuthUser(
            userId = "uid",
            login = "test@example.com",
            email = "test@example.com"
        )
        userProfileRepository.upsertProfile(
            profile(
                login = "test@example.com",
                username = "test01",
                birthdate = LocalDate.of(2005, 5, 18)
            )
        )
        viewModel = createViewModel()

        viewModel.updateLanguage("ca")

        assertEquals("ca", viewModel.settings.value.languageTag)
        assertEquals("test01", viewModel.settings.value.username)
        assertEquals("18/05/2005", viewModel.settings.value.dateOfBirth)
    }

    @Test
    fun settings_withoutAuthenticatedUserUseStoredPreferences() {
        viewModel = createViewModel()

        viewModel.updateUsername("local-user")
        viewModel.updateDateOfBirth("01/01/2001")

        assertEquals("local-user", viewModel.settings.value.username)
        assertEquals("01/01/2001", viewModel.settings.value.dateOfBirth)
    }

    private fun createViewModel(): SettingsViewModel {
        return SettingsViewModel(
            repository = settingsRepository,
            authRepository = authRepository,
            userProfileRepository = userProfileRepository
        )
    }

    private fun profile(login: String, username: String, birthdate: LocalDate): UserProfile {
        return UserProfile(
            login = login,
            username = username,
            birthdate = birthdate,
            address = "Test street 1",
            country = "Spain",
            phone = "600000000",
            acceptsReceiveEmails = true
        )
    }

    private class FakeUserSettingsRepository : UserSettingsRepository {
        private val mutableSettings = MutableStateFlow(UserSettings(languageTag = "en"))
        override val settings: StateFlow<UserSettings> = mutableSettings.asStateFlow()

        override fun readStoredLanguageTag(): String = mutableSettings.value.languageTag

        override fun hasAcceptedTerms(): Boolean = mutableSettings.value.termsAccepted

        override fun updateUsername(value: String) {
            mutableSettings.value = mutableSettings.value.copy(username = value)
        }

        override fun updateDateOfBirth(value: String) {
            mutableSettings.value = mutableSettings.value.copy(dateOfBirth = value)
        }

        override fun updateDarkMode(enabled: Boolean) {
            mutableSettings.value = mutableSettings.value.copy(darkMode = enabled)
        }

        override fun updateLanguage(languageTag: String) {
            mutableSettings.value = mutableSettings.value.copy(languageTag = languageTag)
        }

        override fun setTermsAccepted(accepted: Boolean) {
            mutableSettings.value = mutableSettings.value.copy(termsAccepted = accepted)
        }
    }

    private class FakeAuthRepository : AuthRepository {
        val currentUserFlow = MutableStateFlow<AuthUser?>(null)
        override val currentUser: StateFlow<AuthUser?> = currentUserFlow

        override suspend fun login(email: String, password: String): AuthResult = AuthResult.Success()

        override suspend fun register(registration: AuthRegistration): AuthResult = AuthResult.Success()

        override suspend fun recoverPassword(email: String): AuthResult = AuthResult.Success()

        override suspend fun logout() {
            currentUserFlow.value = null
        }
    }

    private class FakeUserProfileRepository : UserProfileRepository {
        val profiles = MutableStateFlow<Map<String, UserProfile>>(emptyMap())

        fun upsertProfile(profile: UserProfile) {
            profiles.value = profiles.value + (profile.login to profile)
        }

        override suspend fun getUser(login: String): UserProfile? = profiles.value[login]

        override fun observeUser(login: String): Flow<UserProfile?> {
            return profiles.map { currentProfiles -> currentProfiles[login] }
        }

        override suspend fun upsertUser(profile: UserProfile) {
            upsertProfile(profile)
        }

        override suspend fun deleteUser(login: String) {
            profiles.value = profiles.value - login
        }
    }
}
