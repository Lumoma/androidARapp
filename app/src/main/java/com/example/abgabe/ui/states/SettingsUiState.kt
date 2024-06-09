package com.example.abgabe.ui.states

sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Content(val currentCatsAmount: Int, val currentRandomCatPic: String) : SettingsUiState
    data object EmptyDatabase : SettingsUiState
    data object Error : SettingsUiState
}