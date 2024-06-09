package com.example.abgabe.ui.states

import com.example.abgabe.data.local.Cat

sealed interface OverviewUiState {
    data object Loading : OverviewUiState
    data class Content(val cats: List<Cat>) : OverviewUiState
    data class UpdateCat(val cat: Cat) : OverviewUiState
    data object UpdateCatList : OverviewUiState
    data object EmptyDatabase : OverviewUiState
    data class AddCat(val pictureUrl: String) : OverviewUiState
    data object DeleteCat : OverviewUiState
    data class Error(val message: String) : OverviewUiState
}