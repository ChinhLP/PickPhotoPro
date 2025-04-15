package com.apero.pickphoto

import android.app.Application
import com.apero.pickphoto.api.VslModulePickPhotoApi
import com.apero.pickphoto.api.VslModulePickPhotoApiImpl
import com.apero.pickphoto.api.config.VslPickPhotoConfig
import com.apero.pickphoto.di.DIContainer
import com.apero.pickphoto.di.DIContainer.repositoryContainer
import com.apero.pickphoto.util.PickPhotoLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

object VslPickPhotoEntry {

    @Volatile
    private var isInitialized = false

    @JvmStatic
    @Synchronized
    fun initialize(
        context: Application,
        config: VslPickPhotoConfig
    ) {
        if (isInitialized) return
        PickPhotoLogger.d("VslPickPhotoEntry", "initialized version 1.0.0")
        isInitialized = true
        saveImageSample(context)
        DIContainer.init(context, config)
    }
    fun saveImageSample(context: Application) {
        val myScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        myScope.launch {
            repositoryContainer.galleryRepository.cacheDrawableImage(
                context,
                R.drawable.img_demo
            )
        }

    }


    @JvmStatic
    fun getApi(): VslModulePickPhotoApi {
        checkInitialized()
        return VslModulePickPhotoApiImpl()
    }

    private fun checkInitialized() {
        check(isInitialized) {
            "VslModulePickPhotoEntry must be initialized first. Call VslModulePickPhotoEntry.initialize(context, config)"
        }
    }
}