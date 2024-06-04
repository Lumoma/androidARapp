package com.example.abgabe.ui.states

sealed interface CatOverviewUiState {
    data object AccessingDatabase : CatOverviewUiState
    data object LoadingPictures : CatOverviewUiState

    data class Content(
        val text: String,
        val onLoadClicked: () -> Unit
    ): CatOverviewUiState

}
