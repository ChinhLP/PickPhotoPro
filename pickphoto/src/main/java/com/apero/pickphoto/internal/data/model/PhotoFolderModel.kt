package com.apero.pickphoto.internal.data.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PhotoFolderModel(
    val folderId: String = "",
    val folderName: String = "",
    val photos: MutableList<PhotoModel> = mutableListOf(),
    val thumbnailUri: Uri? = null
) : Parcelable