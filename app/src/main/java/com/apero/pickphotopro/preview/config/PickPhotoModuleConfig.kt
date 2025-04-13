package com.apero.pickphotopro.preview.config

import com.apero.pickphoto.api.config.module.VslPickPhotoModuleConfig

class PickPhotoModuleConfig() : VslPickPhotoModuleConfig {
    override val projectName: String
        get() = "ddd"
    override val appName: String
        get() = "pickPhoto"
    override val language: String
        get() = "en"
}