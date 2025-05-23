package com.apero.pickphoto.internal.ui.screen.pickphoto.intent

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.apero.pickphoto.di.DIContainer
import com.apero.pickphoto.internal.data.model.PhotoFolderModel
import com.apero.pickphoto.internal.data.model.PhotoModel
import kotlinx.android.parcel.Parcelize

@Parcelize
@Immutable
internal data class PickPhotoState(
    val photos: MutableList<PhotoModel> = mutableListOf(),
    val folders: MutableList<PhotoFolderModel> = mutableListOf(
        PhotoFolderModel(
            folderId = NAME_ALL_PHOTOS,
            folderName = NAME_ALL_PHOTOS
        )
    ),
    val folderSelected: PhotoFolderModel = PhotoFolderModel(),
    val itemSelected: PhotoModel = PhotoModel(path = DIContainer.pathImageSample , name = "Sample Image"),
    val pathImageSample: String = DIContainer.pathImageSample,
    val isShowListFolder: Boolean = false,
    val isFullPhotoPermissionGranted : Boolean = false
) : Parcelable

const val NAME_ALL_PHOTOS = "All Photos"
