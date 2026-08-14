package com.ai.assistance.operit.ui.main.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ai.assistance.operit.R
import com.ai.assistance.operit.ui.theme.MeiningDaiQing
import com.ai.assistance.operit.ui.theme.MeiningGold
import com.ai.assistance.operit.ui.theme.MeiningInk
import com.ai.assistance.operit.ui.theme.MeiningMoonWhite
import androidx.compose.foundation.BorderStroke
import com.ai.assistance.operit.ui.main.components.MeiningBackgroundStore
import com.ai.assistance.operit.ui.main.components.MeiningPageBackground
import com.ai.assistance.operit.data.preferences.DisplayPreferencesManager
import androidx.compose.runtime.collectAsState
import coil.compose.rememberAsyncImagePainter
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import com.ai.assistance.operit.ui.main.components.RecentUsageStore

// 梅凝工具页入口：图标使用素材包中的古风素材
private data class MeiningToolEntry(val title: String, val iconRes: Int, val screen: Screen)

@Composable
fun ToolsHomeContent(navigateTo: (Screen) -> Unit) {
    val context = LocalContext.current
    val entries = listOf(
        MeiningToolEntry(stringResource(R.string.tool_terminal), R.drawable.ic_tool_terminal, Screen.Terminal),
        MeiningToolEntry(stringResource(R.string.nav_memory_base), R.drawable.ic_tool_memory, Screen.MemoryBase),
        MeiningToolEntry(stringResource(R.string.nav_toolbox), R.drawable.ic_tool_toolbox, Screen.Toolbox),
        MeiningToolEntry(stringResource(R.string.nav_packages), R.drawable.ic_tool_packages, Screen.Packages),
        MeiningToolEntry(stringResource(R.string.nav_workflow), R.drawable.ic_tool_workflow, Screen.Workflow),
        MeiningToolEntry(stringResource(R.string.nav_assistant_config), R.drawable.ic_tool_assistant, Screen.AssistantConfig),
        // 梅凝：存储管理（第4行，用户指定图标 ic_storage）
        MeiningToolEntry("存储管理", R.drawable.ic_storage, Screen.StorageManage),
    )
    Box(modifier = Modifier.fillMaxSize()) {
        // 梅凝：工具页使用 B006 淡墨山水背景（可自定义）
        MeiningPageBackground(
            pageKey = MeiningBackgroundStore.PAGE_TOOLS,
            defaultRes = R.drawable.bg_tools
        )
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.nav_tools),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MeiningDaiQing
            )
            // 鎏金装饰线
            Box(
                modifier =
                        Modifier.padding(top = 6.dp).size(width = 44.dp, height = 3.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(MeiningGold.copy(alpha = 0.75f))
            )
            Spacer(modifier = Modifier.height(14.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(entries) { entry ->
                    MeiningToolCard(
                        entry = entry,
                        onClick = {
                            RecentUsageStore.recordTool(context, entry.screen)
                            navigateTo(entry.screen)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MeiningToolCard(entry: MeiningToolEntry, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = Color.White.copy(alpha = 0.9f),
        border = BorderStroke(1.2.dp, MeiningGold.copy(alpha = 0.5f)),
        shadowElevation = 3.dp
    ) {
        Column(
            modifier = Modifier.padding(vertical = 18.dp, horizontal = 12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 梅凝：只留图标实体（无底托/内嵌/装饰框），图标大尺寸
            Image(
                painter = painterResource(entry.iconRes),
                contentDescription = entry.title,
                modifier = Modifier
                    .size(72.dp)
                    .shadow(6.dp, RoundedCornerShape(14.dp), clip = false),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = entry.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MeiningInk,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 16.sp
            )
        }
    }
}

// 梅凝我的页入口：图标使用素材包中的古风素材
private data class MeiningProfileEntry(val title: String, val iconRes: Int, val screen: Screen)

@Composable
fun ProfileHomeContent(navigateTo: (Screen) -> Unit) {
    val context = LocalContext.current
    // 梅凝：用户自定义头像（图库选择，全局生效）
    val displayPrefs = remember { DisplayPreferencesManager.getInstance(context) }
    val globalUserAvatar by displayPrefs.globalUserAvatarUri.collectAsState(initial = null)
    val items = listOf(
        MeiningProfileEntry(stringResource(R.string.meining_model_config), R.drawable.ic_profile_model, Screen.ModelConfig),
        MeiningProfileEntry(stringResource(R.string.meining_theme_settings), R.drawable.ic_profile_theme, Screen.ThemeSettings),
        // 梅凝：原"包管理"已删除，替换为"背景头像"设置入口
        MeiningProfileEntry("背景头像", R.drawable.ic_profile_packages, Screen.BackgroundAvatar),
        MeiningProfileEntry(stringResource(R.string.meining_memory_data), R.drawable.ic_profile_memory, Screen.ChatHistorySettings),
        MeiningProfileEntry(stringResource(R.string.meining_privacy), R.drawable.ic_profile_privacy, Screen.Settings),
        MeiningProfileEntry(stringResource(R.string.nav_about), R.drawable.ic_profile_about, Screen.About),
    )
    Box(modifier = Modifier.fillMaxSize()) {
        // 梅凝：我的页使用 B014 群峰云海背景（可自定义）
        MeiningPageBackground(
            pageKey = MeiningBackgroundStore.PAGE_PROFILE,
            defaultRes = R.drawable.bg_profile
        )
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
        ) {
        // 顶部：梅凝古风头像 + 名字 + 副标题
        Column(
            modifier = Modifier.fillMaxWidth().background(MeiningDaiQing.copy(alpha = 0.92f)).padding(top = 28.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(104.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.size(96.dp),
                    shape = CircleShape,
                    color = MeiningMoonWhite
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(2.dp), contentAlignment = Alignment.Center) {
                        // 梅凝：古风女头像 T013；用户自定义头像优先
                        if (globalUserAvatar != null) {
                            Image(
                                painter = rememberAsyncImagePainter(model = Uri.parse(globalUserAvatar)),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(R.drawable.ic_avatar_meining_main),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MeiningMoonWhite,
                fontSize = 26.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.meining_profile_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MeiningMoonWhite.copy(alpha = 0.85f)
            )
        }
        Column(modifier = Modifier.padding(16.dp)) {
            items.forEach { entry ->
                MeiningProfileRow(entry = entry, onClick = { navigateTo(entry.screen) })
                Spacer(modifier = Modifier.height(10.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.about_copyright),
                style = MaterialTheme.typography.bodySmall,
                color = MeiningInk.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp)
            )
        }
    }
    }
}

@Composable
private fun MeiningProfileRow(entry: MeiningProfileEntry, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.92f),
        border = BorderStroke(1.2.dp, MeiningGold.copy(alpha = 0.5f)),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 梅凝：只留图标实体（无底托/内嵌/装饰框）
            Image(
                painter = painterResource(entry.iconRes),
                contentDescription = entry.title,
                modifier = Modifier
                    .size(52.dp)
                    .shadow(4.dp, RoundedCornerShape(12.dp), clip = false)
            )
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = entry.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MeiningInk,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = MeiningGold,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}