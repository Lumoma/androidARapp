package com.example.abgabe.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.abgabe.data.local.CatDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CameraScreenViewModel @Inject constructor(
    private val catDao: CatDao,
) : ViewModel() {

    // LiveData to hold the scanned cat ID
    private val _scannedCatId = MutableLiveData<String?>()
    val validCatFound = MutableLiveData<Boolean>()
    val scannedCatId: MutableLiveData<String?> get() = _scannedCatId

    fun onQrCodeScanned(scannedText: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val cat = catDao.getCatByIdUUID(UUID.fromString(scannedText))
            if (cat != null) {
                _scannedCatId.postValue(cat.id.toString())
                validCatFound.postValue(true)
            }
            else {
                validCatFound.postValue(false)
            }
        }
    }
}