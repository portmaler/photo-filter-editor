package com.example.photoedit.viewmodels

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.photoedit.repositories.SavedImagesRepository
import com.example.photoedit.utilities.Coroutines
import java.io.File

class SavedImagesViewModel(private val savedImagesRepository: SavedImagesRepository) : ViewModel() {

    private val savedImagesDataState = MutableLiveData<SavedImagesDatatState>()
    val savedImagesUiState: LiveData<SavedImagesDatatState> get() = savedImagesDataState

    fun loadSavedImages(){
        Coroutines.io {
            runCatching {
                emitSavedImagesUiState(isLoading = true)
                savedImagesRepository.loadSavedImages()
            }.onSuccess { savedImages ->
                if(savedImages.isNullOrEmpty()){
                    emitSavedImagesUiState(error = "no image found")
                }else {
                    emitSavedImagesUiState(savedImages = savedImages)
                }
            }.onFailure {
                emitSavedImagesUiState(error = it.message.toString())
            }
        }
    }

    private fun emitSavedImagesUiState(
        isLoading: Boolean = false,
        savedImages: List<Pair<File,Bitmap>>? = null,
        error: String? = null
    ){
        val dataState = SavedImagesDatatState(isLoading,savedImages,error)
        savedImagesDataState.postValue(dataState)
    }

    data class SavedImagesDatatState(
        val isLoading: Boolean,
        val savedImages: List<Pair<File,Bitmap>>?,
        val error: String?
    )
}