package com.example.photoedit.activities.savedimages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.FileProvider
import com.example.photoedit.R
import com.example.photoedit.activities.editimage.EditImageActivity
import com.example.photoedit.activities.filteredImage.FilteredImageActivity
import com.example.photoedit.adapters.SavedImagesAdapter
import com.example.photoedit.databinding.ActivitySavedImagesBinding
import com.example.photoedit.listeners.SavedImageListener
import com.example.photoedit.utilities.displayToast
import com.example.photoedit.utilities.show
import com.example.photoedit.viewmodels.SavedImagesViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File

class SavedImagesActivity : AppCompatActivity() , SavedImageListener{

    private lateinit var binding: ActivitySavedImagesBinding
    private val viewModel: SavedImagesViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedImagesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupObservers()
        setListeners()
        viewModel.loadSavedImages()
    }

    private fun setupObservers() {
        viewModel.savedImagesUiState.observe(this) {
            val savedImagesDataState  = it ?: return@observe
            binding.savedImagesProgressBar.visibility =
                if (savedImagesDataState.isLoading) View.VISIBLE else View.GONE
            savedImagesDataState.savedImages?.let { savedImages ->
                SavedImagesAdapter(savedImages, this).also {  adapter ->
                    with(binding.savedImagesRecycleview) {
                        this.adapter = adapter
                        visibility = View.VISIBLE
                    }
                }
            } ?: run {
                savedImagesDataState.error?.let { error ->
                    displayToast(error)
                }
            }
        }
    }

    private fun setListeners() {
        binding.imageback.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onImageClicked(file: File) {
        val fileUri = FileProvider.getUriForFile(
            applicationContext,
            "${packageName}.provider",
            file
        )
        Intent(
            applicationContext,
            FilteredImageActivity::class.java
        ).also { filtereImageIntent ->
            filtereImageIntent.putExtra(EditImageActivity.KEY_FILTERED_IMAGE_URI, fileUri)
            startActivity(filtereImageIntent)
        }
    }
}