package com.apero.pickphotopro.preview.di

import com.apero.pickphoto.api.config.VslPickPhotoConfig
import com.apero.pickphoto.api.config.action.VslPickPhotoActionConfig
import com.apero.pickphoto.api.config.module.VslPickPhotoModuleConfig
import com.apero.pickphoto.api.config.ui.VslPickPhotoUiConfig
import com.apero.pickphotopro.preview.config.ConfigPickPhoto
import com.apero.pickphotopro.preview.config.PickPhotoActionConfig
import com.apero.pickphotopro.preview.config.PickPhotoModuleConfig
import com.apero.pickphotopro.preview.config.PickPhotoUiConfig

object DIContainer {
    val pickPhotoUiConfig: VslPickPhotoUiConfig by lazy { PickPhotoUiConfig() }
    val pickPhotoActionConfig: VslPickPhotoActionConfig by lazy { PickPhotoActionConfig() }
    val pickPhotoModuleConfig: VslPickPhotoModuleConfig by lazy { PickPhotoModuleConfig() }
    val pickPhotoConfig: VslPickPhotoConfig by lazy {
        ConfigPickPhoto(
            pickPhotoActionConfig,
            pickPhotoUiConfig,
            pickPhotoModuleConfig
        )
    }
}