import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toPixelMap
import platform.Foundation.NSData
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned

actual fun ImageBitmap.toBase64(): String {
    // iOS: convert pixel map to base64
    val pixels = this.toPixelMap()
    val bytes = mutableListOf<Byte>()
    for (y in 0 until pixels.height) {
        for (x in 0 until pixels.width) {
            val c = pixels[x, y]
            bytes.add((c.red * 255).toInt().toByte())
            bytes.add((c.green * 255).toInt().toByte())
            bytes.add((c.blue * 255).toInt().toByte())
        }
    }
    return platform.Foundation.NSData.create(
        bytes = bytes.toByteArray().usePinned { it.addressOf(0) },
        length = bytes.size.toULong()
    ).base64EncodedStringWithOptions(0uL)
}
