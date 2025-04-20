package com.apero.pickphoto.internal.ui.screen.pickphoto.intent

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apero.pickphoto.internal.data.model.PhotoFolderModel
import com.apero.pickphoto.internal.data.model.PhotoModel
import com.apero.pickphoto.internal.data.repo.GalleryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class PickPhotoViewModel(
    private val galleryRepository: GalleryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PickPhotoState())
    val uiState: StateFlow<PickPhotoState> = _uiState.asStateFlow()

    private fun updateUiState(update: PickPhotoState.() -> PickPhotoState) {
        val newState = uiState.value.update()
        _uiState.value = newState
    }

    fun onEvent(intent: PickPhotoIntent) {
        when (intent) {
            is PickPhotoIntent.LoadInitPhotos -> viewModelScope.launch {
                loadInitPhotos(intent.context)
            }

            is PickPhotoIntent.SelectPhoto -> selectPhoto(intent.itemSelected)
            is PickPhotoIntent.LoadFolders -> viewModelScope.launch {
                loadFolders(intent.context)
            }

            PickPhotoIntent.ChangeShowListFolder -> {
                updateUiState { copy(isShowListFolder = !isShowListFolder) }
            }

            is PickPhotoIntent.SelectFolder -> {
                selectFolder(intent.folderSelected)
            }

            is PickPhotoIntent.SetPhotoPermissionFullGranted -> setPhotoPermissionFullGranted(intent.isGranted)
        }
    }

    private fun setPhotoPermissionFullGranted(isGranted: Boolean) {
        updateUiState { copy(isFullPhotoPermissionGranted = isGranted) }
    }

    private fun selectFolder(folderSelected: PhotoFolderModel) {
        onEvent(PickPhotoIntent.ChangeShowListFolder)
        updateUiState { copy(folderSelected = folderSelected) }
    }


    private fun loadInitPhotos(context: Context) =
        viewModelScope.launch {
            galleryRepository.getPhotos(context, 50).collect { photoList ->
                updateUiState {
                    photos.addAll(photoList)
                    copy(photos = (photos))
                }
            }
        }

    private fun selectPhoto(itemSelected: PhotoModel) {
        updateUiState { copy(itemSelected = itemSelected) }
    }

    private fun loadFolders(context: Context) =
        viewModelScope.launch {
            galleryRepository.getAllFolders(context).collect { folderBatch ->
                updateUiState {
                    val index = folders.indexOfFirst { it.folderId == NAME_ALL_PHOTOS }
                    if (index != -1) {
                        folders[index] = folders[index].copy(
                            photos = photos,
                            thumbnailUri = photos.firstOrNull()?.uri
                        )
                    }
                    folders.addAll(folderBatch)
                    copy(folders = folders, folderSelected = folders[index])
                }
            }
        }

}