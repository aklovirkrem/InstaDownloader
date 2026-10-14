package com.instagrab.app.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.instagrab.app.data.*
import com.instagrab.app.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MainViewModel, onSettingsClick: () -> Unit) {
    val postState by viewModel.postState.collectAsState()
    val downloadStates by viewModel.downloadStates.collectAsState()
    val urlInput by viewModel.urlInput.collectAsState()
    val snackMessage by viewModel.snackMessage.collectAsState()
    val serverConnected by viewModel.serverConnected.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val clipboard = LocalClipboardManager.current

    LaunchedEffect(snackMessage) {
        snackMessage?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            viewModel.clearSnack()
        }
    }

    Scaffold(
        containerColor = BgDark,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BgDark),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp))
                                .background(Brush.linearGradient(listOf(Purple80, Violet80, Cyan80))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Download, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Text("InstaGrab", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp,
                            style = MaterialTheme.typography.titleLarge.copy(
                                brush = Brush.linearGradient(listOf(Purple80, Violet80, Cyan80))
                            ))
                    }
                },
                actions = {
                    // Server status
                    Box(modifier = Modifier.padding(end = 4.dp).size(8.dp).clip(androidx.compose.foundation.shape.CircleShape)
                        .background(when (serverConnected) { true -> SuccessColor; false -> ErrorColor; else -> MutedText }))
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Filled.Settings, "Ayarlar", tint = MutedText)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // URL Input
            UrlInputCard(
                url = urlInput,
                onUrlChange = viewModel::onUrlChanged,
                onFetch = { keyboardController?.hide(); viewModel.fetchPost() },
                onPaste = {
                    val text = clipboard.getText()?.text ?: ""
                    viewModel.onUrlChanged(text)
                    if (text.isNotBlank()) viewModel.fetchPost(text)
                },
                isLoading = postState is PostState.Loading
            )

            AnimatedVisibility(visible = postState is PostState.Error) {
                val msg = (postState as? PostState.Error)?.message ?: ""
                ErrorCard(msg)
            }

            AnimatedVisibility(
                visible = postState is PostState.Success,
                enter = fadeIn() + slideInVertically { it / 4 },
                exit = fadeOut()
            ) {
                (postState as? PostState.Success)?.post?.let { post ->
                    PostResultCard(post, downloadStates,
                        onDownload = { idx -> viewModel.downloadVideo(post.shortcode, idx) },
                        onClear = viewModel::clearPost)
                }
            }

            if (postState is PostState.Idle) HowToSection()
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun UrlInputCard(url: String, onUrlChange: (String) -> Unit, onFetch: () -> Unit, onPaste: () -> Unit, isLoading: Boolean) {
    Card(modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, BorderDark)) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("INSTAGRAM LİNKİ", style = MaterialTheme.typography.labelSmall, color = MutedText, letterSpacing = 1.5.sp)
            OutlinedTextField(
                value = url, onValueChange = onUrlChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("instagram.com/p/ veya /reel/", color = MutedText, fontSize = 14.sp) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Go),
                keyboardActions = KeyboardActions(onGo = { onFetch() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Purple80, unfocusedBorderColor = BorderDark,
                    focusedContainerColor = Surface2, unfocusedContainerColor = Surface2,
                    cursorColor = Purple80, focusedTextColor = Color(0xFFF0F0F8), unfocusedTextColor = Color(0xFFF0F0F8)
                ),
                shape = RoundedCornerShape(14.dp),
                trailingIcon = if (url.isNotBlank()) {{ IconButton({ onUrlChange("") }) { Icon(Icons.Filled.Clear, null, tint = MutedText) } }} else null
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(onClick = onPaste, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderDark), colors = ButtonDefaults.outlinedButtonColors(contentColor = MutedText)) {
                    Icon(Icons.Filled.ContentPaste, null, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(6.dp)); Text("Yapıştır", fontSize = 13.sp)
                }
                Button(onClick = onFetch, enabled = !isLoading && url.isNotBlank(), modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Purple80, disabledContainerColor = Purple80.copy(.4f))) {
                    if (isLoading) {
                        CircularProgressIndicator(Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp)); Text("Yükleniyor…", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Filled.Search, null, Modifier.size(16.dp)); Spacer(Modifier.width(6.dp)); Text("Getir", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorCard(message: String) {
    Card(modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ErrorColor.copy(.08f)),
        shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, ErrorColor.copy(.3f))) {
        Row(Modifier.padding(14.dp), Alignment.CenterVertically, Arrangement.spacedBy(10.dp)) {
            Icon(Icons.Filled.ErrorOutline, null, tint = ErrorColor, modifier = Modifier.size(20.dp))
            Text(message, color = ErrorColor, fontSize = 13.sp, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun PostResultCard(post: PostInfoResponse, downloadStates: Map<Int, DownloadState>, onDownload: (Int) -> Unit, onClear: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, BorderDark)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AsyncImage(model = post.thumbnail, contentDescription = null,
                    modifier = Modifier.size(72.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
                Column(Modifier.weight(1f)) {
                    Text("@${post.owner}", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Purple80)
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        if ((post.likes ?: 0) > 0) Text("❤️ ${(post.likes ?: 0).fmtCount()}", fontSize = 11.sp, color = MutedText)
                        if (post.date.isNotBlank()) Text("📅 ${post.date}", fontSize = 11.sp, color = MutedText)
                        Text("🎬 ${post.videoCount}", fontSize = 11.sp, color = MutedText)
                    }
                    if (!post.caption.isNullOrBlank()) {
                        Spacer(Modifier.height(3.dp))
                        Text(post.caption, fontSize = 12.sp, color = MutedText, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                }
                IconButton(onClick = onClear, modifier = Modifier.size(30.dp)) {
                    Icon(Icons.Filled.Close, null, tint = MutedText, modifier = Modifier.size(18.dp))
                }
            }
            HorizontalDivider(color = BorderDark)
            Text("VİDEOLAR", style = MaterialTheme.typography.labelSmall, color = MutedText, letterSpacing = 1.5.sp)
            post.videos.forEach { video ->
                VideoRow(video, downloadStates[video.index] ?: DownloadState.Idle, { onDownload(video.index) })
            }
        }
    }
}

@Composable
fun VideoRow(video: VideoItem, state: DownloadState, onDownload: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface2),
        shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, BorderDark)) {
        Row(Modifier.padding(12.dp), Alignment.CenterVertically, Arrangement.spacedBy(12.dp)) {
            Box(Modifier.size(64.dp, 48.dp).clip(RoundedCornerShape(8.dp)).background(BgDark), Alignment.Center) {
                AsyncImage(model = video.thumbnail, contentDescription = null,
                    modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                Box(Modifier.fillMaxSize().background(Color.Black.copy(.3f)), Alignment.Center) {
                    Icon(Icons.Filled.PlayCircle, null, tint = Color.White.copy(.9f), modifier = Modifier.size(22.dp))
                }
            }
            Column(Modifier.weight(1f)) {
                Text("Video ${video.index + 1}", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                val sub = buildString {
                    video.views?.let { append("${it.fmtCount()} görüntüleme • ") }
                    video.duration?.let { append("${it.toInt()}s • ") }
                    append("MP4")
                }
                Text(sub, fontSize = 11.sp, color = MutedText)
                if (state is DownloadState.Downloading) {
                    Spacer(Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { state.progress / 100f },
                        modifier = Modifier.fillMaxWidth().height(3.dp).clip(RoundedCornerShape(2.dp)),
                        color = Purple80, trackColor = BorderDark)
                }
            }
            when (state) {
                is DownloadState.Idle, is DownloadState.Error ->
                    FilledTonalButton(onClick = onDownload, shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = Purple80.copy(.15f), contentColor = Purple80),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)) {
                        Icon(Icons.Filled.Download, null, Modifier.size(15.dp)); Spacer(Modifier.width(4.dp)); Text("İndir", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                is DownloadState.Downloading ->
                    CircularProgressIndicator(Modifier.size(28.dp), color = Purple80, strokeWidth = 2.5.dp)
                is DownloadState.Success ->
                    Icon(Icons.Filled.CheckCircle, null, tint = SuccessColor, modifier = Modifier.size(28.dp))
            }
        }
    }
}

@Composable
fun HowToSection() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("NASIL KULLANILIR", style = MaterialTheme.typography.labelSmall, color = MutedText, letterSpacing = 1.5.sp)
        listOf(
            Triple("1", "Linki Kopyala", "Instagram'da ··· menüsü → Bağlantıyı Kopyala"),
            Triple("2", "Yapıştır & Getir", "Yapıştır butonuna bas, otomatik arar"),
            Triple("3", "İndir", "Video kartındaki İndir butonuna bas"),
            Triple("⚙️", "Sunucu Ayarla", "Ayarlar'dan bilgisayarının IP'sini gir, %100 başarı"),
        ).forEach { (n, title, desc) ->
            Card(modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, BorderDark)) {
                Row(Modifier.padding(14.dp), Alignment.CenterVertically, Arrangement.spacedBy(12.dp)) {
                    Box(Modifier.size(34.dp).clip(RoundedCornerShape(9.dp))
                        .background(Brush.linearGradient(listOf(Purple80, Violet80))), Alignment.Center) {
                        Text(n, fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 14.sp)
                    }
                    Column { Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp); Text(desc, fontSize = 12.sp, color = MutedText) }
                }
            }
        }
    }
}

fun Long.fmtCount(): String = when {
    this >= 1_000_000 -> "%.1fM".format(this / 1_000_000.0)
    this >= 1_000     -> "%.1fK".format(this / 1_000.0)
    else              -> toString()
}
