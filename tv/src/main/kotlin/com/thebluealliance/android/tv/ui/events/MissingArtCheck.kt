package com.thebluealliance.android.tv.ui.events

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.graphics.createBitmap
import coil3.size.Size
import coil3.transform.Transformation
import com.thebluealliance.android.tv.data.model.WebcastThumbnails

/**
 * Fails the load when a decoded still is one of the CDNs' "no picture" frames (YouTube's narrow grey
 * slate, or a black pre-/post-stream frame), so the card keeps its placeholder. A Coil transformation
 * because it runs off the main thread on a software bitmap, and a failed result is never cached.
 */
object MissingArtCheck : Transformation() {
    override val cacheKey: String = "MissingArtCheck"

    // A 16×9 grid is plenty to tell a black frame from a picture, and cheap to scan.
    private const val SAMPLE_WIDTH = 16
    private const val SAMPLE_HEIGHT = 9

    override suspend fun transform(
        input: Bitmap,
        size: Size,
    ): Bitmap {
        if (!WebcastThumbnails.isUsableWidth(input.width)) {
            throw MissingArtException("still is only ${input.width}px wide")
        }
        if (WebcastThumbnails.isMostlyBlack(sampleLuminances(input))) {
            throw MissingArtException("still is a black frame")
        }
        return input
    }

    // Samples only the centre 16:9 band the card shows, so hqdefault's letterbox bars don't count
    // as black.
    private fun sampleLuminances(input: Bitmap): FloatArray {
        val bandHeight = minOf(input.height, input.width * SAMPLE_HEIGHT / SAMPLE_WIDTH)
        val top = (input.height - bandHeight) / 2
        val sample = createBitmap(SAMPLE_WIDTH, SAMPLE_HEIGHT)
        try {
            Canvas(sample).drawBitmap(
                input,
                Rect(0, top, input.width, top + bandHeight),
                Rect(0, 0, SAMPLE_WIDTH, SAMPLE_HEIGHT),
                Paint(Paint.FILTER_BITMAP_FLAG),
            )
            val pixels = IntArray(SAMPLE_WIDTH * SAMPLE_HEIGHT)
            sample.getPixels(pixels, 0, SAMPLE_WIDTH, 0, 0, SAMPLE_WIDTH, SAMPLE_HEIGHT)
            return FloatArray(pixels.size) { WebcastThumbnails.luminance(pixels[it]) }
        } finally {
            sample.recycle()
        }
    }
}

class MissingArtException(
    message: String,
) : Exception(message)
