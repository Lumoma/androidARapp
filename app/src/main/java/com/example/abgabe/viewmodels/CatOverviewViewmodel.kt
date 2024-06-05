package com.example.abgabe.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.abgabe.data.local.AppDatabase
import com.example.abgabe.ui.states.CatOverviewUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatOverviewViewModel @Inject constructor(
    private val catDatabase: AppDatabase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CatOverviewUiState>(CatOverviewUiState.Loading)
    val uiState: StateFlow<CatOverviewUiState> = _uiState.asStateFlow()

    init {
        loadCats()
    }

    private fun loadCats() {
        viewModelScope.launch {
            catDatabase.catDao().getCatsOrderedByName().collect { cats ->
                _uiState.value = if (cats.isNotEmpty()) {
                    CatOverviewUiState.Success(cats)
                } else {
                    CatOverviewUiState.EmptyDatabase
                }
            }
        }
    }
}
