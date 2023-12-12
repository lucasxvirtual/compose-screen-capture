package com.lucasxvirtual.composescreencapture

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

sealed class ScreenShotResult {
    object Initial: ScreenShotResult()
    data class Success internal constructor(val bitmap: Bitmap): ScreenShotResult()
    data class Error internal constructor(val throwable: Throwable): ScreenShotResult()
}

data class ScreenCaptureOptions(
    val width: Int? = null,
    val height: Int? = null
)

@Composable
fun rememberScreenCaptureState() = remember {
    ScreenCaptureState()
}

class ScreenCaptureState internal constructor() {
    var imageState by mutableStateOf<ScreenShotResult>(ScreenShotResult.Initial)
        private set

    val bitmap by derivedStateOf {
        (imageState as? ScreenShotResult.Success)?.bitmap
    }

    internal var capture by mutableStateOf(false)
    internal var options: ScreenCaptureOptions? = null
        private set

    fun capture(options: ScreenCaptureOptions?) {
        capture = true
        this.options = options
    }

    internal fun updateImageState(newState: ScreenShotResult) {
        imageState = newState
        capture = false
        options = null
    }
}