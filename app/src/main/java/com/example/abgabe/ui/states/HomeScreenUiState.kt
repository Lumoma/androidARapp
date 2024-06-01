package com.example.abgabe.ui.states

sealed interface HomeScreenUiState {
    data object Loading : HomeScreenUiState

    data class Content(
        val text: String,
        val onLoadClicked: () -> Unit
    ): HomeScreenUiState

}
