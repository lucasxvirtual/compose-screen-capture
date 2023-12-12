package com.lucasxvirtual.composescreencapture

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.graphics.applyCanvas
import androidx.core.view.doOnLayout

@Composable
fun ScreenCapture(
    screenCaptureState: ScreenCaptureState,
    content: @Composable () -> Unit
) {
    val viewLocal = LocalView.current
    val context = LocalContext.current
    val activity = try {
        context.findActivity()
    } catch (t: Throwable) {
        screenCaptureState.updateImageState(ScreenShotResult.Error(t))
        return
    }
    LaunchedEffect(screenCaptureState.capture) {
        if (!screenCaptureState.capture)
            return@LaunchedEffect
        takeScreenShot(
            activity = activity,
            viewLocal = viewLocal,
            content = content,
            screenCaptureState = screenCaptureState
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
    screenCaptureState: ScreenCaptureState
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
            screenCaptureState.options?.width ?: ViewGroup.LayoutParams.MATCH_PARENT,
            screenCaptureState.options?.height ?: viewLocal.measuredHeight
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
            screenCaptureState.updateImageState(ScreenShotResult.Success(bitmap))
            (it as ComposeView).cleanUp(frame)
        }
    } catch (t: Throwable) {
        view.cleanUp(frame)
        screenCaptureState.updateImageState(ScreenShotResult.Error(t))
    }
}

private fun ComposeView.cleanUp(
    frame: ViewGroup
) {
    disposeComposition()
    frame.removeAllViews()
    (frame.rootView as? ViewGroup)?.removeView(frame)
}
