package com.example.bbtraveling.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.bbtraveling.domain.UserProfile
import com.example.bbtraveling.domain.UserSettings
import com.example.bbtraveling.domain.repository.AuthRepository
import com.example.bbtraveling.domain.repository.UserProfileRepository
import com.example.bbtraveling.domain.repository.UserSettingsRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: UserSettingsRepository,
    private val authRepository: AuthRepository,
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val _settings = MutableStateFlow(repository.settings.value)
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    private var baseSettings = repository.settings.value
    private var activeUser = authRepository.currentUser.value
    private var activeUserProfile: UserProfile? = null
    private var profileJob: Job? = null

    init {
        viewModelScope.launch {
            repository.settings.collect { storedSettings ->
                baseSettings = storedSettings
                publishSettings()
            }
        }

        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                activeUser = user
                activeUserProfile = null
                profileJob?.cancel()
                profileJob = null
                publishSettings()

                if (user != null) {
                    profileJob = launch {
                        userProfileRepository.observeUser(user.login).collect { profile ->
                            activeUserProfile = profile
                            publishSettings()
                        }
                    }
                }
            }
        }
    }

    fun updateUsername(value: String) {
        viewModelScope.launch {
            runCatching {
                val profile = activeUser?.login?.let { login -> userProfileRepository.getUser(login) }
                if (profile == null) {
                    repository.updateUsername(value.trim())
                } else {
                    userProfileRepository.upsertUser(profile.copy(username = value.trim()))
                }
            }.onSuccess {
                Log.i(TAG, "username updated")
            }.onFailure { error ->
                Log.w(TAG, "username update failed", error)
            }
        }
    }

    fun updateDateOfBirth(value: String) {
        viewModelScope.launch {
            runCatching {
                val profile = activeUser?.login?.let { login -> userProfileRepository.getUser(login) }
                val parsedDate = runCatching { LocalDate.parse(value, PREFERENCE_DATE_FORMAT) }.getOrNull()
                if (profile == null || parsedDate == null) {
                    repository.updateDateOfBirth(value)
                } else {
                    userProfileRepository.upsertUser(profile.copy(birthdate = parsedDate))
                }
            }.onSuccess {
                Log.i(TAG, "dateOfBirth updated")
            }.onFailure { error ->
                Log.w(TAG, "dateOfBirth update failed", error)
            }
        }
    }

    fun updateDarkMode(enabled: Boolean) {
        repository.updateDarkMode(enabled)
        Log.i(TAG, "dark mode updated: $enabled")
    }

    fun updateLanguage(languageTag: String) {
        repository.updateLanguage(languageTag)
        Log.i(TAG, "language updated: $languageTag")
    }

    fun hasAcceptedTerms(): Boolean = repository.hasAcceptedTerms()

    fun acceptTerms() {
        repository.setTermsAccepted(true)
        Log.i(TAG, "terms accepted")
    }

    private fun publishSettings() {
        val profile = activeUserProfile
        _settings.value = if (profile == null) {
            baseSettings
        } else {
            baseSettings.copy(
                username = profile.username,
                dateOfBirth = profile.birthdate?.format(PREFERENCE_DATE_FORMAT).orEmpty()
            )
        }
    }

    companion object {
        private const val TAG = "SettingsViewModel"
        private val PREFERENCE_DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    }
}
