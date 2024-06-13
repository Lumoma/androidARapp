package com.example.abgabe.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.abgabe.data.local.Cat
import com.example.abgabe.data.local.CatDao
import com.example.abgabe.ui.states.DetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
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



   private lateinit var catEdited: Cat

    private val catIdFlow = MutableStateFlow<UUID?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<DetailUiState> = combine(
        catIdFlow.flatMapLatest { id ->
            id?.let { catDao.getCatByIdFlow(it) } ?: flowOf(null)
        },
        catEditFlow
    ){ cat, catEdit ->
        when{
            cat == null -> DetailUiState.Loading
            catEdit -> DetailUiState.Edit(
                editCat = cat,
                onSaveChanges = { viewModelScope.launch { updateCat(catEdited) } ; catEditFlow.value = false },
                onEditName = { catEdited = cat.copy(name = it) },
                onEditBreed = { catEdited = cat.copy(breed = it) },
                onEditTemperament = { catEdited = cat.copy(temperament = it) },
                onEditOrigin = { catEdited = cat.copy(origin = it) },
                onEditLifeExpectancy = { catEdited = cat.copy(lifeExpectancy = it) }
            )
            else -> DetailUiState.Content(cat, onEdit = { catEditFlow.value = true })
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), DetailUiState.Loading)

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
        (uiState.value as? DetailUiState.Content)?.cat?.let { catDao.delete(it) }
        }
    }

    fun openEditWindow() {
        catEditFlow.value = true
    }
}