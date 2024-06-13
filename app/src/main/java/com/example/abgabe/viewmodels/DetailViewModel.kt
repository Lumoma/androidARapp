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

    sealed interface DetailsUiState {
        data object Loading : DetailsUiState

        data class Edit(
            val editCat: Cat,
            val onSaveChanges: () -> Unit,
            val onEditName: (String) -> Unit,
            val onEditBreed: (String) -> Unit,
            val onEditTemperament: (String) -> Unit,
            val onEditOrigin: (String) -> Unit,
            val onEditLifeExpectancy: (String) -> Unit,
        ) : DetailsUiState
        data class Content(val cat: Cat, val onEdit: () -> Unit) : DetailsUiState
    }

   private lateinit var catEdited: Cat

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
                editCat = cat,
                onSaveChanges = { viewModelScope.launch { updateCat(catEdited) } ; catEditFlow.value = false },
                onEditName = { catEdited = cat.copy(name = it) },
                onEditBreed = { catEdited = cat.copy(breed = it) },
                onEditTemperament = { catEdited = cat.copy(temperament = it) },
                onEditOrigin = { catEdited = cat.copy(origin = it) },
                onEditLifeExpectancy = { catEdited = cat.copy(lifeExpectancy = it) }
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
        catEdited = (uiState.value as? DetailsUiState.Content)?.cat ?: return
        catEditFlow.value = true
    }
}