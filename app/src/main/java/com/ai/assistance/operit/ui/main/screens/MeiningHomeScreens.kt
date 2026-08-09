package com.ai.assistance.operit.ui.main.screens

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.ai.assistance.operit.ui.theme.MeiningMountainBackground
import androidx.compose.foundation.BorderStroke

private data class MeiningToolEntry(val title: String, val icon: ImageVector, val screen: Screen)

@Composable
fun ToolsHomeContent(navigateTo: (Screen) -> Unit) {
    val entries = listOf(
        MeiningToolEntry(stringResource(R.string.tool_terminal), Icons.Default.Terminal, Screen.Terminal),
        MeiningToolEntry(stringResource(R.string.nav_memory_base), Icons.Default.History, Screen.MemoryBase),
        MeiningToolEntry(stringResource(R.string.nav_toolbox), Icons.Default.Apps, Screen.Toolbox),
        MeiningToolEntry(stringResource(R.string.nav_packages), Icons.Default.Extension, Screen.Packages),
        MeiningToolEntry(stringResource(R.string.nav_workflow), Icons.Default.AccountTree, Screen.Workflow),
        MeiningToolEntry(stringResource(R.string.nav_assistant_config), Icons.Default.Tune, Screen.AssistantConfig),
    )
    Box(modifier = Modifier.fillMaxSize()) {
        MeiningMountainBackground()
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
                    MeiningToolCard(entry = entry, onClick = { navigateTo(entry.screen) })
                }
            }
        }
    }
}

@Composable
private fun MeiningToolCard(entry: MeiningToolEntry, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.88f),
        border = BorderStroke(1.dp, MeiningGold.copy(alpha = 0.45f)),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(vertical = 20.dp, horizontal = 12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(52.dp).clip(RoundedCornerShape(14.dp)).background(MeiningDaiQing.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(entry.icon, contentDescription = entry.title, tint = MeiningDaiQing, modifier = Modifier.size(30.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = entry.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MeiningInk,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private data class MeiningProfileEntry(val title: String, val icon: ImageVector, val screen: Screen)

@Composable
fun ProfileHomeContent(navigateTo: (Screen) -> Unit) {
    val items = listOf(
        MeiningProfileEntry(stringResource(R.string.meining_model_config), Icons.Default.SmartToy, Screen.ModelConfig),
        MeiningProfileEntry(stringResource(R.string.meining_theme_settings), Icons.Default.Palette, Screen.ThemeSettings),
        MeiningProfileEntry(stringResource(R.string.nav_packages), Icons.Default.Extension, Screen.Packages),
        MeiningProfileEntry(stringResource(R.string.meining_memory_data), Icons.Default.History, Screen.ChatHistorySettings),
        MeiningProfileEntry(stringResource(R.string.meining_privacy), Icons.Default.Settings, Screen.Settings),
        MeiningProfileEntry(stringResource(R.string.nav_about), Icons.Default.Info, Screen.About),
    )
    Box(modifier = Modifier.fillMaxSize()) {
        MeiningMountainBackground()
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
        ) {
        // 顶部：头像 + 梅凝 + 副标题
        Column(
            modifier = Modifier.fillMaxWidth().background(MeiningDaiQing).padding(top = 28.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(88.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    color = MeiningMoonWhite
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(6.dp), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.SmartToy, contentDescription = null, tint = MeiningDaiQing, modifier = Modifier.size(52.dp))
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
        shape = RoundedCornerShape(14.dp),
        color = Color.White.copy(alpha = 0.88f),
        border = BorderStroke(1.dp, MeiningGold.copy(alpha = 0.45f)),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(MeiningGold.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(entry.icon, contentDescription = entry.title, tint = MeiningDaiQing, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.size(14.dp))
            Text(
                text = entry.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MeiningInk,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = MeiningGold, modifier = Modifier.size(16.dp))
        }
    }
}
