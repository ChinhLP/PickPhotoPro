package com.apero.pickphoto.internal.data.repo

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.apero.pickphoto.internal.data.model.PhotoFolderModel
import com.apero.pickphoto.internal.data.model.PhotoModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class GalleryRepositoryImpl : GalleryRepository {
    override suspend fun getPhotos(
        context: Context,
        limit: Int,
        lastPhotoDateAdded: Long?
    ): List<PhotoModel> = withContext(Dispatchers.IO) {
        val photos = mutableListOf<PhotoModel>()

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED
        )

        val selection = lastPhotoDateAdded?.let {
            "${MediaStore.Images.Media.DATE_ADDED} < ?"
        }

        val selectionArgs = lastPhotoDateAdded?.let {
            arrayOf(it.toString())
        }

        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val query = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val folderColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

            var count = 0
            while (cursor.moveToNext() && count < limit) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val folder = cursor.getString(folderColumn)
                val date = cursor.getLong(dateColumn)

                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                photos.add(
                    PhotoModel(
                        id = id.toString() + "_compressed",
                        uri = contentUri,
                        name = name,
                        folder = folder,
                        dateAdded = date
                    )
                )
                count++
            }
        }

        photos
    }

    override suspend fun getAllFolders(context: Context): List<PhotoFolderModel> =
        withContext(Dispatchers.IO) {
            val folders = mutableListOf<PhotoFolderModel>()

            val projection = arrayOf(
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media._ID
            )

            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            val cursor = context.contentResolver.query(
                uri,
                projection,
                null,
                null,
                sortOrder
            )

            // key = Pair(folderId, folderName)
            val folderMap = linkedMapOf<Pair<String, String>, MutableList<Uri>>()

            cursor?.use {
                val folderNameColumn =
                    it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
                val folderIdColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

                while (it.moveToNext()) {
                    val folderName = it.getString(folderNameColumn) ?: "Unknown"
                    val folderId = it.getString(folderIdColumn) ?: "0"
                    val id = it.getLong(idColumn)
                    val contentUri = ContentUris.withAppendedId(uri, id)

                    val key = folderId to folderName
                    if (folderMap.containsKey(key)) {
                        folderMap[key]?.add(contentUri)
                    } else {
                        folderMap[key] = mutableListOf(contentUri)
                    }
                }
            }

            folderMap.forEach { (key, uris) ->
                val (folderId, folderName) = key
                folders.add(
                    PhotoFolderModel(
                        folderId = folderId,
                        folderName = folderName,
                        photos = uris.take(50).map { PhotoModel(uri = it) }.toMutableList(),
                        thumbnailUri = uris.first()
                    )
                )
            }

            folders
        }

    override suspend fun getMoreImagesInFolder(
        context: Context,
        folderId: String,
        limit: Int,
        lastPhotoDateAdded: Long?
    ): List<PhotoModel> = withContext(Dispatchers.IO) {
        val photos = mutableListOf<PhotoModel>()

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED
        )

        val selection = buildString {
            append("${MediaStore.Images.Media.BUCKET_ID} = ?")
            if (lastPhotoDateAdded != null) {
                append(" AND ${MediaStore.Images.Media.DATE_ADDED} < ?")
            }
        }

        val selectionArgs = buildList {
            add(folderId)
            if (lastPhotoDateAdded != null) {
                add(lastPhotoDateAdded.toString())
            }
        }.toTypedArray()

        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC LIMIT $limit"

        val query = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val date = cursor.getLong(dateColumn)

                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                photos.add(
                    PhotoModel(
                        id = id.toString() + "_compressed",
                        uri = contentUri,
                        name = name,
                        dateAdded = date
                    )
                )
            }
        }

        photos
    }
}