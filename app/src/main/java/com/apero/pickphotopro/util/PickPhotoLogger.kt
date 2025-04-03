package com.apero.pickphotopro.util

import android.util.Log

internal object PickPhotoLogger {
    fun d(tag: String, message: String) {
        Log.d("VslPickPhoto_$tag", message)
    }

    fun i(tag: String, message: String) {
        Log.i("VslPickPhoto_$tag", message)
    }

    fun e(tag: String, throwable: Throwable? = null, message: String = "") {
        Log.e("VslPickPhoto_$tag", message, throwable)
    }
}