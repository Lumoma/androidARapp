package com.example.abgabe.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Cat::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun catDao(): CatDao
}
