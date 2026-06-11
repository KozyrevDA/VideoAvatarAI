package utils

import androidx.compose.ui.graphics.ImageBitmap

expect fun ImageBitmap.toPngByteArray(): ByteArray
expect fun ByteArray.toImageBitmap(): ImageBitmap
expect fun ImageBitmap.toBase64(): String
