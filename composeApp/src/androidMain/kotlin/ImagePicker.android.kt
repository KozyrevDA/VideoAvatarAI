import android.Manifest
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext

class AndroidImagePicker : ImagePicker {

    @Composable
    override fun PickImageFromGallery(
        showPicker: MutableState<Boolean>,
        onPicked: (ImageBitmap) -> Unit,
    ) {
        val context = LocalContext.current
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                val bitmap = decodeBitmapSafely(context, it)
                if (bitmap != null) onPicked(bitmap)
            }
            showPicker.value = false
        }

        LaunchedEffect(showPicker.value) {
            if (showPicker.value) {
                launcher.launch("image/*")
            }
        }
    }

    @Composable
    override fun RequestGalleryPermission(onGranted: () -> Unit) {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            if (result.values.all { it }) onGranted()
        }
        LaunchedEffect(Unit) { launcher.launch(permission) }
    }
}

actual fun getImagePicker(): ImagePicker = AndroidImagePicker()

private fun decodeBitmapSafely(context: Context, uri: Uri): ImageBitmap? {
    return try {
        context.contentResolver.openInputStream(uri)?.use { stream ->
            // Sample down large images to avoid OOM
            val options = android.graphics.BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(stream, null, options)
            val maxSize = 1024
            options.inSampleSize = calculateInSampleSize(options, maxSize, maxSize)
            options.inJustDecodeBounds = false
            context.contentResolver.openInputStream(uri)?.use { s ->
                BitmapFactory.decodeStream(s, null, options)?.asImageBitmap()
            }
        }
    } catch (e: Exception) {
        null
    }
}

private fun calculateInSampleSize(
    options: android.graphics.BitmapFactory.Options,
    reqWidth: Int,
    reqHeight: Int,
): Int {
    val (height, width) = options.outHeight to options.outWidth
    var inSampleSize = 1
    if (height > reqHeight || width > reqWidth) {
        val halfHeight = height / 2
        val halfWidth = width / 2
        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}
