package com.waqas.landmarkremark.presentation.home.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waqas.landmarkremark.domain.base.BaseResult
import com.waqas.landmarkremark.domain.home.entity.NoteEntity
import com.waqas.landmarkremark.domain.home.usecases.GetAllNotesUseCase
import com.waqas.landmarkremark.domain.home.usecases.GetCachedNotesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject constructor(
        private val getAllNotesUseCase: GetAllNotesUseCase,
        private val getCachedNotesUseCase: GetCachedNotesUseCase): ViewModel(){

    private val state = MutableStateFlow<HomeActivityState>(HomeActivityState.Init)
    val mState: StateFlow<HomeActivityState> get() = state

    private fun setLoading(){
        state.value = HomeActivityState.IsLoading(true)
    }

    private fun hideLoading() {
        state.value = HomeActivityState.IsLoading(false)
    }

    private fun successResponse(notes: List<NoteEntity>){
        state.value = HomeActivityState.SuccessResponse(notes)
    }

    private fun errorResponse(rawResponse: String){
        state.value = HomeActivityState.ErrorResponse(rawResponse)
    }

    fun getAllNotes(){
        viewModelScope.launch {
            getAllNotesUseCase.invoke()
                    .onStart {
                        setLoading()
                    }
                    .catch { exception ->
                        hideLoading()
                    }
                    .collect { result ->
                        hideLoading()
                        when(result){
                            is BaseResult.Success -> successResponse(result.data)
                            is BaseResult.Error -> errorResponse(result.rawResponse)
                        }
                    }
        }
    }

    fun getCachedNotes(): List<NoteEntity>{
        return getCachedNotesUseCase.invoke()
    }
}

sealed class HomeActivityState{
    object Init: HomeActivityState()
    data class IsLoading(val isLoading: Boolean): HomeActivityState()
    data class SuccessResponse(val notes: List<NoteEntity>): HomeActivityState()
    data class ErrorResponse(val rawResponse: String): HomeActivityState()
}