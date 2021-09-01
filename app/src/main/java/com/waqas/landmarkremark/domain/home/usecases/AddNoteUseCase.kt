package com.waqas.landmarkremark.domain.home.usecases

import com.waqas.landmarkremark.domain.base.BaseResult
import com.waqas.landmarkremark.domain.home.HomeRepository
import com.waqas.landmarkremark.domain.home.entity.NoteEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddNoteUseCase @Inject constructor(private val homeRepository: HomeRepository) {
    suspend fun invoke(note: NoteEntity): Flow<BaseResult<String, String>> {
        return homeRepository.addNote(note)
    }
}