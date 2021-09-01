package com.waqas.landmarkremark.data.home.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.waqas.landmarkremark.domain.base.BaseResult
import com.waqas.landmarkremark.domain.home.HomeRepository
import com.waqas.landmarkremark.domain.home.entity.NoteEntity
import com.waqas.landmarkremark.infra.util.AppConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class HomeRepositoryImp() : HomeRepository {
    override var allNotes: List<NoteEntity> = ArrayList()
    override suspend fun getAllNotes(): Flow<BaseResult<List<NoteEntity>, String>> {
        return  flow{
            var success = false
            val db = FirebaseFirestore.getInstance()
            db.collection(AppConstants.DB_COLLECTION)
                    .get()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val notes = mutableListOf<NoteEntity>()
                            for (document in it.result!!) {
                                notes.add(NoteEntity(document.data.getValue(AppConstants.KEY_LAT) as Double,
                                        document.data.getValue(AppConstants.KEY_LONG) as Double,
                                        document.data.getValue(AppConstants.KEY_NOTES) as String,
                                        document.data.getValue(AppConstants.KEY_USERNAME) as String))
                            }
                            allNotes = notes
                            success = true


                        } else {
                            success = false
                        }
                    }
            if(success){
                emit(BaseResult.Success(allNotes))
            }
            else{
                emit(BaseResult.Error(AppConstants.ERROR))
            }
        }
    }

    override fun getCachedNotes(): List<NoteEntity> {
        return allNotes
    }

    override suspend fun addNote(note: NoteEntity): Flow<BaseResult<String, String>> {
        return flow {
            var success = false
            val db = FirebaseFirestore.getInstance()
            val newNote : MutableMap<String, Any> = HashMap()
            newNote[AppConstants.KEY_LAT] = note.latitude
            newNote[AppConstants.KEY_LONG] = note.longitude
            newNote[AppConstants.KEY_USERNAME] = note.userName
            newNote[AppConstants.KEY_NOTES] = note.notes

            db.collection(AppConstants.DB_COLLECTION)
                    .add(newNote)
                    .addOnSuccessListener {
                        success = true
                    }
                    .addOnFailureListener {
                        success = false
                    }
            if(success){
                emit(BaseResult.Success(AppConstants.SUCCESS))
            }
            else{
                emit(BaseResult.Error(AppConstants.ERROR))
            }
            getAllNotes()
        }
    }
}