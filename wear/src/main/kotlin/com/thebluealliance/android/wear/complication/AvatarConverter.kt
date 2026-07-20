package com.thebluealliance.android.wear.complication

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Icon
import android.util.Base64
import android.util.Log
import androidx.core.graphics.createBitmap
import androidx.wear.watchface.complications.data.MonochromaticImage
import java.util.concurrent.atomic.AtomicReference

/**
 * Converts a color avatar bitmap (base64-encoded PNG) into an alpha mask
 * suitable for MonochromaticImage. The watch face tints visible pixels
 * to match its color scheme.
 *
 * The conversion computes per-pixel luminance and uses it as the alpha
 * channel: bright pixels become opaque, dark pixels become transparent.
 * RGB is set to white so the tint color applies uniformly.
 */
object AvatarConverter {
    private const val TAG = "AvatarConverter"

    /** FRC avatars are 40x40; cap the decode so a malformed/huge PNG can't blow up memory. */
    private const val MAX_AVATAR_PX = 96

    /**
     * Single-entry memo of the last conversion. Only one team is tracked at a time, so the same
     * avatar is requested on every complication render; caching avoids re-running the decode +
     * two-pass luminance scan each time. Keyed by the source base64 so it self-invalidates when
     * the avatar changes. A failed decode caches null (skips re-attempting a corrupt image); the
     * complication process is short-lived, so a transient failure recovers on the next rebind.
     */
    private val cached = AtomicReference<Pair<String, MonochromaticImage?>?>(null)

    fun toMonochromaticImage(base64: String): MonochromaticImage? {
        cached.get()?.let { (key, image) -> if (key == base64) return image }
        val image =
            decodeBoundedBitmap(base64)?.let { bitmap ->
                MonochromaticImage.Builder(Icon.createWithBitmap(toAlphaMask(bitmap))).build()
            }
        cached.set(base64 to image)
        return image
    }

    /**
     * Decode a base64 PNG, downsampling an oversized image toward [MAX_AVATAR_PX] (via a
     * power-of-two [BitmapFactory.Options.inSampleSize]) so it never allocates at full resolution.
     * Catches [Throwable] (not just [Exception]) so an OutOfMemoryError from a hostile/oversized
     * image returns null instead of crashing the complication service or tracker.
     */
    fun decodeBoundedBitmap(base64: String): Bitmap? =
        try {
            val bytes = Base64.decode(base64, Base64.DEFAULT)
            val bounds =
                BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
            val options =
                BitmapFactory.Options().apply {
                    inSampleSize = calculateInSampleSize(bounds.outWidth, bounds.outHeight)
                }
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
        } catch (e: Throwable) {
            Log.w(TAG, "Failed to decode avatar", e)
            null
        }

    /** Largest power-of-two sample size that keeps both dimensions >= [MAX_AVATAR_PX]. */
    private fun calculateInSampleSize(
        width: Int,
        height: Int,
    ): Int {
        var sampleSize = 1
        while (width / (sampleSize * 2) >= MAX_AVATAR_PX &&
            height / (sampleSize * 2) >= MAX_AVATAR_PX
        ) {
            sampleSize *= 2
        }
        return sampleSize
    }

    /**
     * Convert a color bitmap to a white-on-transparent alpha mask.
     * Luminance becomes the alpha channel; RGB is set to white.
     */
    private fun toAlphaMask(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height
        val pixels = IntArray(width * height)
        source.getPixels(pixels, 0, width, 0, 0, width, height)

        // First pass: compute luminance and find min/max for normalization
        val luminances = IntArray(pixels.size)
        var minLum = 255
        var maxLum = 0
        for (i in pixels.indices) {
            val pixel = pixels[i]
            val lum =
                (
                    0.299 * Color.red(pixel) + 0.587 * Color.green(pixel) +
                        0.114 * Color.blue(pixel)
                ).toInt().coerceIn(0, 255)
            luminances[i] = lum
            if (lum < minLum) minLum = lum
            if (lum > maxLum) maxLum = lum
        }

        // Second pass: normalize luminance to full [0, 255] range as alpha
        val range = (maxLum - minLum).coerceAtLeast(1)
        for (i in pixels.indices) {
            val alpha = ((luminances[i] - minLum) * 255) / range
            pixels[i] = Color.argb(alpha, 255, 255, 255)
        }

        val result = createBitmap(width, height)
        result.setPixels(pixels, 0, width, 0, 0, width, height)
        return result
    }
}
