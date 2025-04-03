package com.apero.pickphoto

import android.app.Application
import com.apero.pickphoto.api.VslModulePickPhotoApi
import com.apero.pickphoto.api.VslModulePickPhotoApiImpl
import com.apero.pickphoto.api.config.VslPickPhotoConfig
import com.apero.pickphoto.di.DIContainer
import com.apero.pickphoto.util.PickPhotoLogger

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
        DIContainer.init(context, config)
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