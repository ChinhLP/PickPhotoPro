package com.apero.pickphoto.internal.ui.screen.pickphoto.intent

import android.content.Context
import com.apero.pickphoto.internal.data.model.PhotoModel

internal sealed class PickPhotoIntent {
    data class LoadInitPhotos(val context: Context) : PickPhotoIntent()
    data class LoadMorePhotos(val context: Context, val lastPhotoDateAdded: Long) : PickPhotoIntent()
    data class LoadFolders(val context: Context) : PickPhotoIntent()
    data class LoadMoreImageInFolder(val context: Context, val folderId: String, val limit: Int = 50, val lastPhotoDateAdded: Long?) : PickPhotoIntent()
    data class SelectPhoto(val itemSelected: PhotoModel) : PickPhotoIntent()
}