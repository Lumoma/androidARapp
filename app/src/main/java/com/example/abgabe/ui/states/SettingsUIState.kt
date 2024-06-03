package com.example.abgabe.ui.states

sealed interface SettingsUIState {
    data class Content(
        val amount: Int,
        val onDumpDatabaseClicked: () -> Unit,
        val onLoadDatabaseClicked: () -> Unit
    ): SettingsUIState
    data object DumpDatabase : SettingsUIState
    data class LoadDatabase(val amount: Int): SettingsUIState
    data object DatabaseSuccess : SettingsUIState
}