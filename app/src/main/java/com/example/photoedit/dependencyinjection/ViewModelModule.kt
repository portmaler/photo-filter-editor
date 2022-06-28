package com.example.photoedit.dependencyinjection

import com.example.photoedit.viewmodels.EditImageViewModl
import com.example.photoedit.viewmodels.SavedImagesViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { EditImageViewModl(editImageRepository = get())}
    viewModel { SavedImagesViewModel(savedImagesRepository = get()) }
}