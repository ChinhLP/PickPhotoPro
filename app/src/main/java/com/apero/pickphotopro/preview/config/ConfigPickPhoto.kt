package com.apero.pickphotopro.preview.config

import com.apero.pickphoto.api.config.VslPickPhotoConfig
import com.apero.pickphoto.api.config.action.VslPickPhotoActionConfig
import com.apero.pickphoto.api.config.module.VslPickPhotoModuleConfig
import com.apero.pickphoto.api.config.ui.VslPickPhotoUiConfig

class ConfigPickPhoto(
    private val vslPickPhotoActionConfig: VslPickPhotoActionConfig,
    private val vslPickPhotoUiConfig: VslPickPhotoUiConfig,
    private val vslPickPhotoModuleConfig: VslPickPhotoModuleConfig
) : VslPickPhotoConfig, VslPickPhotoActionConfig by vslPickPhotoActionConfig,
    VslPickPhotoUiConfig by vslPickPhotoUiConfig,
    VslPickPhotoModuleConfig by vslPickPhotoModuleConfig