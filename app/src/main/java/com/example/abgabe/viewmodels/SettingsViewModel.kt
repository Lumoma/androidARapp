package com.example.abgabe.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.abgabe.data.local.AppDatabase
import com.example.abgabe.data.remote.getCats
import com.example.abgabe.ui.states.SettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val database: AppDatabase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val initialAmount = getDatabaseSize()
            _uiState.value = if (initialAmount > 0) {
                SettingsUiState.Content(initialAmount)
            } else {
                SettingsUiState.DatabaseEmpty
            }
        }
    }

    fun onDumpDatabaseClicked() {
        viewModelScope.launch {
            _uiState.value = SettingsUiState.DumpingDatabase
            withContext(Dispatchers.IO) {
                database.catDao().deleteAll()
            }
            _uiState.value = SettingsUiState.DatabaseEmpty
        }
    }

    fun onGenerateCatsClicked(amount: Int, context: Context) {
        viewModelScope.launch {
            _uiState.value = SettingsUiState.Loading
            withContext(Dispatchers.IO) {
                getCats(if (amount > 0) amount else 10, context)
                    .forEach { database.catDao().insert(it) }
            }
            _uiState.value = SettingsUiState.Content(getDatabaseSize())
        }
    }

    private suspend fun getDatabaseSize(): Int {
        return withContext(Dispatchers.IO) {
            database.catDao().getAll().size
        }
    }
}