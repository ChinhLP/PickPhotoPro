package com.apero.pickphoto.internal.data.repo

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.ContextCompat
import coil3.ImageLoader
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.apero.pickphoto.internal.data.model.PhotoFolderModel
import com.apero.pickphoto.internal.data.model.PhotoModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import androidx.core.graphics.createBitmap
import com.apero.pickphoto.di.DIContainer
import java.io.File
import java.io.FileOutputStream

internal class GalleryRepositoryImpl : GalleryRepository {
    override suspend fun getPhotos(
        context: Context,
        limit: Int
    ): Flow<List<PhotoModel>> = flow {
        var offset = 0
        var hasMorePhotos = true

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val query = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )

        query?.use { cursor ->
            while (hasMorePhotos) {
                val photos = mutableListOf<PhotoModel>()

                if (cursor.moveToPosition(offset)) {
                    var count = 0
                    do {
                        val id =
                            cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                        val name =
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                        val folder =
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                        val date =
                            cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED))

                        val contentUri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            id
                        )

                        photos.add(
                            PhotoModel(
                                path = contentUri.path,
                                uri = contentUri,
                                name = name,
                                folder = folder,
                                dateAdded = date
                            )
                        )
                        count++
                    } while (cursor.moveToNext() && count < limit)
                }

                emit(photos)
                hasMorePhotos = photos.size == limit
                offset += limit
            }
        }
    }

    override suspend fun getAllFolders(context: Context): Flow<List<PhotoFolderModel>> = flow {
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
                folderMap.getOrPut(key) { mutableListOf() }.add(contentUri)
            }
        }

        val allFolders = folderMap.map { (key, uris) ->
            val (folderId, folderName) = key
            PhotoFolderModel(
                folderId = folderId,
                folderName = folderName,
                photos = uris.take(50).map { PhotoModel(path = it.path, uri = it) }
                    .toMutableList(),
                thumbnailUri = uris.first()
            )
        }

        // Emit liên tục từng batch 10 folders
        allFolders.chunked(10).forEach { chunk ->
            emit(chunk)
        }
    }.flowOn(Dispatchers.IO)

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
                        path = contentUri.path + "_compressed",
                        uri = contentUri,
                        name = name,
                        dateAdded = date
                    )
                )
            }
        }

        photos
    }

    override suspend fun cacheDrawableImage(context: Context, drawableResId: Int): String? {
        return withContext(Dispatchers.IO) {
            try {
                val drawable = ContextCompat.getDrawable(context, drawableResId) ?: return@withContext null

                val bitmap = if (drawable is BitmapDrawable) {
                    drawable.bitmap
                } else {
                    createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight).also {
                        val canvas = Canvas(it)
                        drawable.setBounds(0, 0, canvas.width, canvas.height)
                        drawable.draw(canvas)
                    }
                }

                // Lưu bitmap vào file trong cache dir
                val fileName = "cached_drawable_${drawableResId}.png"
                val file = File(context.cacheDir, fileName)
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }

                // Nạp vào cache Coil (tuỳ bạn, có thể bỏ nếu chỉ cần path)
                val imageLoader = ImageLoader.Builder(context)
                    .crossfade(false)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .build()

                val request = ImageRequest.Builder(context)
                    .data(file)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .build()

                imageLoader.execute(request)

                DIContainer.pathImageSample = file.absolutePath
                return@withContext file.absolutePath
            } catch (e: Exception) {
                Log.e("CacheDrawable", "Failed to cache drawable", e)
                return@withContext null
            }
        }
    }
}