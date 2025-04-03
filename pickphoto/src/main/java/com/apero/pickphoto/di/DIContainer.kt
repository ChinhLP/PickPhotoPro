package com.apero.pickphoto.di

import android.app.Application
import com.apero.pickphoto.api.config.VslPickPhotoConfig
import com.apero.pickphoto.api.config.action.VslPickPhotoActionConfig
import com.apero.pickphoto.api.config.ads.VslPickPhotoAdsConfig
import com.apero.pickphoto.api.config.module.VslPickPhotoModuleConfig
import com.apero.pickphoto.api.config.ui.VslPickPhotoUiConfig
import com.apero.pickphoto.di.repo.RepositoryContainer
import com.apero.pickphoto.di.viewmodel.ViewModelContainer

internal object DIContainer {
    lateinit var application: Application
    lateinit var vslPickPhotoUiConfig: VslPickPhotoUiConfig
    lateinit var vslPickPhotoActionConfig: VslPickPhotoActionConfig
    lateinit var vslPickPhotoAdsConfig: VslPickPhotoAdsConfig
    lateinit var vslPickPhotoModuleContainer: VslPickPhotoModuleConfig

    val repositoryContainer by lazy { RepositoryContainer() }
    val viewModelContainer by lazy { ViewModelContainer() }

    @JvmStatic
    @JvmSynthetic
    @Synchronized
    fun init(
        context: Application,
        vslPickPhotoConfig: VslPickPhotoConfig
    ) {
        if (DIContainer::application.isInitialized) return
        application = context
        vslPickPhotoUiConfig = vslPickPhotoConfig
        vslPickPhotoActionConfig = vslPickPhotoConfig
        vslPickPhotoAdsConfig = vslPickPhotoConfig
        vslPickPhotoModuleContainer = vslPickPhotoConfig
    }
}