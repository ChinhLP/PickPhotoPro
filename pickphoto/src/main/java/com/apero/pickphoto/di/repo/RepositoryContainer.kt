package com.apero.pickphoto.di.repo

import com.apero.pickphoto.internal.data.pref.SharedPref
import com.apero.pickphoto.internal.data.repo.GalleryRepository
import com.apero.pickphoto.internal.data.repo.GalleryRepositoryImpl

internal class RepositoryContainer {

    val sharedPref: SharedPref by lazy { SharedPref() }

    val galleryRepository: GalleryRepository by lazy { GalleryRepositoryImpl() }

}