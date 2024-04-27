package com.dicoding.asclepius.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.dicoding.asclepius.R
import com.dicoding.asclepius.base.BaseActivity
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.yalantis.ucrop.UCrop
import org.tensorflow.lite.task.vision.classifier.Classifications
import timber.log.Timber
import java.io.File

class MainActivity : BaseActivity<ActivityMainBinding>() {

    private var currentImageUri: Uri? = null

    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initUI() {
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
    }

    override fun initProcess() {}

    override fun initObservers() {}

    override fun initActionButtons() {
        binding.apply {
            galleryButton.setOnClickListener {
                startGallery()
            }

            analyzeButton.setOnClickListener {
                analyzeImage()
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            startUCrop(uri)
        } else {
            Timber.tag("Photo Picker").d("No media selected")
        }
    }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri: Uri = UCrop.getOutput(result.data!!)!!
                currentImageUri = imageUri
                showImage()
            }
        }

    private fun startUCrop(uri: Uri) {
        val fileName = "UCrop-image-${System.currentTimeMillis()}"
        val destinationUri = Uri.fromFile(File(cacheDir, fileName))
        UCrop.of(uri, destinationUri)
            .withAspectRatio(1f, 1f)
            .start(this, activityResultLauncher)
    }

    private fun showImage() {
        currentImageUri?.let {
            Timber.tag("Image URI").d("showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun analyzeImage() {
        currentImageUri?.let { uri ->
            val imageClassifier = ImageClassifierHelper(
                context = this,
                classifierListener = object : ImageClassifierHelper.ClassifierListener {
                    override fun onError(error: String) {
                        showToast(error)
                    }

                    override fun onResults(results: List<Classifications>?) {
                        if (results != null) {
                            if (results.isNotEmpty() && results[0].categories.isNotEmpty()) {
                                val sortedClassifications =
                                    results[0].categories.sortedByDescending { it.score }
                                val confidenceScore = sortedClassifications[0].score * 100
                                val prediction = sortedClassifications[0].label

                                moveToResult(uri, confidenceScore, prediction)
                            } else {
                                showToast(getString(R.string.image_classifier_failed))
                            }
                        }

                        results?.let { classifications ->

                        }
                    }
                }
            )
            imageClassifier.classifyStaticImage(uri)
        } ?: showToast(getString(R.string.no_image))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_history -> {
                val intent = Intent(this, ResultHistoryActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun moveToResult(
        imageUri: Uri?,
        confidenceScore: Float,
        prediction: String
    ) {
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra(ResultActivity.EXTRA_URI_IMAGE, currentImageUri.toString())
            putExtra(ResultActivity.EXTRA_PREDICTION, prediction)
            putExtra(ResultActivity.EXTRA_CONFIDENCE_SCORE, confidenceScore)
        }
        startActivity(intent)
    }
}