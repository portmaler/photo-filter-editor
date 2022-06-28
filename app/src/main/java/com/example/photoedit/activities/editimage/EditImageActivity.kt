package com.example.photoedit.activities.editimage

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.example.photoedit.activities.filteredImage.FilteredImageActivity
import com.example.photoedit.activities.main.MainActivity
import com.example.photoedit.adapters.ImageFiltersAdapter
import com.example.photoedit.data.ImageFilter
import com.example.photoedit.databinding.ActivityEditImageBinding
import com.example.photoedit.listeners.ImageFilterListener
import com.example.photoedit.utilities.displayToast
import com.example.photoedit.utilities.show
import com.example.photoedit.viewmodels.EditImageViewModl
import jp.co.cyberagent.android.gpuimage.GPUImage
import org.koin.android.viewmodel.ext.android.viewModel

class EditImageActivity : AppCompatActivity(), ImageFilterListener {

    companion object{
        const val KEY_FILTERED_IMAGE_URI = "filteredImageUri"
    }

    private lateinit var binding: ActivityEditImageBinding
    private val viewModel:EditImageViewModl by viewModel()
    private lateinit var gpuImage: GPUImage

    //Image bitmap
    private lateinit var originalBitmap: Bitmap
    private val filterBitmap = MutableLiveData<Bitmap>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListners()
        setupObservers()
        prepareImagePreview()
    }

    private fun setupObservers(){
        viewModel.uiState.observe(this) {
            val dataState = it ?: return@observe
            binding.previewProgressBar.visibility =
                if (dataState.isLoading) View.VISIBLE else View.GONE
            dataState.bitmap?.let { bitmap ->
                //for the first time "filtered image = original Image'
                originalBitmap = bitmap
                filterBitmap.value = bitmap

                with(originalBitmap){
                    gpuImage.setImage(this)
                    binding.imagePreviews.show()
                    viewModel.loadImageFilters(bitmap)
                }
            } ?: kotlin.run {
                dataState.error?.let { error ->
                    displayToast(error)
                }
            }
        }
        viewModel.imageFiltersUiState.observe(this) {
            val imageFilterDataState = it ?: return@observe
            binding.imageFilterprogressBar.visibility =
                if (imageFilterDataState.isLoading) View.VISIBLE else View.GONE
            imageFilterDataState.imageFilters?.let { imageFilters ->
                ImageFiltersAdapter(imageFilters,this).also { adapter ->
                    binding.filterRecycleview.adapter = adapter
                }
            } ?: kotlin.run {
                imageFilterDataState.error?.let { error ->
                    displayToast(error)
                }
            }
        }
        filterBitmap.observe(this) { bitmap ->
            binding.imagePreviews.setImageBitmap(bitmap)
        }
        viewModel.saveFilteredImageUiState.observe(this){
            val saveFilteredImageDataState = it ?: return@observe
            if(saveFilteredImageDataState.isLoading){
                binding.imagesave.visibility = View.GONE
                binding.savingProgressBar.visibility = View.VISIBLE
            }else {
                binding.imagesave.visibility = View.VISIBLE
                binding.savingProgressBar.visibility = View.GONE
            }
            saveFilteredImageDataState.uri?.let {  savedImageUri ->
                Intent(
                    applicationContext,
                    FilteredImageActivity::class.java
                ).also { filteredImageIntent ->
                    filteredImageIntent.putExtra(KEY_FILTERED_IMAGE_URI,savedImageUri)
                    startActivity(filteredImageIntent)
                }
            } ?: kotlin.run {
                saveFilteredImageDataState.error?.let { error ->
                    displayToast(error)
                }
            }
        }
    }

    private fun prepareImagePreview(){
        gpuImage = GPUImage(applicationContext)
        intent.getParcelableExtra<Uri>(MainActivity.KEY_IMAGE_URL)?.let { imageUri ->
            viewModel.prpareimagePreview(imageUri)
        }
    }

    /*private fun displaypreviews() {
        intent.getParcelableExtra<Uri>(MainActivity.KEY_IMAGE_URL)?.let{ imageUri ->
            val inputStream = contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            binding.imagePreviews.setImageBitmap(bitmap)
            binding.imagePreviews.visibility = View.VISIBLE
        }
    }*/

    private fun setListners() {
        binding.imageback.setOnClickListener {
            onBackPressed()
        }

        binding.imagesave.setOnClickListener {
            filterBitmap.value?.let {  bitmap ->
                viewModel.saveFilteredImage(bitmap)
            }
        }

        binding.imagePreviews.setOnLongClickListener {
            binding.imagePreviews.setImageBitmap(originalBitmap)
            return@setOnLongClickListener false
        }
        binding.imagePreviews.setOnClickListener {
            binding.imagePreviews.setImageBitmap(filterBitmap.value)
        }
    }

    override fun onFilterSelected(imageFilter: ImageFilter) {
        with(imageFilter){
            with(gpuImage){
                setFilter(filter)
                filterBitmap.value = bitmapWithFilterApplied
            }
        }
    }
}