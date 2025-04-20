package com.apero.pickphoto.internal.ui.screen.pickphoto.intent

import android.content.Context
import com.apero.pickphoto.internal.data.model.PhotoFolderModel
import com.apero.pickphoto.internal.data.model.PhotoModel

internal sealed class PickPhotoIntent {
    data class LoadInitPhotos(val context: Context) : PickPhotoIntent()
    data class LoadFolders(val context: Context) : PickPhotoIntent()
    data class SelectPhoto(val itemSelected: PhotoModel) : PickPhotoIntent()
    data class SelectFolder(val folderSelected: PhotoFolderModel) : PickPhotoIntent()
    data class SetPhotoPermissionFullGranted(val isGranted: Boolean) : PickPhotoIntent()
    data object ChangeShowListFolder : PickPhotoIntent()
}