package com.waqas.landmarkremark.domain.home

import com.waqas.landmarkremark.domain.base.BaseResult
import com.waqas.landmarkremark.domain.home.entity.NoteEntity
import kotlinx.coroutines.flow.Flow


interface HomeRepository {
    var allNotes: List<NoteEntity>
    suspend fun getAllNotes(): Flow<BaseResult<List<NoteEntity>, String>>
    fun getCachedNotes(): List<NoteEntity>
}