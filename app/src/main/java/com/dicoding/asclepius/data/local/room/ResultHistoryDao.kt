package com.dicoding.asclepius.data.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.asclepius.data.local.entity.ResultHistoryEntity

@Dao
interface ResultHistoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertResultHistory(resultHistory: ResultHistoryEntity)

    @Delete
    fun deleteResultHistory(resultHistory: ResultHistoryEntity)

    @Query("SELECT * FROM result_history")
    suspend fun getAllResultHistory(): List<ResultHistoryEntity>
}