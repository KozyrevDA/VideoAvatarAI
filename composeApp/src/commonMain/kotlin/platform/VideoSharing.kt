package platform

/**
 * expect/actual для сохранения видео и публикации в сторис.
 * Android: MediaStore + Intent.ACTION_SEND + Instagram deep link
 * iOS: Photos framework + UIActivityViewController + Instagram URL scheme
 */

/** Результат операции сохранения/шаринга */
sealed class ShareResult {
    object Success : ShareResult()
    data class Error(val message: String) : ShareResult()
}

expect class VideoSharing {
    /**
     * Скачать видео из URL и сохранить в галерею (MediaStore/Photos).
     * Показывает прогресс и уведомление по завершении.
     */
    suspend fun saveToGallery(videoUrl: String, fileName: String): ShareResult

    /**
     * Открыть системный ShareSheet с видео.
     * Пользователь сам выбирает куда отправить.
     */
    suspend fun shareVideo(videoUrl: String, text: String): ShareResult

    /**
     * Публикация в Instagram Сторис.
     * Если Instagram не установлен — предлагает ShareSheet.
     */
    suspend fun shareToInstagramStories(videoUrl: String): ShareResult

    /**
     * Публикация в TikTok.
     * Открывает TikTok с выбранным видео через SDK/Intent.
     */
    suspend fun shareToTikTok(videoUrl: String): ShareResult

    /**
     * Копировать текст в буфер обмена.
     */
    fun copyToClipboard(text: String)
}

expect fun getVideoSharing(): VideoSharing
