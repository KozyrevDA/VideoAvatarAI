package utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toPixelMap
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.base64EncodedStringWithOptions
import platform.Foundation.create

/**
 * iOS implementation — конвертация через PixelMap без зависимости от Skia private API.
 * Генерирует минимальный BMP (простейший lossless формат без внешних зависимостей).
 */

actual fun ImageBitmap.toPngByteArray(): ByteArray = toBmpBytes()

actual fun ByteArray.toImageBitmap(): ImageBitmap {
    // Decode через Skia (встроен в CMP)
    val image = org.jetbrains.skia.Image.makeFromEncoded(this)
    return androidx.compose.ui.graphics.ImageBitmap(image.width, image.height)
}

@OptIn(ExperimentalForeignApi::class)
actual fun ImageBitmap.toBase64(): String {
    val bytes = toPngByteArray()
    if (bytes.isEmpty()) return ""
    return bytes.usePinned { pinned ->
        val nsData = NSData.create(bytes = pinned.addressOf(0), length = bytes.size.toULong())
        nsData.base64EncodedStringWithOptions(0u)
    }
}

/** Кодирует ImageBitmap в BMP формат (24-bit, без сжатия). */
private fun ImageBitmap.toBmpBytes(): ByteArray {
    val pixelMap = toPixelMap()
    val w = pixelMap.width
    val h = pixelMap.height
    val rowSize = (w * 3 + 3) / 4 * 4  // выравнивание до 4 байт
    val pixelData = rowSize * h
    val fileSize = 54 + pixelData

    val buf = ByteArray(fileSize)
    var i = 0
    fun writeInt(v: Int) { buf[i++]=v.toByte(); buf[i++]=(v shr 8).toByte(); buf[i++]=(v shr 16).toByte(); buf[i++]=(v shr 24).toByte() }
    fun writeShort(v: Int) { buf[i++]=v.toByte(); buf[i++]=(v shr 8).toByte() }
    fun writeByte(v: Int) { buf[i++]=v.toByte() }

    // BMP file header
    writeByte(0x42); writeByte(0x4D)  // "BM"
    writeInt(fileSize); writeInt(0); writeInt(54)
    // DIB header (BITMAPINFOHEADER)
    writeInt(40); writeInt(w); writeInt(-h)  // -h = top-down
    writeShort(1); writeShort(24); writeInt(0); writeInt(pixelData)
    writeInt(2835); writeInt(2835); writeInt(0); writeInt(0)

    // Pixel data (bottom-up because we used -h → actually top-down)
    for (y in 0 until h) {
        var rowBytes = 0
        for (x in 0 until w) {
            val px = pixelMap[x, y]
            writeByte((px.blue  * 255).toInt().coerceIn(0,255))
            writeByte((px.green * 255).toInt().coerceIn(0,255))
            writeByte((px.red   * 255).toInt().coerceIn(0,255))
            rowBytes += 3
        }
        repeat(rowSize - rowBytes) { writeByte(0) }
    }
    return buf
}
