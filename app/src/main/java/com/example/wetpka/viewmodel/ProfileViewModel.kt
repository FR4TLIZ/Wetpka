package com.example.wetpka.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wetpka.data.AppDatabase
import com.example.wetpka.data.CatchRecord
import com.example.wetpka.data.SessionStore
import com.example.wetpka.data.WetpkaRepository
import com.example.wetpka.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = true,
    val loggedInUser: User? = null,
    val loginError: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = WetpkaRepository(AppDatabase.getDatabase(application))

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    val catches: StateFlow<List<CatchRecord>> = _uiState
        .flatMapLatest { state ->
            val username = state.loggedInUser?.username
            if (username == null) flowOf(emptyList()) else repository.getCatchesByOwner(username)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        loadSession()
    }

    fun loadSession() {
        viewModelScope.launch(Dispatchers.IO) {
            val savedId = SessionStore.getLoggedInUserId(getApplication())
            val user = if (savedId != -1) repository.findUserById(savedId) else null
            _uiState.value = ProfileUiState(
                isLoading = false,
                loggedInUser = user,
                loginError = null
            )
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = repository.findUserByUsername(username.trim())
            if (user != null && user.passwordHash == hashPassword(password)) {
                SessionStore.saveLoggedInUserId(getApplication(), user.id)
                _uiState.value = ProfileUiState(
                    isLoading = false,
                    loggedInUser = user,
                    loginError = null
                )
            } else {
                _uiState.value = _uiState.value.copy(loginError = "Nieprawidłowe dane logowania.")
            }
        }
    }

    fun clearLoginError() {
        _uiState.value = _uiState.value.copy(loginError = null)
    }

    fun logout() {
        SessionStore.clearLoggedInUser(getApplication())
        _uiState.value = ProfileUiState(isLoading = false)
    }

    private fun hashPassword(password: String): String {
        val bytes = java.security.MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}


