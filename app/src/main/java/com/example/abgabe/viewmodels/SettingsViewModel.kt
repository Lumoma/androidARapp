package com.example.abgabe.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.abgabe.data.local.AppDatabase
import com.example.abgabe.data.local.CatDao
import com.example.abgabe.data.remote.getCats
import com.example.abgabe.ui.states.SettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val catDao: CatDao,
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = SettingsUiState.Loading
            val initialAmount = getDBSize()
            _uiState.value = if (initialAmount > 0) {
                SettingsUiState.Content(initialAmount)
            } else {
                SettingsUiState.EmptyDatabase
            }
        }
    }

    fun dumpDatabase() {
        viewModelScope.launch {
            _uiState.value = SettingsUiState.DumpingDatabase
            withContext(Dispatchers.IO) {
                catDao.deleteAll()
            }
            _uiState.value = SettingsUiState.EmptyDatabase
        }
    }

    fun generateCat(amount: Int, context: Context) {
        viewModelScope.launch {
            _uiState.value = SettingsUiState.UpdatingDatabase(getDBSize(), amount)
            val amountDifference = amount - getDBSize()
            if (amountDifference > 0) {
                val cats = withContext(Dispatchers.IO) {
                    getCats(amountDifference, context)
                }
                catDao.insertAll(cats)
                _uiState.value = SettingsUiState.Content(getDBSize())
            }
            else{
                _uiState.value = SettingsUiState.Error
            }
        }
    }

    private suspend fun getDBSize(): Int {
        return withContext(Dispatchers.IO) {
            catDao.getCount()
        }
    }
}