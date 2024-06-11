package com.example.abgabe.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface CatDao {

    @Insert
    fun insert(cat: Cat)

    @Insert
    fun insertAll(cats: List<Cat>)

    @Query("SELECT * FROM cat")
    fun getAll(): List<Cat>

    @Query("SELECT * FROM cat WHERE id = :id")
    fun getCatByIdFlow(id: UUID): Flow<Cat?>

    @Query("SELECT * FROM cat WHERE id = :id")
    fun getCatByIdUUID(id: UUID): Cat

    @Query("SELECT * FROM cat ORDER BY name ASC")
    fun getCatsOrderedByName(): Flow<List<Cat>>

    @Query("SELECT * FROM cat ORDER BY breed ASC")
    fun getCatsOrderedByBreed(): List<Cat>
    @Query("SELECT COUNT(*) FROM cat")
    fun getCount(): Int

    @Update
    fun updateCatInfos(cat: Cat)

    @Delete
    fun delete(cat: Cat)

    @Query("DELETE FROM cat")
    fun deleteAll()
}