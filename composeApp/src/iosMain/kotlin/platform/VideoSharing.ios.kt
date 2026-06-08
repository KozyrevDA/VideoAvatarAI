package platform

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.*
import platform.Photos.*
import platform.UIKit.*

actual class VideoSharing {

    actual suspend fun saveToGallery(videoUrl: String, fileName: String): ShareResult =
        withContext(Dispatchers.Main) {
            try {
                val url = NSURL(string = videoUrl)
                val data = NSData.dataWithContentsOfURL(url)
                    ?: return@withContext ShareResult.Error("Не удалось загрузить видео")

                val tempUrl = NSURL.fileURLWithPath(
                    NSTemporaryDirectory() + fileName.let {
                        if (it.endsWith(".mp4")) it else "$it.mp4"
                    }
                )
                data.writeToURL(tempUrl, atomically = true)

                var saveResult: ShareResult = ShareResult.Error("Неизвестная ошибка")

                PHPhotoLibrary.sharedPhotoLibrary().performChanges({
                    PHAssetChangeRequest.creationRequestForAssetFromVideoAtFileURL(tempUrl)
                }) { success, error ->
                    saveResult = if (success) ShareResult.Success
                    else ShareResult.Error(error?.localizedDescription ?: "Ошибка сохранения")
                }

                saveResult
            } catch (e: Exception) {
                ShareResult.Error(e.message ?: "Ошибка сохранения")
            }
        }

    actual suspend fun shareVideo(videoUrl: String, text: String): ShareResult =
        withContext(Dispatchers.Main) {
            try {
                val url = NSURL(string = videoUrl)
                val data = NSData.dataWithContentsOfURL(url)
                    ?: return@withContext ShareResult.Error("Не удалось загрузить видео")

                val tempUrl = NSURL.fileURLWithPath(NSTemporaryDirectory() + "share_video.mp4")
                data.writeToURL(tempUrl, atomically = true)

                val controller = UIActivityViewController(
                    activityItems = listOf(tempUrl, text),
                    applicationActivities = null,
                )

                // iOS 15+ compatible: connectedScenes вместо deprecated keyWindow
                UIApplication.sharedApplication.connectedScenes
                    .filterIsInstance<platform.UIKit.UIWindowScene>()
                    .firstOrNull()
                    ?.windows
                    ?.firstOrNull { it.isKeyWindow }
                    ?.rootViewController
                    ?.presentViewController(controller, animated = true, completion = null)

                ShareResult.Success
            } catch (e: Exception) {
                ShareResult.Error(e.message ?: "Ошибка шаринга")
            }
        }

    actual suspend fun shareToInstagramStories(videoUrl: String): ShareResult =
        withContext(Dispatchers.Main) {
            try {
                val instagramUrl = NSURL(string = "instagram-stories://share?source_application=нейросеть-видео")
                if (UIApplication.sharedApplication.canOpenURL(instagramUrl)) {
                    val url = NSURL(string = videoUrl)
                    val data = NSData.dataWithContentsOfURL(url)
                        ?: return@withContext ShareResult.Error("Не удалось загрузить видео")

                    val pasteboard = UIPasteboard.generalPasteboard
                    pasteboard.setData(data as NSData, forPasteboardType = "com.instagram.sharedSticker.backgroundVideo")

                    UIApplication.sharedApplication.openURL(instagramUrl)
                    ShareResult.Success
                } else {
                    // Instagram не установлен
                    shareVideo(videoUrl, "Смотри моё AI-видео! 🎬")
                }
            } catch (e: Exception) {
                ShareResult.Error(e.message ?: "Ошибка публикации в Instagram")
            }
        }

    actual suspend fun shareToTikTok(videoUrl: String): ShareResult =
        withContext(Dispatchers.Main) {
            try {
                // TikTok на iOS — через UIActivityViewController (нет прямого API без TikTok SDK)
                shareVideo(videoUrl, "Смотри моё AI-видео! 🎬 #нейросеть #AI")
            } catch (e: Exception) {
                ShareResult.Error(e.message ?: "Ошибка публикации в TikTok")
            }
        }

    actual fun copyToClipboard(text: String) {
        UIPasteboard.generalPasteboard.string = text
    }
}

actual fun getVideoSharing(): VideoSharing = VideoSharing()
