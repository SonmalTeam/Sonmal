package com.d202.sonmal.utils

import android.app.Activity
import android.content.Context
import android.graphics.Insets
import android.graphics.Point
import android.view.WindowInsets
import android.view.WindowManager

fun getDeviceSize(activity: Activity): Point {
    val windowManager = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    return windowManager.currentWindowMetricsPointCompat()
}

fun WindowManager.currentWindowMetricsPointCompat() : Point {
    // R(30) 이상
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
        val windowInsets = currentWindowMetrics.windowInsets
        var insets: Insets = windowInsets.getInsets(WindowInsets.Type.navigationBars())
        windowInsets.displayCutout?.run {
            insets = Insets.max(
                insets,
                Insets.of(safeInsetLeft, safeInsetTop, safeInsetRight, safeInsetBottom)
            )
        }
        val insetsWidth = insets.right + insets.left
        val insetsHeight = insets.top + insets.bottom
        Point(
            currentWindowMetrics.bounds.width() - insetsWidth,
            currentWindowMetrics.bounds.height() - insetsHeight
        )
    } else {
        Point().apply {
            defaultDisplay.getSize(this)
        }
    }
}