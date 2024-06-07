package com.example.abgabe.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.abgabe.data.local.AppDatabase
import com.example.abgabe.data.local.Cat
import com.example.abgabe.ui.states.OverviewUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val database: AppDatabase
) : ViewModel() {

    private val _uiState = MutableStateFlow<OverviewUiState>(OverviewUiState.Loading)
    val uiState: StateFlow<OverviewUiState> = _uiState.asStateFlow()

    init {
        loadCats()
    }

    fun loadCats() {
        viewModelScope.launch {
            database.catDao().getCatsOrderedByName().collect { cats ->
                _uiState.value = if (cats.isNotEmpty()) {
                    OverviewUiState.Content(cats)
                } else {
                    OverviewUiState.EmptyDatabase
                }
            }
        }
    }

    fun addCat(cat: Cat) {
        viewModelScope.launch(Dispatchers.IO) {
            database.catDao().insert(cat)
            loadCats()
        }
    }
}
