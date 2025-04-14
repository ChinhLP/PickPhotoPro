package com.apero.pickphotopro.preview.sample

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.apero.pickphotopro.preview.preview.PreviewActivity.Companion.IMAGE_PREVIEW

class SampleViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    val path = savedStateHandle.get<String>(IMAGE_PREVIEW)
}