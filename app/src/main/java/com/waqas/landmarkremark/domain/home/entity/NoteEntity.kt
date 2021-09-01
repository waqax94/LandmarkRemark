package com.waqas.landmarkremark.domain.home.entity

data class NoteEntity(
    val latitude: Double,
    val longitude: Double,
    val notes: String,
    val userName: String
)