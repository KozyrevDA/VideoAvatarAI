package platform

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.nla.videoavataraii.app.AndroidApp
import java.io.File
import java.net.URL

actual class VideoSharing {
    private val ctx: Context get() = AndroidApp.instance

    actual suspend fun saveToGallery(videoUrl: String, fileName: String): ShareResult =
        withContext(Dispatchers.IO) {
            try {
                val bytes = URL(videoUrl).readBytes()
                val name = if (fileName.endsWith(".mp4")) fileName else "$fileName.mp4"

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Android 10+ — MediaStore API
                    val values = ContentValues().apply {
                        put(MediaStore.Video.Media.DISPLAY_NAME, name)
                        put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                        put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/НейросетьВидео")
                        put(MediaStore.Video.Media.IS_PENDING, 1)
                    }
                    val resolver = ctx.contentResolver
                    val uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
                        ?: return@withContext ShareResult.Error("Не удалось создать файл в галерее")

                    resolver.openOutputStream(uri)?.use { it.write(bytes) }
                    values.clear()
                    values.put(MediaStore.Video.Media.IS_PENDING, 0)
                    resolver.update(uri, values, null, null)
                } else {
                    // Android 9 и ниже
                    val dir = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
                        "НейросетьВидео"
                    ).also { it.mkdirs() }
                    File(dir, name).writeBytes(bytes)
                }
                ShareResult.Success
            } catch (e: Exception) {
                ShareResult.Error(e.message ?: "Ошибка сохранения")
            }
        }

    actual suspend fun shareVideo(videoUrl: String, text: String): ShareResult =
        withContext(Dispatchers.IO) {
            try {
                val bytes = URL(videoUrl).readBytes()
                val file = File(ctx.cacheDir, "share_video.mp4").also { it.writeBytes(bytes) }
                val uri = FileProvider.getUriForFile(ctx, "${ctx.packageName}.fileprovider", file)

                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "video/mp4"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_TEXT, text)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                ctx.startActivity(Intent.createChooser(intent, "Поделиться видео").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
                ShareResult.Success
            } catch (e: Exception) {
                ShareResult.Error(e.message ?: "Ошибка шаринга")
            }
        }

    actual suspend fun shareToInstagramStories(videoUrl: String): ShareResult =
        withContext(Dispatchers.IO) {
            try {
                val bytes = URL(videoUrl).readBytes()
                val file = File(ctx.cacheDir, "ig_story.mp4").also { it.writeBytes(bytes) }
                val uri = FileProvider.getUriForFile(ctx, "${ctx.packageName}.fileprovider", file)

                // Instagram Stories deep link
                val intent = Intent("com.instagram.share.ADD_TO_STORY").apply {
                    setDataAndType(uri, "video/mp4")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    `package` = "com.instagram.android"
                }

                val pm = ctx.packageManager
                if (pm.resolveActivity(intent, 0) != null) {
                    ctx.startActivity(intent.apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })
                    ShareResult.Success
                } else {
                    // Instagram не установлен — fallback на ShareSheet
                    shareVideo(videoUrl, "Смотри моё AI-видео! 🎬")
                }
            } catch (e: Exception) {
                ShareResult.Error(e.message ?: "Ошибка публикации в Instagram")
            }
        }

    actual suspend fun shareToTikTok(videoUrl: String): ShareResult =
        withContext(Dispatchers.IO) {
            try {
                val bytes = URL(videoUrl).readBytes()
                val file = File(ctx.cacheDir, "tiktok_video.mp4").also { it.writeBytes(bytes) }
                val uri = FileProvider.getUriForFile(ctx, "${ctx.packageName}.fileprovider", file)

                // TikTok Share API через Intent
                val intent = Intent("com.zhiliaoapp.musically.intent.SHARE_VIDEO").apply {
                    type = "video/mp4"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
                    `package` = "com.zhiliaoapp.musically"
                }

                val pm = ctx.packageManager
                if (pm.resolveActivity(intent, 0) != null) {
                    ctx.startActivity(intent)
                    ShareResult.Success
                } else {
                    // TikTok не установлен
                    shareVideo(videoUrl, "Смотри моё AI-видео! 🎬")
                }
            } catch (e: Exception) {
                ShareResult.Error(e.message ?: "Ошибка публикации в TikTok")
            }
        }

    actual fun copyToClipboard(text: String) {
        val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("text", text))
    }
}

actual fun getVideoSharing(): VideoSharing = VideoSharing()
