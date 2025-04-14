package com.apero.pickphoto.internal.ui.screen.pickphoto.intent

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.apero.pickphoto.internal.data.model.PhotoFolderModel
import com.apero.pickphoto.internal.data.model.PhotoModel
import kotlinx.android.parcel.Parcelize

@Parcelize
@Immutable
internal data class PickPhotoState(
    val photos: MutableList<PhotoModel> = mutableListOf(),
    val folders: MutableList<PhotoFolderModel> = mutableListOf(),
    val lastItemPickPhoto: PhotoModel = PhotoModel(),
    val lastItemInFolder: PhotoModel = PhotoModel(),
    val folderSelected: PhotoFolderModel = PhotoFolderModel(),
    val itemSelected: PhotoModel = PhotoModel(),
    val isShowListFolder: Boolean = false
) : Parcelable