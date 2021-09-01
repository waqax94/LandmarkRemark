package com.waqas.landmarkremark.domain.home.usecases

import com.waqas.landmarkremark.domain.home.HomeRepository
import com.waqas.landmarkremark.domain.home.entity.NoteEntity
import javax.inject.Inject

class GetCachedNotesUseCase @Inject constructor(private val homeRepository: HomeRepository) {
    fun invoke(): List<NoteEntity>{
        return homeRepository.getCachedNotes()
    }
}