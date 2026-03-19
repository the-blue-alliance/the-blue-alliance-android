package com.thebluealliance.android.wear.complication

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Base64
import androidx.wear.watchface.complications.data.MonochromaticImage
import androidx.core.graphics.createBitmap
import android.graphics.drawable.Icon

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

    fun toMonochromaticImage(base64: String): MonochromaticImage? {
        val bitmap = decodeBase64(base64) ?: return null
        val mask = toAlphaMask(bitmap)
        return MonochromaticImage.Builder(Icon.createWithBitmap(mask)).build()
    }

    private fun decodeBase64(base64: String): Bitmap? {
        return try {
            val bytes = Base64.decode(base64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            null
        }
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
            val lum = (0.299 * Color.red(pixel) + 0.587 * Color.green(pixel) + 0.114 * Color.blue(pixel)).toInt().coerceIn(0, 255)
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
