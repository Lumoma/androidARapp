package com.example.abgabe.ui.states

sealed interface CatOverviewScreenUiState {
    data object GeneratingCats : CatOverviewScreenUiState

    data object AccessingDatabase : CatOverviewScreenUiState

    data object LoadingPictures : CatOverviewScreenUiState

    data class Content(
        val text: String,
        val onLoadClicked: () -> Unit
    ): CatOverviewScreenUiState

}
