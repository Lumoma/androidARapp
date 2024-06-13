package com.example.abgabe.ui.states

import com.example.abgabe.data.local.Cat

sealed interface DetailUiState {
    data object Loading : DetailUiState

    data class Edit(
        val editCat: Cat,
        val onSaveChanges: () -> Unit,
        val onEditName: (String) -> Unit,
        val onEditBreed: (String) -> Unit,
        val onEditTemperament: (String) -> Unit,
        val onEditOrigin: (String) -> Unit,
        val onEditLifeExpectancy: (String) -> Unit,
    ) : DetailUiState
    data class Content(val cat: Cat, val onEdit: () -> Unit) : DetailUiState
}