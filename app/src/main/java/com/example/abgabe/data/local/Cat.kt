package com.example.abgabe.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/*
data class Cat(
    @PrimaryKey val id: UUID = UUID.randomUUID(), //used by the QR Code
    val name: String,
    val breed: String,
    val temperament: String,
    val origin: String,
    val lifeExpectancy: Int,
    val imageUrl: String,

    //Bonus
    //weitere Eigenschaften
)

 */

@Entity
data class CatApiData(
    @PrimaryKey
    val id: String,
    val url: String,
    val width: Int,
    val height: Int
)