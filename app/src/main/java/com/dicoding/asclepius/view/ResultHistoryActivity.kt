package com.dicoding.asclepius.view

import android.content.Intent
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.adapter.ResultHistoryAdapter
import com.dicoding.asclepius.base.BaseActivity
import com.dicoding.asclepius.data.local.entity.ResultHistoryEntity
import com.dicoding.asclepius.databinding.ActivityResultHistoryBinding

class ResultHistoryActivity : BaseActivity<ActivityResultHistoryBinding>(),
    ResultHistoryAdapter.OnItemClickCallback {
    private val rhViewModel by viewModels<ResultHistoryViewModel> {
        ViewModelFactory.getInstance(application)
    }

    override fun getViewBinding(): ActivityResultHistoryBinding {
        return ActivityResultHistoryBinding.inflate(layoutInflater)
    }

    override fun initUI() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        binding.apply {
            rvResultHistory.apply {
                layoutManager = LinearLayoutManager(this@ResultHistoryActivity)
            }
        }
    }

    override fun initProcess() {}

    override fun initObservers() {
        rhViewModel.getAllHistory()
        rhViewModel.listHistory.observe(this) { listHistory ->
            listHistoryDataService(listHistory)
        }
    }

    override fun onResume() {
        super.onResume()
        rhViewModel.getAllHistory()
    }

    private fun intentResult(data: ResultHistoryEntity) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(ResultActivity.EXTRA_RESULT_HISTORY, ResultActivity.HISTORY_REQUEST_CODE)
        intent.putExtra(ResultActivity.EXTRA_LOCAL_DATA, data)
        startActivity(intent)
    }

    private fun listHistoryDataService(listHistory: List<ResultHistoryEntity>) {
        val adapter = ResultHistoryAdapter()
        adapter.listHistory = listHistory
        binding.rvResultHistory.adapter = adapter

        adapter.setOnItemClickCallback(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClicked(history: ResultHistoryEntity) {
        intentResult(history)
    }
}