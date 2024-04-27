package com.dicoding.asclepius.view

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.asclepius.data.local.entity.ResultHistoryEntity
import com.dicoding.asclepius.data.repository.ResultHistoryRepository
import kotlinx.coroutines.launch

class ResultHistoryViewModel(application: Application) : ViewModel() {
    private val rhRepository: ResultHistoryRepository = ResultHistoryRepository(application)
    private val _listHistory = MutableLiveData<List<ResultHistoryEntity>>()
    val listHistory: LiveData<List<ResultHistoryEntity>> = _listHistory

    fun getAllHistory() {
        viewModelScope.launch {
            rhRepository.getAllHistory().asLiveData().observeForever {
                _listHistory.value = it
            }
        }
    }
}