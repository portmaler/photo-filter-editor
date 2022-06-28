package com.example.photoedit.listeners

import java.io.File

interface SavedImageListener {
    fun onImageClicked(file: File)
}