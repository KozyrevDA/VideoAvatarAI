package utils

import utils.ImagePicker
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.BitmapFactory

class AndroidImagePicker : ImagePicker {

    @Composable
    override fun PickImageFromGallery(
        showPicker: MutableState<Boolean>,
        onPicked: (ImageBitmap) -> Unit,
    ) {
        val context = androidx.compose.ui.platform.LocalContext.current
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            showPicker.value = false
            if (uri != null) {
                try {
                    val stream = context.contentResolver.openInputStream(uri)
                    val bmp = BitmapFactory.decodeStream(stream)
                    stream?.close()
                    if (bmp != null) onPicked(bmp.asImageBitmap())
                } catch (_: Exception) { }
            }
        }
        if (showPicker.value) {
            LaunchedEffect(Unit) {
                launcher.launch("image/*")
            }
        }
    }

    @Composable
    override fun RequestGalleryPermission(onGranted: () -> Unit) = onGranted()
}

actual fun getImagePicker(): ImagePicker? = AndroidImagePicker()
