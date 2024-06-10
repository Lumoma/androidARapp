package com.example.abgabe.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.abgabe.data.local.Cat
import com.example.abgabe.data.local.CatDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val catDao: CatDao,
) : ViewModel() {
    var cat by mutableStateOf<Cat?>(null)
    private var catId: UUID? = null

    init {
        refreshCat()
    }

    fun setCatId(id: String) {
        catId = UUID.fromString(id)
        refreshCat()
    }

    fun refreshCat() {
        viewModelScope.launch {
            catId?.let {
                withContext(Dispatchers.IO) {
                    catDao.getCatByIdFlow(it).collect { cat = it }
                }
            }
        }
    }

    fun updateCat(cat: Cat) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                catDao.updateCatInfos(cat)
            }
        }
    }

    fun deleteCatFromDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            cat?.let { catDao.delete(it) }
        }
    }

    fun updateAndRefreshCat(catName: String? = null, catBreed: String? = null, catTemperament: String? = null, catOrigin: String? = null, catLifeExpectancy: String? = null) {
    viewModelScope.launch {
        withContext(Dispatchers.IO) {
            cat?.copy(
                name = catName ?: cat!!.name,
                breed = catBreed ?: cat!!.breed,
                temperament = catTemperament ?: cat!!.temperament,
                origin = catOrigin ?: cat!!.origin,
                lifeExpectancy = catLifeExpectancy ?: cat!!.lifeExpectancy
            )?.let { updateCat(it) }

        }
    }
}
}

