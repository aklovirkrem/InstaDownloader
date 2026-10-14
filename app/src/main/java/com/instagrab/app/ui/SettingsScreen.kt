package com.instagrab.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.instagrab.app.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val serverUrl by viewModel.serverUrl.collectAsState()
    val serverConnected by viewModel.serverConnected.collectAsState()
    val keyboard = LocalSoftwareKeyboardController.current

    Scaffold(
        containerColor = BgDark,
        topBar = {
            TopAppBar(
                title = { Text("Ayarlar", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Geri") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgDark)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, BorderDark)) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(Modifier.size(32.dp).clip(RoundedCornerShape(8.dp))
                            .background(Brush.linearGradient(listOf(Purple80, Violet80))), Alignment.Center) {
                            Icon(Icons.Filled.Dns, null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                        Text("Sunucu Adresi", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }

                    Text(
                        "app.py'nin çalıştığı PC'nin IP adresi.\n• Emülatör: http://10.0.2.2:5000\n• Gerçek cihaz: http://192.168.x.x:5000\n\nIP öğrenmek: Windows → ipconfig, Mac → ifconfig",
                        fontSize = 12.sp, color = MutedText, lineHeight = 18.sp
                    )

                    OutlinedTextField(
                        value = serverUrl, onValueChange = viewModel::onServerUrlChanged,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Sunucu URL") },
                        placeholder = { Text("http://192.168.1.x:5000") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { keyboard?.hide(); viewModel.saveServerUrl() }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Purple80, unfocusedBorderColor = BorderDark,
                            focusedContainerColor = Surface2, unfocusedContainerColor = Surface2,
                            focusedTextColor = Color(0xFFF0F0F8), unfocusedTextColor = Color(0xFFF0F0F8), cursorColor = Purple80
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(onClick = viewModel::checkServerConnection, modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, BorderDark),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MutedText)) {
                            Icon(Icons.Filled.NetworkCheck, null, Modifier.size(16.dp)); Spacer(Modifier.width(6.dp)); Text("Test Et", fontSize = 13.sp)
                        }
                        Button(onClick = { keyboard?.hide(); viewModel.saveServerUrl() }, modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Purple80)) {
                            Icon(Icons.Filled.Save, null, Modifier.size(16.dp)); Spacer(Modifier.width(6.dp)); Text("Kaydet", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    val (statusColor, statusText) = when (serverConnected) {
                        true  -> SuccessColor to "✅ Bağlı"
                        false -> ErrorColor   to "❌ Bağlantı yok"
                        null  -> MutedText    to "⏳ Kontrol ediliyor…"
                    }
                    Text(statusText, color = statusColor, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }

            Card(modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, BorderDark)) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Uygulama Hakkında", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("InstaGrab v1.0", fontSize = 13.sp, color = MutedText)
                    Text("Instagram videolarını indirmek için yerel Python sunucusu (app.py) veya bulut API kullanır. Yalnızca izinli içerikleri indirin.",
                        fontSize = 12.sp, color = MutedText, lineHeight = 18.sp)
                }
            }
        }
    }
}
