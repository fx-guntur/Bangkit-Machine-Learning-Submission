package com.dicoding.asclepius.view

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.R
import com.dicoding.asclepius.adapter.NewsAdapter
import com.dicoding.asclepius.base.BaseActivity
import com.dicoding.asclepius.data.local.entity.ResultHistoryEntity
import com.dicoding.asclepius.data.remote.response.ArticlesItem
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.DateHelper

class ResultActivity : BaseActivity<ActivityResultBinding>(), NewsAdapter.OnItemClickCallback {
    private var historyEntity: ResultHistoryEntity? = ResultHistoryEntity()
    private var requestCode: Int = 0
    private val resultViewModel by viewModels<ResultViewModel> {
        ViewModelFactory.getInstance(application)
    }

    override fun getViewBinding(): ActivityResultBinding {
        return ActivityResultBinding.inflate(layoutInflater)
    }

    override fun initIntent() {
        requestCode = intent.getIntExtra(EXTRA_RESULT_HISTORY, 0)

        if (requestCode == HISTORY_REQUEST_CODE) {
            historyEntity = if (Build.VERSION.SDK_INT >= 33) {
                intent.getParcelableExtra(EXTRA_LOCAL_DATA, ResultHistoryEntity::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(EXTRA_LOCAL_DATA)
            }
        } else {
            historyEntity?.apply {
                imageUri = intent.getStringExtra(EXTRA_URI_IMAGE)
                prediction = intent.getStringExtra(EXTRA_PREDICTION) ?: ""
                confidenceScore = intent.getFloatExtra(EXTRA_CONFIDENCE_SCORE, 0f)
                date = DateHelper.getCurrentTimestamp()
            }
            historyEntity.let {
                historyEntity?.let { resultViewModel.insertHistory(it) }
                showToast(getString(R.string.saved_in_history))
            }
        }

    }

    override fun initUI() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        historyEntity?.let { history ->
            history.imageUri.let {
                binding.resultImage.setImageURI(Uri.parse(it))
            }
            val predictionAndConfidence = String.format(
                "%s : %.2f%%", history.prediction, history.confidenceScore
            )
            binding.resultText.text = predictionAndConfidence
        }

        binding.apply {
            rvNews.apply {
                layoutManager = LinearLayoutManager(this@ResultActivity)
            }
        }
    }

    private fun listNewsDataService(listNews: List<ArticlesItem>) {
        val adapter = NewsAdapter()
        adapter.listNews = listNews
        binding.rvNews.adapter = adapter

        adapter.setOnItemClickCallback(this)
    }

    override fun initProcess() {}

    private fun showLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun deleteHistory() {
        historyEntity?.let { resultViewModel.deleteHistory(it) }
        if (historyEntity != null) {
            showToast(getString(R.string.history_deleted))
            finish()
        } else showToast(getString(R.string.history_delete_error))
    }

    override fun initObservers() {
        resultViewModel.isLoading.observe(this, ::showLoading)
        resultViewModel.findNews.observe(this) {
            listNewsDataService(it.articles)
        }
        resultViewModel.isError.observe(this) {
            val message = getString(R.string.find_news_error, it)
            showToast(message)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.result_menu, menu)
        if (requestCode != HISTORY_REQUEST_CODE) {
            menu?.findItem(R.id.remove_history)?.isVisible = false
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }

            R.id.remove_history -> {
                deleteHistory()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClicked(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    companion object {
        const val EXTRA_URI_IMAGE = "extra_uri_image"
        const val EXTRA_PREDICTION = "extra_prediction"
        const val EXTRA_CONFIDENCE_SCORE = "extra_confidence_score"
        const val EXTRA_LOCAL_DATA = "extra_local_data"
        const val EXTRA_RESULT_HISTORY = "extra_result_history"
        const val HISTORY_REQUEST_CODE = 100
    }

}