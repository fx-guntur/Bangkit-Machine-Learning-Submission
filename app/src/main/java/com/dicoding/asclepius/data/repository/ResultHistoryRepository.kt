package com.dicoding.asclepius.data.repository

import android.app.Application
import com.dicoding.asclepius.data.local.entity.ResultHistoryEntity
import com.dicoding.asclepius.data.local.room.ResultHistoryDao
import com.dicoding.asclepius.data.local.room.ResultHistoryDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ResultHistoryRepository(application: Application) {
    private val dao: ResultHistoryDao
    private val exeService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = ResultHistoryDatabase.getInstance(application)
        dao = db.resultHistoryDao()
    }

    fun getAllHistory(): Flow<List<ResultHistoryEntity>> = flow {
        emit(dao.getAllResultHistory())
    }

    fun insertHistory(historyEntity: ResultHistoryEntity) {
        exeService.execute { dao.insertResultHistory(historyEntity) }
    }

    fun deleteHistory(historyEntity: ResultHistoryEntity) {
        exeService.execute { dao.deleteResultHistory(historyEntity) }
    }
}