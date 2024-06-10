package com.example.abgabe.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.abgabe.data.local.AppDatabase
import com.example.abgabe.data.local.Cat
import com.example.abgabe.data.local.CatDao
import com.example.abgabe.data.remote.generateQRCodeByteCodeFromUUID
import com.example.abgabe.data.remote.generateQRCodeFromUUID
import com.example.abgabe.data.remote.getRandomCatPictureUrlFromApi
import com.example.abgabe.ui.states.OverviewUiState
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
class OverviewViewModel @Inject constructor(
    private val catDao: CatDao,
) : ViewModel() {

    private val _uiState = MutableStateFlow<OverviewUiState>(OverviewUiState.Loading)
    val uiState: StateFlow<OverviewUiState> = _uiState.asStateFlow()
    

    init {
        loadCats()
    }

    fun loadCats() {
        viewModelScope.launch {
            _uiState.value = OverviewUiState.Loading
            catDao.getCatsOrderedByName().collect { cats ->
                _uiState.value = if (cats.isNotEmpty()) {
                    OverviewUiState.Content(cats)
                } else {
                    OverviewUiState.EmptyDatabase
                }
            }
        }
    }

    fun addCatToDatabase(cat: Cat, context: android.content.Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = OverviewUiState.Loading
            cat.qrCodePath = generateQRCodeFromUUID(cat.name, cat.id, context)
            cat.qrCodeByteArray = generateQRCodeByteCodeFromUUID(cat.id)
            catDao.insert(cat)
            loadCats()
        }
    }

    private suspend fun getRandomCatPictureURL(): String {
        return withContext(Dispatchers.IO) {
            getRandomCatPictureUrlFromApi()
        }
    }

    fun showAddCat() {
        viewModelScope.launch {
        _uiState.value = OverviewUiState.AddCat(getRandomCatPictureURL())
        }
    }
}
