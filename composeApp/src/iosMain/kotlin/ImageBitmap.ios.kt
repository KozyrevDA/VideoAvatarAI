import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image
import platform.Foundation.NSData
import platform.Foundation.base64EncodedStringWithOptions
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation

actual fun ImageBitmap.toPngByteArray(): ByteArray {
    val skiaImage = this.asSkiaImage()
    return skiaImage.encodeToData()?.bytes ?: ByteArray(0)
}

actual fun ByteArray.toImageBitmap(): ImageBitmap =
    Image.makeFromEncoded(this).toComposeImageBitmap()

actual fun ImageBitmap.toBase64(): String {
    val bytes = toPngByteArray()
    val nsData = bytes.toNSData()
    return nsData.base64EncodedStringWithOptions(0u)
}

private fun ByteArray.toNSData(): NSData {
    return this.usePinned { pinned ->
        NSData.create(bytes = pinned.addressOf(0), length = this.size.toULong())
    }
}
