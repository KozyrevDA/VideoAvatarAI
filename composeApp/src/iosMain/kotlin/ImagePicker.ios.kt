import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.ImageBitmap

class IosImagePicker : ImagePicker {
    @Composable
    override fun PickImageFromGallery(
        showPicker: MutableState<Boolean>,
        onPicked: (ImageBitmap) -> Unit,
    ) {
        // TODO: implement via PHPickerViewController when showPicker.value == true
        // Requires UIKit interop — will be wired in iOSApp.swift
    }

    @Composable
    override fun RequestGalleryPermission(onGranted: () -> Unit) {
        // iOS permissions handled by Info.plist NSPhotoLibraryUsageDescription
        onGranted()
    }
}

actual fun getImagePicker(): ImagePicker = IosImagePicker()
