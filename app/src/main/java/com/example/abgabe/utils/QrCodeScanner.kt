package com.example.abgabe.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.abgabe.data.local.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class QrCodeScannerViewModel @Inject constructor(
    private val database: AppDatabase
) : ViewModel() {

    // LiveData to hold the scanned cat ID
    private val _scannedCatId = MutableLiveData<String>()
    val scannedCatId: LiveData<String> get() = _scannedCatId

    fun onQrCodeScanned(scannedText: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val cat = database.catDao().getCatByIdUUID(UUID.fromString(scannedText))
            if (cat != null) {
                _scannedCatId.postValue(cat.id.toString())
            } else {
                // Handle the case where the cat is not found in the database
            }
        }
    }
}