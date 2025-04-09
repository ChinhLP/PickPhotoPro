package com.apero.pickphoto.internal.data.repo

import android.content.Context
import com.apero.pickphoto.internal.data.model.PhotoFolderModel
import com.apero.pickphoto.internal.data.model.PhotoModel

internal interface GalleryRepository {
    suspend fun getPhotos(
        context: Context,
        limit: Int,
        lastPhotoDateAdded: Long? = null
    ): List<PhotoModel>

    suspend fun getAllFolders(context: Context): List<PhotoFolderModel>

    suspend fun getMoreImagesInFolder(
        context: Context,
        folderId: String,
        limit: Int,
        lastPhotoDateAdded: Long?
    ): List<PhotoModel>
}