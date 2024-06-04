package com.example.abgabe.ui.states

sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data object DumpingDatabase : SettingsUiState
    data class Content(val amount: Int) : SettingsUiState
    data object DatabaseEmpty : SettingsUiState
}