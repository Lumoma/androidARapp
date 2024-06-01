package com.example.abgabe.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CatDao {
    @Query("SELECT * FROM catApiData")
    fun getAll(): List<CatApiData>

    @Query("SELECT * FROM catApiData WHERE id = :id")
    fun getById(id: Int): CatApiData

    /*
    @Query("SELECT * FROM catApiData WHERE name = :name")
    fun getByName(name: String): CatApiData
     */

    @Insert
    fun insert(cat: CatApiData)

    @Insert
    fun insertAll(cats: List<CatApiData>)

    @Delete
    fun delete(cat: CatApiData)
}
