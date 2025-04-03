package com.apero.pickphotopro.di

import android.app.Application
import com.apero.pickphotopro.api.config.VslPickPhotoConfig
import com.apero.pickphotopro.api.config.action.VslPickPhotoActionConfig
import com.apero.pickphotopro.api.config.ads.VslPickPhotoAdsConfig
import com.apero.pickphotopro.api.config.module.VslPickPhotoModuleConfig
import com.apero.pickphotopro.api.config.ui.VslPickPhotoUiConfig
import com.apero.pickphotopro.di.repo.RepositoryContainer
import com.apero.pickphotopro.di.viewmodel.ViewModelContainer

internal object DIContainer {
    lateinit var application: Application
    lateinit var vslPickPhotoUiConfig: VslPickPhotoUiConfig
    lateinit var vslPickPhotoActionConfig: VslPickPhotoActionConfig
    lateinit var vslPickPhotoAdsConfig: VslPickPhotoAdsConfig
    lateinit var vslPickPhotoModelContainer: VslPickPhotoModuleConfig

    val repositoryContainer by lazy { RepositoryContainer() }
    val viewModelContainer by lazy { ViewModelContainer() }

    @JvmStatic
    @JvmSynthetic
    @Synchronized
    fun init(
        context: Application,
        vslPickPhotoConfig: VslPickPhotoConfig
    ) {
        if (::application.isInitialized) return
        this.application = context
        vslPickPhotoUiConfig = vslPickPhotoConfig
        vslPickPhotoActionConfig = vslPickPhotoConfig
        vslPickPhotoAdsConfig = vslPickPhotoConfig
        vslPickPhotoModelContainer = vslPickPhotoConfig
    }
}