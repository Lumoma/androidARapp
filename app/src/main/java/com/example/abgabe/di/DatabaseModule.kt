package com.example.abgabe.di

import android.content.Context
import androidx.room.Room
import com.example.abgabe.data.local.AppDatabase
import com.example.abgabe.data.remote.CatApi
import com.example.abgabe.data.util.CatConverter
import com.example.abgabe.data.util.QrCodeHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, "cat-database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideCatDao(appDatabase: AppDatabase) = appDatabase.catDao()

    @Provides
    @Singleton
    fun provideQrCodeHelper(@ApplicationContext context: Context) = QrCodeHelper(context)

    @Provides
    @Singleton
    fun provideCatConverter(qrCodeHelper: QrCodeHelper) = CatConverter(qrCodeHelper)

    @Provides
    @Singleton
    fun provideCatApi(catConverter: CatConverter) = CatApi(catConverter)
}