package com.example.photoedit.dependencyinjection

import com.example.photoedit.repositories.EditImageRepository
import com.example.photoedit.repositories.EitImageRepositoryImpl
import com.example.photoedit.repositories.SavedImagesRepository
import com.example.photoedit.repositories.SavedImagesRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    factory<EditImageRepository> { EitImageRepositoryImpl(androidContext()) }
    factory<SavedImagesRepository> { SavedImagesRepositoryImpl(androidContext()) }
}