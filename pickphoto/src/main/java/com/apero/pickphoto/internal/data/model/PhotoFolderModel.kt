package com.apero.pickphoto.internal.data.model

import android.net.Uri

data class PhotoFolderModel(
    val folderId: String = "",
    val folderName: String = "",
    val photos: MutableList<PhotoModel> = mutableListOf(),
    val thumbnailUri: Uri? = null
)