package com.apero.pickphoto.api.config

import com.apero.pickphoto.api.config.action.VslPickPhotoActionConfig
import com.apero.pickphoto.api.config.ads.VslPickPhotoAdsConfig
import com.apero.pickphoto.api.config.module.VslPickPhotoModuleConfig
import com.apero.pickphoto.api.config.ui.VslPickPhotoUiConfig

interface VslPickPhotoConfig : VslPickPhotoUiConfig, VslPickPhotoAdsConfig,
    VslPickPhotoActionConfig, VslPickPhotoModuleConfig