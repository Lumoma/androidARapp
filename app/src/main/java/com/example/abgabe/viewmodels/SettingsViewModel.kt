package com.example.abgabe.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.abgabe.data.local.AppDatabase
import com.example.abgabe.ui.states.SettingsUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val database: AppDatabase
) : ViewModel() {
    private val internalAmountFlow = MutableStateFlow<Int?>(null)
    val uiState: StateFlow<SettingsUIState> = internalAmountFlow.map { amount ->
        SettingsUIState.Content(
            amount = getDatabaseSize(),
            onDumpDatabaseClicked = {
                viewModelScope.launch {
                    database.catDao().deleteAll()
                    internalAmountFlow.value = 0
                }
            },
            onLoadDatabaseClicked = {
                viewModelScope.launch {
                    //TODO
                }
            }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), SettingsUIState.DumpDatabase)

    private suspend fun getDatabaseSize(): Int {
        return withContext(Dispatchers.IO) {
            database.catDao().getAll().size
        }
    }
}