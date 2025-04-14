package com.apero.pickphoto.internal.data.repo

import android.content.Context
import com.apero.pickphoto.internal.data.model.PhotoFolderModel
import com.apero.pickphoto.internal.data.model.PhotoModel
import kotlinx.coroutines.flow.Flow

internal interface GalleryRepository {
    suspend fun getPhotos(
        context: Context,
        limit: Int
    ): Flow<List<PhotoModel>>

    suspend fun getAllFolders(context: Context): Flow<List<PhotoFolderModel>>

    suspend fun getMoreImagesInFolder(
        context: Context,
        folderId: String,
        limit: Int,
        lastPhotoDateAdded: Long?
    ): List<PhotoModel>
}