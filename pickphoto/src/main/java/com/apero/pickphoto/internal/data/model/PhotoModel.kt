package com.apero.pickphoto.internal.data.model

import android.net.Uri

data class PhotoModel(
    val uri : Uri? = null,
    val name : String = "",
    val folder : String = "",
    val dateAdded : Long = 0
)