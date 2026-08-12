package com.ai.assistance.operit.ui.main.screens

import android.os.Environment
import android.os.StatFs
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.ai.assistance.operit.ui.theme.MeiningDaiQing
import com.ai.assistance.operit.ui.theme.MeiningGold
import com.ai.assistance.operit.ui.theme.MeiningInk
import com.ai.assistance.operit.ui.theme.MeiningMoonWhite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest

/**
 * 梅凝：存储管理页（R7）。
 * 存储总览 + 一键智能清理 + 大文件榜 + 重复文件 + 旧 APK/QQ 缓存专项。
 */
@Composable
fun StorageManageScreen(onGoBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var totalBytes by remember { mutableStateOf(0L) }
    var freeBytes by remember { mutableStateOf(0L) }
    var bigFiles by remember { mutableStateOf<List<File>>(emptyList()) }
    var duplicateFiles by remember { mutableStateOf<List<File>>(emptyList()) }
    var oldApks by remember { mutableStateOf<List<File>>(emptyList()) }
    var qqCacheBytes by remember { mutableStateOf(0L) }
    var scanning by remember { mutableStateOf(false) }
    var cleaning by remember { mutableStateOf(false) }
    var lastCleanMsg by remember { mutableStateOf("") }

    fun refreshStorage() {
        try {
            val stat = StatFs(Environment.getDataDirectory().path)
            totalBytes = stat.totalBytes
            freeBytes = stat.availableBytes
        } catch (_: Exception) {
        }
    }

    LaunchedEffect(Unit) {
        refreshStorage()
        scanning = true
        withContext(Dispatchers.IO) {
            // 大文件：扫描 Download 与 Android 常见目录，>80MB
            bigFiles = scanBigFiles(Environment.getExternalStorageDirectory(), 80L * 1024 * 1024, 20)
            // 旧 APK：Download 下 *.apk
            oldApks = scanApks(File(Environment.getExternalStorageDirectory(), "Download"))
            // 重复文件：Download 下按大小分组哈希
            duplicateFiles = scanDuplicates(File(Environment.getExternalStorageDirectory(), "Download"))
            // QQ 缓存大小
            qqCacheBytes = dirSize(File(Environment.getExternalStorageDirectory(), "Android/data/com.tencent.mobileqq"))
        }
        scanning = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_tools),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "存储管理",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MeiningDaiQing
            )
            Box(
                modifier = Modifier.padding(top = 6.dp).size(width = 44.dp, height = 3.dp)
                    .clip(RoundedCornerShape(2.dp)).background(MeiningGold.copy(alpha = 0.75f))
            )
            Spacer(modifier = Modifier.height(14.dp))

            // 存储总览
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.9f),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "存储空间",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MeiningInk
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val used = (totalBytes - freeBytes).coerceAtLeast(0)
                    val usedRatio = if (totalBytes > 0) used.toFloat() / totalBytes else 0f
                    Box(
                        modifier = Modifier.fillMaxWidth().height(14.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(MeiningGold.copy(alpha = 0.25f))
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth(usedRatio.coerceIn(0f, 1f))
                                .height(14.dp)
                                .clip(RoundedCornerShape(7.dp))
                                .background(MeiningDaiQing)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "已用 ${formatSize(used)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MeiningInk,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "可用 ${formatSize(freeBytes)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MeiningInk.copy(alpha = 0.7f)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "总计 ${formatSize(totalBytes)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MeiningInk.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 一键智能清理
            Surface(
                modifier = Modifier.fillMaxWidth().clickable(enabled = !cleaning) {
                    scope.launch {
                        cleaning = true
                        lastCleanMsg = withContext(Dispatchers.IO) { smartClean(context) }
                        cleaning = false
                        refreshStorage()
                    }
                },
                shape = RoundedCornerShape(16.dp),
                color = MeiningDaiQing,
                shadowElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (cleaning) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp,
                            color = MeiningGold
                        )
                    } else {
                        Text(
                            text = "🧹",
                            fontSize = 22.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (cleaning) "正在清理…" else "一键智能清理",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MeiningMoonWhite
                        )
                        Text(
                            text = "缓存、临时文件、空文件夹、旧安装包（只清安全项）",
                            style = MaterialTheme.typography.labelSmall,
                            color = MeiningMoonWhite.copy(alpha = 0.75f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            if (lastCleanMsg.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = lastCleanMsg,
                    style = MaterialTheme.typography.labelSmall,
                    color = MeiningDaiQing,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 大文件排行榜
            SectionCard(title = "大文件排行榜", subtitle = "全机 >80MB 的文件，点右侧删除") {
                if (scanning) {
                    Text(
                        text = "扫描中…",
                        style = MaterialTheme.typography.bodySmall,
                        color = MeiningInk.copy(alpha = 0.6f)
                    )
                } else if (bigFiles.isEmpty()) {
                    Text(
                        text = "未发现大文件",
                        style = MaterialTheme.typography.bodySmall,
                        color = MeiningInk.copy(alpha = 0.6f)
                    )
                } else {
                    bigFiles.forEach { file ->
                        FileRow(
                            name = file.name,
                            size = formatSize(file.length()),
                            onDelete = {
                                bigFiles = bigFiles.filterNot { it.absolutePath == file.absolutePath }
                                runCatching { file.delete() }
                                refreshStorage()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 重复文件
            SectionCard(title = "重复文件", subtitle = "Download 目录内内容相同的文件") {
                if (scanning) {
                    Text(
                        text = "扫描中…",
                        style = MaterialTheme.typography.bodySmall,
                        color = MeiningInk.copy(alpha = 0.6f)
                    )
                } else if (duplicateFiles.isEmpty()) {
                    Text(
                        text = "未发现重复文件",
                        style = MaterialTheme.typography.bodySmall,
                        color = MeiningInk.copy(alpha = 0.6f)
                    )
                } else {
                    duplicateFiles.forEach { file ->
                        FileRow(
                            name = file.name,
                            size = formatSize(file.length()),
                            onDelete = {
                                duplicateFiles = duplicateFiles.filterNot { it.absolutePath == file.absolutePath }
                                runCatching { file.delete() }
                                refreshStorage()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 旧安装包
            SectionCard(title = "旧安装包", subtitle = "Download 目录下的 APK 文件") {
                if (scanning) {
                    Text(
                        text = "扫描中…",
                        style = MaterialTheme.typography.bodySmall,
                        color = MeiningInk.copy(alpha = 0.6f)
                    )
                } else if (oldApks.isEmpty()) {
                    Text(
                        text = "没有残留安装包",
                        style = MaterialTheme.typography.bodySmall,
                        color = MeiningInk.copy(alpha = 0.6f)
                    )
                } else {
                    oldApks.forEach { file ->
                        FileRow(
                            name = file.name,
                            size = formatSize(file.length()),
                            onDelete = {
                                oldApks = oldApks.filterNot { it.absolutePath == file.absolutePath }
                                runCatching { file.delete() }
                                refreshStorage()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // QQ 缓存
            SectionCard(title = "QQ 缓存", subtitle = "仅统计占用，清理请在 QQ 内进行（避免误删聊天记录）") {
                Text(
                    text = formatSize(qqCacheBytes),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MeiningInk
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SectionCard(title: String, subtitle: String, content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.9f),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MeiningInk
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MeiningInk.copy(alpha = 0.55f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun FileRow(name: String, size: String, onDelete: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodySmall,
                color = MeiningInk,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = size,
                style = MaterialTheme.typography.labelSmall,
                color = MeiningInk.copy(alpha = 0.5f)
            )
        }
        Text(
            text = "删除",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MeiningDaiQing)
                .clickable(onClick = onDelete)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

// ---------- 工具函数 ----------

private fun formatSize(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    var value = bytes.toDouble()
    var idx = 0
    while (value >= 1024 && idx < units.size - 1) {
        value /= 1024
        idx++
    }
    return if (idx == 0) "${value.toLong()} ${units[idx]}" else "%.1f %s".format(value, units[idx])
}

private fun dirSize(dir: File): Long {
    if (!dir.exists() || !dir.isDirectory) return 0L
    return try {
        dir.walkTopDown().filter { it.isFile }.sumOf { it.length() }
    } catch (_: Exception) {
        0L
    }
}

private fun scanBigFiles(root: File, minSize: Long, limit: Int): List<File> {
    val result = mutableListOf<File>()
    if (!root.exists()) return result
    try {
        val queue = ArrayDeque<File>()
        queue.add(root)
        var visited = 0
        while (queue.isNotEmpty() && visited < 200000) {
            val dir = queue.removeFirst()
            visited++
            val children = dir.listFiles() ?: continue
            for (f in children) {
                if (f.isDirectory) {
                    if (f.name !in setOf("cache", "code_cache", "tmp", ".thumbnails", "logs")) {
                        queue.add(f)
                    }
                } else if (f.isFile && f.length() >= minSize) {
                    result.add(f)
                }
            }
        }
    } catch (_: Exception) {
    }
    return result.sortedByDescending { it.length() }.take(limit)
}

private fun scanApks(dir: File): List<File> {
    if (!dir.exists()) return emptyList()
    return try {
        dir.listFiles { f -> f.isFile && f.name.endsWith(".apk", ignoreCase = true) }
            ?.sortedByDescending { it.length() } ?: emptyList()
    } catch (_: Exception) {
        emptyList()
    }
}

private fun scanDuplicates(dir: File): List<File> {
    if (!dir.exists()) return emptyList()
    return try {
        val bySize = mutableMapOf<Long, MutableList<File>>()
        dir.listFiles()?.filter { it.isFile && it.length() > 64 * 1024 }?.forEach { f ->
            bySize.getOrPut(f.length()) { mutableListOf() }.add(f)
        }
        val result = mutableListOf<File>()
        bySize.values.forEach { group ->
            if (group.size > 1) {
                val seen = mutableMapOf<String, File>()
                for (f in group) {
                    val hash = md5(f)
                    if (hash != null) {
                        if (seen.containsKey(hash)) {
                            result.add(f) // 保留第一个，其余列为重复
                        } else {
                            seen[hash] = f
                        }
                    }
                }
            }
        }
        result
    } catch (_: Exception) {
        emptyList()
    }
}

private fun md5(file: File): String? {
    return try {
        val digest = MessageDigest.getInstance("MD5")
        file.inputStream().use { input ->
            val buffer = ByteArray(64 * 1024)
            while (true) {
                val read = input.read(buffer)
                if (read < 0) break
                digest.update(buffer, 0, read)
            }
        }
        digest.digest().joinToString("") { "%02x".format(it) }
    } catch (_: Exception) {
        null
    }
}

/** 一键智能清理：应用缓存、临时文件、空文件夹、下载目录旧 APK */
private fun smartClean(context: android.content.Context): String {
    var freed = 0L
    var count = 0
    try {
        // 1. 本应用缓存
        val cacheDir = context.cacheDir
        cacheDir.listFiles()?.forEach { f ->
            freed += dirSize(f)
            f.deleteRecursively()
            count++
        }
        // 2. 全局临时目录
        val tmpDirs = listOf(
            File(Environment.getExternalStorageDirectory(), ".thumbnails"),
            File(Environment.getExternalStorageDirectory(), "Android/data/com.meining.ai/cache")
        )
        tmpDirs.forEach { d ->
            if (d.exists()) {
                freed += dirSize(d)
                d.deleteRecursively()
                count++
            }
        }
        // 3. Download 目录下旧 APK
        val downloadDir = File(Environment.getExternalStorageDirectory(), "Download")
        downloadDir.listFiles()?.filter { it.isFile && it.name.endsWith(".apk", ignoreCase = true) }
            ?.forEach { f ->
                freed += f.length()
                f.delete()
                count++
            }
        // 4. 空文件夹（Download 一层）
        downloadDir.listFiles()?.filter { it.isDirectory && (it.listFiles()?.isEmpty() ?: false) }
            ?.forEach { d ->
                d.delete()
                count++
            }
    } catch (_: Exception) {
    }
    return if (freed > 0 || count > 0) {
        "已清理 $count 项，释放 ${formatSize(freed)}"
    } else {
        "没有可清理的垃圾"
    }
}