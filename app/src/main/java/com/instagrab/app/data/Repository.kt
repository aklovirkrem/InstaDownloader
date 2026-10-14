package com.instagrab.app.data

import android.content.Context
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class InstagramRepository(private val context: Context) {

    private var api = ApiClient.create()

    fun updateServerUrl(url: String) {
        api = ApiClient.updateBaseUrl(url)
    }

    suspend fun checkHealth(): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = api.health()
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getPostInfo(url: String): Result<PostInfoResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.getPostInfo(UrlRequest(url))
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Bilinmeyen hata"
                Result.failure(Exception(parseError(errorBody)))
            }
        } catch (e: Exception) {
            Result.failure(Exception(friendlyError(e)))
        }
    }

    fun downloadVideo(shortcode: String, videoIndex: Int): Flow<DownloadProgress> = flow {
        emit(DownloadProgress.Started)
        try {
            val response = api.downloadVideo(DownloadRequest(shortcode, videoIndex))
            if (!response.isSuccessful) {
                val msg = response.errorBody()?.string() ?: "İndirme hatası"
                emit(DownloadProgress.Error(parseError(msg)))
                return@flow
            }

            val body = response.body() ?: run {
                emit(DownloadProgress.Error("Boş yanıt"))
                return@flow
            }

            val downloadsDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            )
            val instaDir = File(downloadsDir, "InstaGrab").apply { mkdirs() }
            val file = File(instaDir, "instagram_${shortcode}_${videoIndex + 1}.mp4")

            val totalBytes = body.contentLength()
            var downloadedBytes = 0L

            withContext(Dispatchers.IO) {
                body.byteStream().use { input ->
                    FileOutputStream(file).use { output ->
                        val buffer = ByteArray(8192)
                        var read: Int
                        while (input.read(buffer).also { read = it } != -1) {
                            output.write(buffer, 0, read)
                            downloadedBytes += read
                            if (totalBytes > 0) {
                                val progress = ((downloadedBytes * 100) / totalBytes).toInt()
                                emit(DownloadProgress.Progress(progress))
                            }
                        }
                    }
                }
            }

            emit(DownloadProgress.Done(file.absolutePath))
        } catch (e: Exception) {
            emit(DownloadProgress.Error(friendlyError(e)))
        }
    }

    private fun parseError(body: String): String {
        return try {
            val regex = "\"error\"\\s*:\\s*\"([^\"]+)\"".toRegex()
            regex.find(body)?.groupValues?.get(1) ?: body
        } catch (e: Exception) {
            body
        }
    }

    private fun friendlyError(e: Exception): String {
        return when {
            e.message?.contains("Unable to resolve host") == true ||
            e.message?.contains("Connection refused") == true ->
                "Sunucuya bağlanılamadı. app.py çalışıyor mu? Ayarlardan IP adresini kontrol edin."
            e.message?.contains("timeout") == true ->
                "Bağlantı zaman aşımına uğradı. İnternet bağlantınızı kontrol edin."
            else -> e.message ?: "Bilinmeyen hata"
        }
    }
}

sealed class DownloadProgress {
    object Started : DownloadProgress()
    data class Progress(val percent: Int) : DownloadProgress()
    data class Done(val filePath: String) : DownloadProgress()
    data class Error(val message: String) : DownloadProgress()
}
