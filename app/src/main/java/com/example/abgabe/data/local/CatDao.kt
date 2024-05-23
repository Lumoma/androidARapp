package com.example.abgabe.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CatDao {
    @Query("SELECT * FROM cat")
    fun getAllCats(): Flow<List<Cat>>

    @Insert
    suspend fun insertCat(cat: Cat)

}
