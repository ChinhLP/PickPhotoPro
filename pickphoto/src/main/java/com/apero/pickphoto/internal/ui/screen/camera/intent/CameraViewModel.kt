package com.apero.pickphoto.internal.ui.screen.camera.intent

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CameraViewModel(
    val savedStateHandle: SavedStateHandle
) : ViewModel() {
    companion object {
        const val CAMERA_UI_STATE = "camera_ui_state"
    }

    val cameraUiState: StateFlow<CameraState> = savedStateHandle.getStateFlow(
        CAMERA_UI_STATE,
        CameraState()
    )

    fun onEvent(intent: CameraIntent) {
        when (intent) {
            is CameraIntent.SetUriImageCapture -> {
                onImageCaptured(intent.uri)
            }
        }
    }

    private fun updateCameraUiState(update: CameraState.() -> CameraState) {
        val newState = cameraUiState.value.update()
        savedStateHandle[CAMERA_UI_STATE] = newState
    }

    private fun onImageCaptured(uri: Uri?) {
        viewModelScope.launch {
            updateCameraUiState { copy(uriImage = uri) }
        }
    }


}

