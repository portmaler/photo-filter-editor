package com.example.photoedit.viewmodels

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.photoedit.data.ImageFilter
import com.example.photoedit.repositories.EditImageRepository
import com.example.photoedit.utilities.Coroutines

class EditImageViewModl(private val editImageRepository: EditImageRepository) : ViewModel() {

    //region:: Prepar Image preview

    private val imagePreviewDataState = MutableLiveData<ImagePreviewDataState>()
    val uiState: LiveData<ImagePreviewDataState> get() = imagePreviewDataState

    fun prpareimagePreview(imageUri: Uri){
        Coroutines.io {
            kotlin.runCatching {
                emitUiState(isLoading = true)
                editImageRepository.prepareImagePreview(imageUri)
            }.onSuccess { bitmap ->
                if(bitmap != null){
                    emitUiState(bitmap = bitmap)
                }else {
                    emitUiState(error = "unable to prepare Imageview")
                }
            }.onFailure {
                emitUiState(error = it.message.toString())
            }
        }
    }

    private fun emitUiState(
        isLoading: Boolean = false,
        bitmap: Bitmap? = null,
        error: String? = null
    ){
        val dataState = ImagePreviewDataState(isLoading,bitmap,error)
        imagePreviewDataState.postValue(dataState)
    }

    data class ImagePreviewDataState(
        val isLoading: Boolean,
        val bitmap: Bitmap?,
        val error: String?
    )

    //endregion

    //region:: Load image filters
    private val imageFiltersDataState = MutableLiveData<ImageFilterDataState>()
    val imageFiltersUiState: LiveData<ImageFilterDataState> get() = imageFiltersDataState

    fun loadImageFilters(originalImage: Bitmap){
        Coroutines.io {
            kotlin.runCatching {
                emitImageFiltersUiState(isLoading = true)
                editImageRepository.getImageFilters(getPreviewImage(originalImage))
            }.onSuccess { imagFilters ->
                emitImageFiltersUiState(imageFilters = imagFilters)
            }.onFailure {
                emitImageFiltersUiState(error = it.message.toString())
            }
        }
    }

    private fun getPreviewImage(originalImage: Bitmap): Bitmap{
        return runCatching {
            val previewWidth = 150
            val previewHeight = originalImage.height * previewWidth / originalImage.width
            Bitmap.createScaledBitmap(originalImage, previewWidth, previewHeight,false)
        }.getOrDefault(originalImage)
    }

    private fun emitImageFiltersUiState(
        isLoading: Boolean = false,
        imageFilters: List<ImageFilter>? =null,
        error: String? = null
    ){
        val dataState = ImageFilterDataState(isLoading, imageFilters,error)
        imageFiltersDataState.postValue(dataState)
    }

    data class ImageFilterDataState(
        val isLoading: Boolean,
        val imageFilters: List<ImageFilter>?,
        val error: String?
    )
    //endregion

    //region:: Save ffiltered image
    private val saveFilteredImageDataState = MutableLiveData<SavFilteredImageDataState>()
    val saveFilteredImageUiState: LiveData<SavFilteredImageDataState> get() = saveFilteredImageDataState

    fun saveFilteredImage(filteredBitmap: Bitmap){
        Coroutines.io {
            runCatching {
                emitSaveFilteredImageUiState(isLoading = true)
                editImageRepository.saveFilteredImage(filteredBitmap)
            }.onSuccess { savedImageuri ->
                emitSaveFilteredImageUiState(uri = savedImageuri)
            }.onFailure {
                emitSaveFilteredImageUiState(error = it.message.toString())
            }
        }
    }

    private fun emitSaveFilteredImageUiState(
        isLoading: Boolean = false,
        uri: Uri? = null,
        error: String? = null
    ){
        val dataState = SavFilteredImageDataState(isLoading, uri,error)
        saveFilteredImageDataState.postValue(dataState)
    }

    data class SavFilteredImageDataState(
        val isLoading: Boolean,
        val uri: Uri?,
        val error: String?
    )
    //endregion



}






