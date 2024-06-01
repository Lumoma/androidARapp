package com.example.abgabe.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CatDao {
    @Query("SELECT * FROM cat")
    fun getAll(): List<Cat>

    @Query("SELECT * FROM cat WHERE id = :id")
    fun getById(id: Int): Cat


    @Query("SELECT * FROM cat WHERE name = :name")
    fun getByName(name: String): Cat

    @Insert
    fun insert(cat: Cat)

    @Insert
    fun insertAll(cats: List<Cat>)

    @Delete
    fun delete(cat: Cat)

    @Query("DELETE FROM cat")
    fun deleteAll()
}
