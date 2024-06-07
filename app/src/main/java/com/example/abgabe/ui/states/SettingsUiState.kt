package com.example.abgabe.ui.states

sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data object DumpingDatabase : SettingsUiState
    data class UpdatingDatabase(val currentCatsAmount: Int, val toGenerateAmount: Int) : SettingsUiState
    data class Content(val currentCatsAmount: Int) : SettingsUiState
    data object EmptyDatabase : SettingsUiState
    data object Error : SettingsUiState
}