package com.waqas.landmarkremark.presentation.home.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waqas.landmarkremark.domain.base.BaseResult
import com.waqas.landmarkremark.domain.home.entity.NoteEntity
import com.waqas.landmarkremark.domain.home.usecases.AddNoteUseCase
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
        private val getCachedNotesUseCase: GetCachedNotesUseCase,
        private val addNoteUseCase: AddNoteUseCase): ViewModel(){

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

    private fun additionSuccessful(msg: String){
        state.value = HomeActivityState.AdditionSuccess(msg)
    }

    private fun additionError(error: String){
        state.value = HomeActivityState.AdditionError(error)
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

    fun addNote(note: NoteEntity){
        viewModelScope.launch {
            addNoteUseCase.invoke(note)
                    .onStart {
                        setLoading()
                    }
                    .catch {
                        hideLoading()
                    }
                    .collect { result ->
                        hideLoading()
                        when(result){
                            is BaseResult.Success -> additionSuccessful(result.data)
                            is BaseResult.Error -> additionError(result.rawResponse)
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
    data class AdditionSuccess(val success: String): HomeActivityState()
    data class AdditionError(val error: String): HomeActivityState()
}