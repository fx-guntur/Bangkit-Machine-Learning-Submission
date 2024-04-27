package com.dicoding.asclepius.view

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.BuildConfig
import com.dicoding.asclepius.data.local.entity.ResultHistoryEntity
import com.dicoding.asclepius.data.remote.ApiConfig
import com.dicoding.asclepius.data.remote.response.NewsResponse
import com.dicoding.asclepius.data.repository.ResultHistoryRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResultViewModel(application: Application) : ViewModel() {
    private val resultHistoryRepository: ResultHistoryRepository =
        ResultHistoryRepository(application)

    private val _findNews = MutableLiveData<NewsResponse>()
    val findNews: LiveData<NewsResponse> = _findNews

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<String>()
    val isError: LiveData<String> = _isError

    init {
        getNews()
    }

    private fun getNews() {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getNews(
            QUERY_NEWS,
            QUERY_CATEGORY,
            QUERY_LANGUAGE,
            BuildConfig.API_KEY_NEWS
        )
        client.enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _findNews.value = response.body()
                } else {
                    _isError.value = response.message()
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                _isLoading.value = false
            }
        })
    }

    fun insertHistory(rhEntity: ResultHistoryEntity) {
        resultHistoryRepository.insertHistory(rhEntity)
    }

    fun deleteHistory(rhEntity: ResultHistoryEntity) {
        resultHistoryRepository.deleteHistory(rhEntity)
    }

    companion object {
        private const val QUERY_NEWS = "cancer"
        private const val QUERY_CATEGORY = "health"
        private const val QUERY_LANGUAGE = "en"
    }
}