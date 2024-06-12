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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val catDao: CatDao,
) : ViewModel() {

    val catEditFlow = MutableStateFlow(false)

    // Cat properties
    val catNameFlow = MutableStateFlow("")
    val catBreedFlow = MutableStateFlow("")
    val catTemperamentFlow = MutableStateFlow("")
    val catOriginFlow = MutableStateFlow("")
    val catLifeExpectancyFlow = MutableStateFlow("")


    sealed interface DetailsUiState {
        data object Loading : DetailsUiState

        data class Edit(
            val cat: Cat,
            val onSaveChanges: () -> Unit,
            val onEditName: (String) -> Unit,
            val onEditBreed: (String) -> Unit,
            val onEditTemperament: (String) -> Unit,
            val onEditOrigin: (String) -> Unit,
            val onEditLifeExpectancy: (String) -> Unit,
        ) : DetailsUiState
        data class Content(val cat: Cat, val onEdit: () -> Unit) : DetailsUiState
    }

    private val catIdFlow = MutableStateFlow<UUID?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<DetailsUiState> = combine(
        catIdFlow.flatMapLatest { id ->
            id?.let { catDao.getCatByIdFlow(it) } ?: flowOf(null)
        },
        catEditFlow
    ){ cat, catEdit ->
        when{
            cat == null -> DetailsUiState.Loading
            catEdit -> DetailsUiState.Edit(
                cat = cat,
                onSaveChanges = { viewModelScope.launch { updateAndRefreshCat() } ; catEditFlow.value = false },
                onEditName = { catNameFlow.value = it },
                onEditBreed = { catBreedFlow.value = it },
                onEditTemperament = { catTemperamentFlow.value = it },
                onEditOrigin = { catOriginFlow.value = it },
                onEditLifeExpectancy = { catLifeExpectancyFlow.value = it },
            )
            else -> DetailsUiState.Content(cat, onEdit = { catEditFlow.value = true })
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), DetailsUiState.Loading)

    fun setCatId(id: String) {
        catIdFlow.value = UUID.fromString(id)
    }

    private fun updateCat(cat: Cat) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                catDao.updateCatInfos(cat)
            }
        }
    }

    fun deleteCatFromDatabase() {
    viewModelScope.launch(Dispatchers.IO) {
        (uiState.value as? DetailsUiState.Content)?.cat?.let { catDao.delete(it) }
        }
    }

    fun openEditWindow() {
        val cat = (uiState.value as? DetailsUiState.Content)?.cat ?: return
        catNameFlow.value = cat.name
        catBreedFlow.value = cat.breed
        catTemperamentFlow.value = cat.temperament
        catOriginFlow.value = cat.origin
        catLifeExpectancyFlow.value = cat.lifeExpectancy
        catEditFlow.value = true
    }

    private fun updateAndRefreshCat() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                (uiState.value as? DetailsUiState.Content)?.cat?.copy(
                    name = catNameFlow.value,
                    breed = catBreedFlow.value,
                    temperament = catTemperamentFlow.value,
                    origin = catOriginFlow.value,
                    lifeExpectancy = catLifeExpectancyFlow.value
                )?.let { updateCat(it) }
            }
        }
    }
}