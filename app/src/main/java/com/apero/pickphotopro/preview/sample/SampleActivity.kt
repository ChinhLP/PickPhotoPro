package com.apero.pickphotopro.preview.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.SavedStateViewModelFactory
import com.apero.pickphoto.VslPickPhotoEntry
import com.apero.pickphotopro.preview.di.DIContainer
import java.lang.ref.WeakReference

class SampleActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SampleScreen()
        }
    }
}

@Composable
fun SampleScreen() {
    val context = LocalActivity.current
    Scaffold(
        content = { paddingValues ->
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                TextButton(
                    onClick = {
                        VslPickPhotoEntry.getApi().openSdkPickPhoto(WeakReference(context))
                    }
                ) {
                    Text(text = "Open PickPhoto")
                }
            }
        }
    )
}

@Preview
@Composable
fun Preview() {
    SampleScreen()
}