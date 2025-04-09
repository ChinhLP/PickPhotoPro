package com.apero.pickphoto.internal.ui.screen.pickphoto.intent

import com.apero.pickphoto.internal.data.model.PhotoFolderModel
import com.apero.pickphoto.internal.data.model.PhotoModel

internal data class PickPhotoState(
    val photos: MutableList<PhotoModel> = mutableListOf(),
    val folders: List<PhotoFolderModel> = mutableListOf(),
    val lastItemPickPhoto: PhotoModel = PhotoModel(),
    val lastItemInFolder: PhotoModel = PhotoModel(),
    val folderSelected: PhotoFolderModel = PhotoFolderModel(),
    val itemSelected: PhotoModel = PhotoModel()
)