package com.example.photoedit.listeners

import com.example.photoedit.data.ImageFilter

interface ImageFilterListener {
    fun onFilterSelected(imageFilter: ImageFilter)
}