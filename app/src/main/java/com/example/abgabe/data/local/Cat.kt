package com.example.abgabe.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity
data class Cat(
    @PrimaryKey val id: String, //used by the QR Code
    val name: String,
    val breed: String,
    val temperament: String,
    val origin: String,
    val lifeExpectancy: Int,
    val imageUrl: String,

    //Bonus
    //weitere Eigenschaften
)