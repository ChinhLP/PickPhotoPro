package com.apero.pickphoto.internal.ui.screen.pickphoto.intent

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
                getItemPickPhotoTheLast()
            }

            is PickPhotoIntent.LoadMorePhotos -> viewModelScope.launch {
                loadMorePhotos(
                    intent.context,
                    intent.lastPhotoDateAdded
                )
                getItemPickPhotoTheLast()
            }

            is PickPhotoIntent.SelectPhoto -> selectPhoto(intent.itemSelected)
            is PickPhotoIntent.LoadFolders -> viewModelScope.launch {
                loadFolders(intent.context)
                getItemInFolderTheLast()
            }

            is PickPhotoIntent.LoadMoreImageInFolder -> viewModelScope.launch {
                loadMoreImageInFolder(
                    intent.context,
                    intent.folderId,
                    intent.lastPhotoDateAdded
                )
                getItemInFolderTheLast()
            }
        }
    }

    fun getItemInFolderTheLast() = viewModelScope.launch {
        TODO()
    }

    private fun getItemPickPhotoTheLast() =
        viewModelScope.launch {
            updateUiState { copy(lastItemPickPhoto = this.photos.last()) }
        }

    private fun loadInitPhotos(context: Context) =
        viewModelScope.launch {
            val photos = galleryRepository.getPhotos(context, 50)
            updateUiState { copy(photos = photos.toMutableList()) }
        }

    private fun loadMorePhotos(context: Context, lastPhotoDateAdded: Long?) =
        viewModelScope.launch {
            val photos = galleryRepository.getPhotos(context, 50, lastPhotoDateAdded)
            updateUiState { copy(photos = this.photos.apply { addAll(photos) }) }
        }

    private fun selectPhoto(itemSelected: PhotoModel) {
        updateUiState { copy(itemSelected = itemSelected) }
    }

    private fun loadFolders(context: Context) =
        viewModelScope.launch {
            val folders = galleryRepository.getAllFolders(context)
            updateUiState { copy(folders = folders) }
        }

    private fun loadMoreImageInFolder(
        context: Context,
        folderId: String,
        lastPhotoDateAdded: Long? = null
    ) =
        viewModelScope.launch {
            val photos =
                galleryRepository.getMoreImagesInFolder(context, folderId, 50, lastPhotoDateAdded)
            updateUiState {
                copy(
                    folders = folders.map { folder ->
                        if (folder.folderId == folderId) {
                            folder.copy(
                                photos = folder.photos.apply { addAll(photos.toMutableList()) }
                            )
                        } else folder
                    }
                )
            }
        }
}