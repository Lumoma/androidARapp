package com.example.abgabe.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.internal.bind.TypeAdapters
import java.util.UUID

@Entity
data class Cat(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val name: String,
    val breed: String,
    val temperament: String,
    val origin: String,
    val lifeExpectancy: String,
    val imageUrl: String,
    val qrCodeID: Int,

    //Bonus
    //weitere Eigenschaften
)