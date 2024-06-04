package com.example.abgabe.ui.states

import com.example.abgabe.data.local.Cat

sealed interface CatOverviewUiState {
    object Loading : CatOverviewUiState
    data class Success(val cats: List<Cat>) : CatOverviewUiState
    object EmptyDatabase : CatOverviewUiState
}