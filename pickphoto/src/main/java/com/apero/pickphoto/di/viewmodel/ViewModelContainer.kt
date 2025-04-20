package com.apero.pickphoto.di.viewmodel

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.apero.pickphoto.di.DIContainer
import com.apero.pickphoto.internal.ui.screen.camera.intent.CameraViewModel
import com.apero.pickphoto.internal.ui.screen.pickphoto.intent.PickPhotoViewModel

internal class ViewModelFactory : AbstractSavedStateViewModelFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return when {
            modelClass.isAssignableFrom(CameraViewModel::class.java) -> {
                CameraViewModel(handle) as T
            }

            modelClass.isAssignableFrom(PickPhotoViewModel::class.java) -> {
                PickPhotoViewModel(
                    DIContainer.repositoryContainer.galleryRepository
                ) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}


internal class ViewModelContainer {

    private val viewModelFactory = ViewModelFactory()

    fun <T : ViewModel> getViewModel(
        owner: ViewModelStoreOwner,
        viewModelClass: Class<T>
    ): T {
        return ViewModelProvider(owner, viewModelFactory)[viewModelClass]
    }
}

