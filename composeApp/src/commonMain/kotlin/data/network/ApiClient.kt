package data.network

import data.model.GenerateAvatarRequest
import data.model.GenerateIdeasRequest
import data.model.GenerateTextPostRequest
import data.model.GenerationResult
import data.model.IdeasResult
import data.model.TextPostResult
import data.model.TranslateVideoRequest
import data.model.VideoItem
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class ApiClient(
    private val baseUrl: String,
    private val token: String,
) {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    suspend fun generateAvatar(request: GenerateAvatarRequest): GenerationResult {
        return client.post("$baseUrl/api/avatar/generate") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun checkGenerationStatus(id: String): GenerationResult {
        return client.get("$baseUrl/api/generation/$id/status") {
            bearerAuth(token)
        }.body()
    }

    suspend fun generateTextPost(request: GenerateTextPostRequest): TextPostResult {
        return client.post("$baseUrl/api/text/generate") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun generateIdeas(request: GenerateIdeasRequest): IdeasResult {
        return client.post("$baseUrl/api/ideas/generate") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun translateVideo(request: TranslateVideoRequest): GenerationResult {
        return client.post("$baseUrl/api/translate/generate") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun getHistory(): List<VideoItem> {
        return client.get("$baseUrl/api/history") {
            bearerAuth(token)
        }.body()
    }

    suspend fun getVideo(id: String): VideoItem {
        return client.get("$baseUrl/api/video/$id") {
            bearerAuth(token)
        }.body()
    }

    suspend fun confirmPurchase(productId: String, purchaseToken: String, store: String): Boolean {
        return try {
            client.post("$baseUrl/api/billing/confirm") {
                bearerAuth(token)
                contentType(ContentType.Application.Json)
                setBody(mapOf(
                    "productId" to productId,
                    "purchaseToken" to purchaseToken,
                    "store" to store
                ))
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}
