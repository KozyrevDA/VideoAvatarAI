package utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.dataWithContentsOfURL
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerConfigurationSelectionOrdered
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.UniformTypeIdentifiers.UTTypeImage
import platform.darwin.NSObject

class IosImagePicker : ImagePicker {

    @Composable
    override fun PickImageFromGallery(
        showPicker: MutableState<Boolean>,
        onPicked: (ImageBitmap) -> Unit,
    ) {
        if (showPicker.value) {
            LaunchedEffect(Unit) {
                presentPicker(
                    onPicked = { bitmap ->
                        showPicker.value = false
                        onPicked(bitmap)
                    },
                    onCancel = {
                        showPicker.value = false
                    },
                )
            }
        }
    }

    @Composable
    override fun RequestGalleryPermission(onGranted: () -> Unit) {
        // iOS: разрешение запрашивается автоматически при открытии PHPickerViewController
        // NSPhotoLibraryUsageDescription должен быть в Info.plist
        onGranted()
    }
}

actual fun getImagePicker(): ImagePicker = IosImagePicker()

// ── PHPickerViewController presentation ──────────────────────────────────────

@OptIn(ExperimentalForeignApi::class)
private fun presentPicker(
    onPicked: (ImageBitmap) -> Unit,
    onCancel: () -> Unit,
) {
    val config = PHPickerConfiguration().apply {
        filter = PHPickerFilter.imagesFilter
        selectionLimit = 1
        selection = PHPickerConfigurationSelectionOrdered
    }

    val picker = PHPickerViewController(configuration = config)
    val delegate = PickerDelegate(onPicked = onPicked, onCancel = onCancel, picker = picker)
    picker.delegate = delegate

    // iOS 15+ — используем connectedScenes
    val rootVC = UIApplication.sharedApplication
        .connectedScenes
        .filterIsInstance<platform.UIKit.UIWindowScene>()
        .firstOrNull()
        ?.windows
        ?.firstOrNull { (it as? platform.UIKit.UIWindow)?.isKeyWindow == true }
        ?.let { (it as? platform.UIKit.UIWindow)?.rootViewController }

    rootVC?.presentViewController(picker, animated = true, completion = null)
        ?: onCancel()
}

// ── Delegate ─────────────────────────────────────────────────────────────────

private class PickerDelegate(
    private val onPicked: (ImageBitmap) -> Unit,
    private val onCancel: () -> Unit,
    private val picker: UIViewController,
) : NSObject(), PHPickerViewControllerDelegateProtocol {

    @OptIn(ExperimentalForeignApi::class)
    override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
        picker.dismissViewControllerAnimated(true, completion = null)

        val results = didFinishPicking.filterIsInstance<PHPickerResult>()
        if (results.isEmpty()) {
            onCancel()
            return
        }

        val result = results.first()
        val provider = result.itemProvider

        if (provider.canLoadObjectOfClass(platform.UIKit.UIImage)) {
            provider.loadObjectOfClass(platform.UIKit.UIImage) { obj, error ->
                val uiImage = obj as? platform.UIKit.UIImage
                if (uiImage != null) {
                    val bitmap = uiImageToImageBitmap(uiImage)
                    if (bitmap != null) {
                        onPicked(bitmap)
                    } else {
                        onCancel()
                    }
                } else {
                    onCancel()
                }
            }
        } else {
            onCancel()
        }
    }
}

// ── UIImage → ImageBitmap ─────────────────────────────────────────────────────

@OptIn(ExperimentalForeignApi::class)
private fun uiImageToImageBitmap(uiImage: platform.UIKit.UIImage): ImageBitmap? {
    // Ресайзим до 1024px максимум (достаточно для аватара, меньше трафика)
    val maxDim = 1024.0
    val origW = uiImage.size.useContents { width }
    val origH = uiImage.size.useContents { height }
    val scale = if (origW > maxDim || origH > maxDim) minOf(maxDim / origW, maxDim / origH) else 1.0
    val targetW = (origW * scale).toInt()
    val targetH = (origH * scale).toInt()

    // Рисуем в CGContext → извлекаем байты → декодируем в Skia
    val colorSpace = platform.CoreGraphics.CGColorSpaceCreateDeviceRGB()
    val ctx = platform.CoreGraphics.CGBitmapContextCreate(
        data = null,
        width = targetW.toULong(),
        height = targetH.toULong(),
        bitsPerComponent = 8u,
        bytesPerRow = (targetW * 4).toULong(),
        space = colorSpace,
        bitmapInfo = platform.CoreGraphics.kCGImageAlphaPremultipliedLast.toUInt(),
    ) ?: return null

    platform.CoreGraphics.CGContextDrawImage(
        ctx,
        platform.CoreGraphics.CGRectMake(0.0, 0.0, targetW.toDouble(), targetH.toDouble()),
        uiImage.CGImage ?: return null,
    )

    val bytesPerRow = targetW * 4
    val totalBytes = bytesPerRow * targetH
    val rawBytes = ByteArray(totalBytes)

    rawBytes.usePinned { pinned ->
        platform.CoreGraphics.CGBitmapContextGetData(ctx)?.let { dataPtr ->
            kotlinx.cinterop.memcpy(pinned.addressOf(0), dataPtr, totalBytes.toULong())
        }
    }

    platform.CoreGraphics.CGContextRelease(ctx)
    platform.CoreGraphics.CGColorSpaceRelease(colorSpace)

    // RGBA bytes → Skia Image → ImageBitmap
    return try {
        val skiaImage = org.jetbrains.skia.Image.makeRaster(
            imageInfo = org.jetbrains.skia.ImageInfo(
                width = targetW,
                height = targetH,
                colorType = org.jetbrains.skia.ColorType.RGBA_8888,
                alphaType = org.jetbrains.skia.ColorAlphaType.PREMUL,
            ),
            bytes = rawBytes,
            rowBytes = bytesPerRow.toLong(),
        )
        org.jetbrains.skia.Image.makeFromEncoded(
            skiaImage.encodeToData(org.jetbrains.skia.EncodedImageFormat.JPEG, 90)?.bytes ?: return null
        ).let { img ->
            androidx.compose.ui.graphics.ImageBitmap(img.width, img.height)
        }
    } catch (e: Exception) {
        null
    }
}
