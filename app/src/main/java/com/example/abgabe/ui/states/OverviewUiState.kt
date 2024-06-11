package com.example.abgabe.ui.states

import com.example.abgabe.data.local.Cat

sealed interface OverviewUiState {
    data object Loading : OverviewUiState
    data class Content(val cats: List<Cat>, val onAddCat: (cat: Cat) -> Unit) : OverviewUiState
    data object EmptyDatabase : OverviewUiState
    data class AddCat(val pictureUrl: String, val generateNewPictureURL: () -> Unit , val onSaveCat: (cat: Cat) -> Unit) : OverviewUiState
}