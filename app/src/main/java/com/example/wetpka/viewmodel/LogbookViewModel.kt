package com.example.wetpka.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wetpka.data.AppDatabase
import com.example.wetpka.data.CatchRecord
import com.example.wetpka.data.SessionStore
import com.example.wetpka.data.WetpkaRepository
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

data class LogbookUiState(
    val isLoading: Boolean = true,
    val loggedInUsername: String? = null,
    val useLocal: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
class LogbookViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = WetpkaRepository(AppDatabase.getDatabase(application))

    private val _uiState = MutableStateFlow(LogbookUiState())
    val uiState: StateFlow<LogbookUiState> = _uiState.asStateFlow()

    private val currentOwner = MutableStateFlow<String?>(null)
    val catches: StateFlow<List<CatchRecord>> = currentOwner
        .flatMapLatest { owner ->
            if (owner == null) flowOf(emptyList()) else repository.getCatchesByOwner(owner)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        refreshSession()
    }

    fun refreshSession() {
        viewModelScope.launch(Dispatchers.IO) {
            val savedId = SessionStore.getLoggedInUserId(getApplication())
            val user = if (savedId != -1) repository.findUserById(savedId) else null
            val username = user?.username
            _uiState.value = LogbookUiState(
                isLoading = false,
                loggedInUsername = username,
                useLocal = false
            )
            currentOwner.value = username
        }
    }

    fun chooseLocal() {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            loggedInUsername = null,
            useLocal = true
        )
        currentOwner.value = "localuser"
    }

    fun leaveLocalMode() {
        _uiState.value = _uiState.value.copy(useLocal = false)
        currentOwner.value = null
    }

    fun logout() {
        SessionStore.clearLoggedInUser(getApplication())
        _uiState.value = LogbookUiState(isLoading = false)
        currentOwner.value = null
    }

    fun saveCatch(record: CatchRecord, isEditMode: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isEditMode) {
                repository.updateCatch(record)
            } else {
                repository.insertCatch(record)
            }
        }
    }

    fun deleteCatch(record: CatchRecord) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCatch(record)
        }
    }
}


