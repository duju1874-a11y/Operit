package com.ai.assistance.operit.ui.main.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import androidx.compose.ui.window.Popup
import androidx.compose.ui.unit.IntOffset
import com.ai.assistance.operit.R
import com.ai.assistance.operit.data.repository.ChatHistoryManager
import com.ai.assistance.operit.ui.main.screens.Screen
import com.ai.assistance.operit.ui.theme.MeiningDaiQing
import com.ai.assistance.operit.ui.theme.MeiningGold
import com.ai.assistance.operit.ui.theme.MeiningInk
import com.ai.assistance.operit.ui.theme.MeiningMoonWhite
import kotlinx.coroutines.runBlocking
import java.io.File

/**
 * 梅凝：铜铃"最近使用"快捷面板。
 * 展示：最近使用的工具 + 最近发送过的链接/图片/文件/视频（从最近聊天消息提取）。
 */
@Composable
fun RecentUsagePanel(
    onDismiss: () -> Unit,
    onOpenTool: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val recentTools = remember { RecentUsageStore.recentTools(context) }
    val recentItems = remember { extractRecentContent(context) }

    Popup(
        onDismissRequest = onDismiss,
        alignment = Alignment.TopEnd,
        offset = IntOffset(0, 56)
    ) {
        Surface(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(0.86f),
            shape = RoundedCornerShape(18.dp),
            color = MeiningMoonWhite.copy(alpha = 0.98f),
            shadowElevation = 12.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // 标题
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "最近使用",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MeiningDaiQing
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "点击关闭",
                        style = MaterialTheme.typography.labelSmall,
                        color = MeiningInk.copy(alpha = 0.5f),
                        modifier = Modifier.clickable(onClick = onDismiss)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .size(width = 44.dp, height = 3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MeiningGold.copy(alpha = 0.75f))
                )

                // 最近工具
                if (recentTools.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "常用工具",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MeiningInk.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        recentTools.take(4).forEach { entry ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        RecentUsageStore.recordTool(context, screenOf(entry))
                                        onOpenTool(screenOf(entry))
                                    }
                                    .padding(vertical = 6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MeiningDaiQing.copy(alpha = 0.08f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    androidx.compose.foundation.Image(
                                        painter = painterResource(entry.iconRes),
                                        contentDescription = entry.title,
                                        modifier = Modifier.size(40.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = entry.title,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MeiningInk,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                // 最近内容
                if (recentItems.isEmpty() && recentTools.isEmpty()) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "暂无最近记录，去使用工具或发送内容吧",
                        style = MaterialTheme.typography.bodySmall,
                        color = MeiningInk.copy(alpha = 0.55f),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else if (recentItems.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "最近内容",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MeiningInk.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyColumn(modifier = Modifier.heightIn(max = 240.dp)) {
                        items(recentItems.take(10)) { item ->
                            RecentContentRow(item = item, context = context)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentContentRow(item: RecentContentItem, context: Context) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { openRecentItem(context, item) }
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(MeiningGold.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = item.typeIcon,
                fontSize = 16.sp
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MeiningInk,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = item.typeLabel,
                style = MaterialTheme.typography.labelSmall,
                color = MeiningInk.copy(alpha = 0.5f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/** 面板内的内容条目 */
data class RecentContentItem(
    val type: String,       // link / image / file / video
    val typeIcon: String,   // 🔗 🖼 📄 🎬
    val typeLabel: String,  // 链接 / 图片 / 文件 / 视频
    val title: String,
    val uri: String
)

/** 从最近聊天消息提取最近发送过的链接/图片/文件/视频 */
fun extractRecentContent(context: Context): List<RecentContentItem> {
    val result = mutableListOf<RecentContentItem>()
    try {
        val manager = ChatHistoryManager.getInstance(context)
        val chats = manager.chatHistoriesFlow.value.take(3)
        val urlRegex = Regex("""https?://[^\s\)\]\}""]+""")
        for (chat in chats) {
            val messages = runBlocking { manager.loadChatMessages(chat.id, limit = 12) }
            for (msg in messages) {
                if (msg.sender != "user") continue
                val content = msg.content
                // 本地文件/视频路径
                val localRegex = Regex("""(/storage/[^\s\)\]\}"]+|/sdcard/[^\s\)\]\}"]+)""")
                for (m in localRegex.findAll(content)) {
                    val path = m.value.trim()
                    val lower = path.lowercase()
                    val type = when {
                        lower.endsWith(".mp4") || lower.endsWith(".mkv") || lower.endsWith(".mov") ||
                                lower.endsWith(".avi") || lower.endsWith(".webm") -> "video"
                        lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg") ||
                                lower.endsWith(".gif") || lower.endsWith(".webp") -> "image"
                        else -> "file"
                    }
                    result.add(
                        RecentContentItem(
                            type = type,
                            typeIcon = if (type == "video") "🎬" else if (type == "image") "🖼" else "📄",
                            typeLabel = if (type == "video") "视频" else if (type == "image") "图片" else "文件",
                            title = File(path).name,
                            uri = path
                        )
                    )
                }
                // URL
                for (m in urlRegex.findAll(content)) {
                    val url = m.value.trimEnd(',', '。', '；', ';', '！', '！', '?', '？')
                    val lower = url.lowercase()
                    val isImage =
                        lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg") ||
                                lower.endsWith(".gif") || lower.endsWith(".webp")
                    val isVideo =
                        lower.endsWith(".mp4") || lower.endsWith(".mkv") || lower.endsWith(".mov")
                    if (!isImage && !isVideo) {
                        result.add(
                            RecentContentItem(
                                type = "link",
                                typeIcon = "🔗",
                                typeLabel = "链接",
                                title = url.take(80),
                                uri = url
                            )
                        )
                    }
                }
            }
            if (result.size >= 10) break
        }
    } catch (_: Exception) {
        // 提取失败时静默返回已有结果
    }
    return result.distinctBy { it.uri }.take(10)
}

private fun openRecentItem(context: Context, item: RecentContentItem) {
    try {
        val intent =
            if (item.type == "link") {
                Intent(Intent.ACTION_VIEW, Uri.parse(item.uri))
            } else {
                Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(Uri.fromFile(File(item.uri)), "*/*")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } catch (_: Exception) {
        // 打不开时忽略
    }
}

/** 根据最近工具条目反查 Screen（用于跳转） */
private fun screenOf(entry: RecentToolEntry): Screen =
    when (entry.title) {
        "命令终端" -> Screen.Terminal
        "记忆库" -> Screen.MemoryBase
        "工具箱" -> Screen.Toolbox
        "插件市场" -> Screen.Packages
        "工作流" -> Screen.Workflow
        "助手配置" -> Screen.AssistantConfig
        "存储管理" -> Screen.StorageManage
        else -> Screen.AiChat
    }