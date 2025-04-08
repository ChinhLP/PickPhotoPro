package com.apero.pickphotopro.preview

import android.app.Application
import com.apero.pickphoto.VslPickPhotoEntry
import com.apero.pickphotopro.preview.di.DIContainer.pickPhotoConfig

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        initPickPhoto()
    }

   private fun initPickPhoto() {
        VslPickPhotoEntry.initialize(this, pickPhotoConfig)
    }
}