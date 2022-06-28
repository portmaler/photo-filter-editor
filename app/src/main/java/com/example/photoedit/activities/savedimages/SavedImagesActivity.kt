package com.example.photoedit.activities.savedimages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.photoedit.R
import com.example.photoedit.databinding.ActivitySavedImagesBinding
import com.example.photoedit.utilities.displayToast
import com.example.photoedit.utilities.show
import com.example.photoedit.viewmodels.SavedImagesViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class SavedImagesActivity : AppCompatActivity() {

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
                displayToast("${savedImages.size} images losaded")
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
}