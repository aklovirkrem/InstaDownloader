package com.instagrab.app.data

import com.google.gson.annotations.SerializedName

// ── API Request Models ──────────────────────────────────────

data class UrlRequest(val url: String)

data class DownloadRequest(
    val shortcode: String,
    @SerializedName("video_index") val videoIndex: Int
)

// ── API Response Models ─────────────────────────────────────

data class PostInfoResponse(
    val shortcode: String,
    val owner: String,
    val caption: String?,
    val likes: Long?,
    val date: String,
    val thumbnail: String,
    val videos: List<VideoItem>,
    @SerializedName("video_count") val videoCount: Int
)

data class VideoItem(
    val index: Int,
    val url: String,
    val thumbnail: String,
    val duration: Float?,
    val views: Long?
)

data class ErrorResponse(val error: String)

// ── Download History ────────────────────────────────────────

data class DownloadRecord(
    val id: String = java.util.UUID.randomUUID().toString(),
    val shortcode: String,
    val owner: String,
    val thumbnail: String,
    val videoIndex: Int,
    val filePath: String,
    val downloadedAt: Long = System.currentTimeMillis(),
    val fileSize: Long = 0L
)

// ── UI State ────────────────────────────────────────────────

sealed class PostState {
    object Idle : PostState()
    object Loading : PostState()
    data class Success(val post: PostInfoResponse) : PostState()
    data class Error(val message: String) : PostState()
}

sealed class DownloadState {
    object Idle : DownloadState()
    data class Downloading(val videoIndex: Int, val progress: Int = 0) : DownloadState()
    data class Success(val videoIndex: Int, val filePath: String) : DownloadState()
    data class Error(val videoIndex: Int, val message: String) : DownloadState()
}
