package com.ai.assistance.operit.ui.main.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ai.assistance.operit.R
import com.ai.assistance.operit.data.preferences.DisplayPreferencesManager
import com.ai.assistance.operit.ui.main.components.MeiningBackgroundStore
import com.ai.assistance.operit.ui.theme.MeiningDaiQing
import com.ai.assistance.operit.ui.theme.MeiningGold
import com.ai.assistance.operit.ui.theme.MeiningInk
import com.ai.assistance.operit.ui.theme.MeiningMoonWhite
import kotlinx.coroutines.launch
import java.io.File

/**
 * 梅凝：背景头像设置页（R4/R6 + R5）。
 * 1) 为 11 个页面更换背景（内置恢复 / 图库选图）
 * 2) 更换用户头像（图库选图，全局生效）
 */
@Composable
fun BackgroundAvatarScreen(onGoBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val backgrounds = remember { MeiningBackgroundStore.builtinBackgrounds() }
    var pendingPage by remember { mutableStateOf<MeiningBackgroundStore.BuiltinBg?>(null) }
    var toast by remember { mutableStateOf("") }

    // 图库选图（背景）
    val bgPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null && pendingPage != null) {
            val page = pendingPage!!
            val ok = runCatching {
                val input = context.contentResolver.openInputStream(uri) ?: return@runCatching false
                val tmp = File(context.cacheDir, "bg_pick_tmp.jpg")
                tmp.outputStream().use { out -> input.copyTo(out) }
                MeiningBackgroundStore.setCustomBackground(context, page.pageKey, tmp)
            }.getOrDefault(false)
            toast = if (ok) "${page.label}背景已更换" else "设置失败，请换一张图片试试"
        }
        pendingPage = null
    }

    // 图库选图（头像）
    val avatarPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val ok = runCatching {
                val input = context.contentResolver.openInputStream(uri) ?: return@runCatching false
                val dest = File(context.filesDir, "meining_user_avatar.jpg")
                dest.outputStream().use { out -> input.copyTo(out) }
                scope.launch {
                    DisplayPreferencesManager.getInstance(context).saveDisplaySettings(
                        globalUserAvatarUri = Uri.fromFile(dest).toString()
                    )
                }
                true
            }.getOrDefault(false)
            toast = if (ok) "头像已更换" else "头像设置失败"
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_profile),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            Text(
                text = "背景头像",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MeiningDaiQing
            )
            Box(
                modifier = Modifier.padding(top = 6.dp).size(width = 44.dp, height = 3.dp)
                    .clip(RoundedCornerShape(2.dp)).background(MeiningGold.copy(alpha = 0.75f))
            )
            Spacer(modifier = Modifier.height(14.dp))

            // 头像区
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.9f),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).clickable { avatarPicker.launch("image/*") },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(72.dp).clip(CircleShape).background(MeiningGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_avatar_meining_main),
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "用户头像",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MeiningInk
                        )
                        Text(
                            text = "点击从图库选择图片更换头像（全局生效）",
                            style = MaterialTheme.typography.labelSmall,
                            color = MeiningInk.copy(alpha = 0.6f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "页面背景（点击更换）",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MeiningDaiQing
            )
            Spacer(modifier = Modifier.height(8.dp))

            backgrounds.forEach { bg ->
                val isCustom = MeiningBackgroundStore.isCustom(context, bg.pageKey)
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable {
                        pendingPage = bg
                        bgPicker.launch("image/*")
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.88f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(MeiningDaiQing.copy(alpha = 0.08f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🏞", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = bg.label,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MeiningInk
                            )
                            Text(
                                text = if (isCustom) "自定义背景" else "内置背景",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isCustom) MeiningGold else MeiningInk.copy(alpha = 0.5f)
                            )
                        }
                        if (isCustom) {
                            Text(
                                text = "恢复默认",
                                style = MaterialTheme.typography.labelMedium,
                                color = MeiningDaiQing,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        MeiningBackgroundStore.resetBackground(context, bg.pageKey)
                                        toast = "${bg.label}已恢复默认背景"
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        } else {
                            Text(
                                text = "更换",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MeiningDaiQing.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "提示：更换的背景立即生效，重新打开页面即可看到。",
                style = MaterialTheme.typography.labelSmall,
                color = MeiningInk.copy(alpha = 0.55f)
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (toast.isNotBlank()) {
        AlertDialog(
            onDismissRequest = { toast = "" },
            title = { Text(text = "设置结果") },
            text = { Text(text = toast) },
            confirmButton = {
                TextButton(onClick = { toast = "" }) { Text("好的") }
            }
        )
    }
}