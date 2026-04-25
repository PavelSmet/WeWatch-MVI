package com.example.wewatch.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.wewatch.models.Movie

@Database(
    entities = [Movie::class],
    version = 2, // Обновили версию из-за новых полей (сюжет, актеры и т.д.)
    exportSchema = false
)
abstract class MovieDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao

    companion object {
        @Volatile
        private var INSTANCE: MovieDatabase? = null

        fun getInstance(context: Context): MovieDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MovieDatabase::class.java,
                    "movie_database"
                )
                .fallbackToDestructiveMigration() // Позволяет сбросить БД при изменении структуры
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}