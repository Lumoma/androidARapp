package com.example.abgabe.viewmodels


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.abgabe.data.local.Cat
import com.example.abgabe.data.local.CatDao
import com.example.abgabe.data.remote.CatApi
import com.example.abgabe.ui.states.OverviewUiState
import com.example.abgabe.data.util.QrCodeHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val catDao: CatDao,
    private val qrCodeHelper: QrCodeHelper,
    private val catApi: CatApi
) : ViewModel() {

    val addCatToggle = MutableStateFlow(false)

    var catListFlow: Flow<List<Cat>> = flowOf(emptyList())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            catDao.getCatsOrderedByName().collect {
                catListFlow = flowOf(it)
            }
        }
    }

    val uiState: StateFlow<OverviewUiState> = combine(
        catListFlow,
        addCatToggle
    ) { cats, addCatState ->
        when {
            addCatState -> OverviewUiState.AddCat(
                pictureUrl = getRandomCatPictureURL(),
                onSaveCat = {
                    addCatToDatabase(it.copy(it.id, it.name, it.breed, it.temperament, it.origin, it.lifeExpectancy, it.imageUrl, it.qrCodePath, it.qrCodeByteArray))
                    addCatToggle.value = false
                },
                generateNewPictureURL = {
                        viewModelScope.launch {
                        getRandomCatPictureURL()
                    }
                }
            )
            else -> OverviewUiState.Content(
                cats = cats,
                onAddCat = {
                    addCatToggle.value = true
                }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), OverviewUiState.Loading)

    fun addCatToDatabase(cat: Cat) {
        viewModelScope.launch(Dispatchers.IO) {
            cat.qrCodePath = qrCodeHelper.generateQRCodeFromUUID(cat.name, cat.id)
            cat.qrCodeByteArray = qrCodeHelper.generateQRCodeByteCodeFromUUID(cat.id)
            catDao.insert(cat)
        }
    }

    private suspend fun getRandomCatPictureURL(): String {
        return withContext(Dispatchers.IO) {
            catApi.getRandomCatPictureUrlFromApi()
        }
    }
}
