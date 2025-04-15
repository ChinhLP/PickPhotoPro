package com.apero.pickphoto.internal.ui.screen.pickphoto.intent

import android.content.Context
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apero.pickphoto.R
import com.apero.pickphoto.internal.data.model.PhotoFolderModel
import com.apero.pickphoto.internal.data.model.PhotoModel
import com.apero.pickphoto.internal.data.repo.GalleryRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class PickPhotoViewModel(
    private val galleryRepository: GalleryRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    companion object {
        private const val UI_STATE_KEY = "photo_ui_state"
    }

    val uiState: StateFlow<PickPhotoState> =
        savedStateHandle.getStateFlow(UI_STATE_KEY, PickPhotoState())

    private fun updateUiState(update: PickPhotoState.() -> PickPhotoState) {
        val newState = uiState.value.update()
        savedStateHandle[UI_STATE_KEY] = newState
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
        }
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