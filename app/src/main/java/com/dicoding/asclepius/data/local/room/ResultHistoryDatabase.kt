package com.dicoding.asclepius.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.asclepius.data.local.entity.ResultHistoryEntity

@Database(entities = [ResultHistoryEntity::class], version = 1, exportSchema = false)
abstract class ResultHistoryDatabase : RoomDatabase() {
    abstract fun resultHistoryDao(): ResultHistoryDao

    companion object {
        @Volatile
        private var instance: ResultHistoryDatabase? = null
        fun getInstance(context: Context): ResultHistoryDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    ResultHistoryDatabase::class.java, "History.db"
                ).build()
            }
    }
}