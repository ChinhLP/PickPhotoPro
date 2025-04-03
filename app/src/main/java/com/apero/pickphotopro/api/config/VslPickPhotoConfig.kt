package com.apero.pickphotopro.api.config

import com.apero.pickphotopro.api.config.action.VslPickPhotoActionConfig
import com.apero.pickphotopro.api.config.ads.VslPickPhotoAdsConfig
import com.apero.pickphotopro.api.config.module.VslPickPhotoModuleConfig
import com.apero.pickphotopro.api.config.ui.VslPickPhotoUiConfig

interface VslPickPhotoConfig : VslPickPhotoUiConfig, VslPickPhotoAdsConfig,
    VslPickPhotoActionConfig, VslPickPhotoModuleConfig