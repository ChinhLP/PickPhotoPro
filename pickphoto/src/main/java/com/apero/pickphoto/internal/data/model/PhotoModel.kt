package com.apero.pickphoto.internal.data.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class PhotoModel(
    val id : String? = null,
    val uri : Uri? = null,
    val name : String = "",
    val folder : String = "",
    val dateAdded : Long = 0
) : Parcelable