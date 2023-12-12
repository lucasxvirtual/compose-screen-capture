package com.lucasxvirtual.composescreencapture

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.graphics.applyCanvas
import androidx.core.view.doOnLayout

sealed class ScreenShotResult {
    data class Success(val bitmap: Bitmap): ScreenShotResult()
    data class Error(val throwable: Throwable): ScreenShotResult()
}

data class ScreenCaptureOptions(
    val width: Int? = null,
    val height: Int? = null
)

@Composable
fun ScreenCapture(
    takeScreenCapture: State<Boolean>,
    options: ScreenCaptureOptions? = null,
    onResult: (ScreenShotResult) -> Unit,
    content: @Composable () -> Unit
) {
    val viewLocal = LocalView.current
    val context = LocalContext.current
    val activity = try {
        context.findActivity()
    } catch (t: Throwable) {
        onResult(ScreenShotResult.Error(t))
        return
    }
    LaunchedEffect(takeScreenCapture) {
        if (!takeScreenCapture.value)
            return@LaunchedEffect
        takeScreenShot(
            activity = activity,
            viewLocal = viewLocal,
            content = content,
            onResult = onResult,
            options = options
        )
    }
}

private fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Did not find activity")
}

private fun takeScreenShot(
    activity: Activity,
    viewLocal: View,
    content: @Composable () -> Unit,
    onResult: (ScreenShotResult) -> Unit,
    options: ScreenCaptureOptions? = null
) {
    val view = ComposeView(activity).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
        // Makes it so that the user can't see the screen
        alpha = 0F
    }

    // Use this frame layout so that we don't block touch events from this invisible view
    val frame = NoTouchFrameLayout(activity).apply {
        layoutParams = ViewGroup.LayoutParams(
            options?.width ?: ViewGroup.LayoutParams.MATCH_PARENT,
            options?.height ?: viewLocal.measuredHeight
        )
        alpha = 0F
        addView(view)
    }

    (viewLocal.rootView as? ViewGroup)?.addView(frame)

    try {
        view.setContent(content)
        view.doOnLayout {
            val bitmap = Bitmap.createBitmap(
                it.measuredWidth,
                it.measuredHeight,
                Bitmap.Config.ARGB_8888
            ).applyCanvas {
                it.draw(this)
            }
            onResult(ScreenShotResult.Success(bitmap))
            (it as ComposeView).cleanUp(frame)
        }
    } catch (t: Throwable) {
        view.cleanUp(frame)
        onResult(ScreenShotResult.Error(t))
    }
}

private fun ComposeView.cleanUp(
    frame: ViewGroup
) {
    disposeComposition()
    frame.removeAllViews()
    (frame.rootView as? ViewGroup)?.removeView(frame)
}
