package com.thebluealliance.android.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import androidx.core.graphics.createBitmap

/**
 * Modify a bitmap to round the corners by a given radius.
 * @param radius The radius of the corners in pixels
 */
fun Bitmap.addRoundedCorners(radius: Float): Bitmap {
    val output = createBitmap(width, height)
    val canvas = Canvas(output)
    val paint = Paint().apply {
        isAntiAlias = true
    }
    val rect = Rect(0, 0, width, height)
    val rectF = RectF(rect)

    canvas.drawRoundRect(rectF, radius, radius, paint)
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(this, rect, rect, paint)
    return output
}
