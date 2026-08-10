package com.ai.assistance.operit.ui.main.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ai.assistance.operit.R
import com.ai.assistance.operit.ui.common.NavItem
import com.ai.assistance.operit.ui.main.NavigationTransitionSource
import com.ai.assistance.operit.ui.main.TopBarTitleContent
import com.ai.assistance.operit.ui.main.navigation.NavigationEntrySpec
import com.ai.assistance.operit.ui.main.navigation.RouteEntry
import com.ai.assistance.operit.ui.main.components.AppContent
import com.ai.assistance.operit.ui.main.screens.Screen
import com.ai.assistance.operit.ui.theme.MeiningDaiQing
import com.ai.assistance.operit.ui.theme.MeiningGold
import com.ai.assistance.operit.ui.theme.MeiningInk
import com.ai.assistance.operit.ui.theme.MeiningMoonWhite
import kotlinx.coroutines.CoroutineScope

/**
 * 梅凝手机布局：底部三标签（聊天 / 工具 / 我的）。
 * 顶层页面显示底部导航；子页面隐藏底部导航，通过顶部返回键返回。
 */
@Composable
fun PhoneLayout(
        currentRouteEntry: RouteEntry,
        currentScreen: Screen,
        selectedItem: NavItem?,
        isLoading: Boolean,
        navItems: List<NavItem>,
        pluginSidebarEntries: List<NavigationEntrySpec>,
        selectedRouteId: String,
        isNetworkAvailable: Boolean,
        networkType: String,
        drawerWidth: Dp,
        navController: androidx.navigation.NavController,
        scope: CoroutineScope,
        drawerState: androidx.compose.material3.DrawerState,
        showFpsCounter: Boolean,
        enableNavigationAnimation: Boolean,
        navigationTransitionSource: NavigationTransitionSource,
        onScreenChange: (Screen) -> Unit,
        onDrawerItemSelected: (Screen) -> Unit,
        onNavigationEntrySelected: (NavigationEntrySpec) -> Unit,
        navigateToTokenConfig: () -> Unit,
        canGoBack: Boolean,
        onGoBack: () -> Unit,
        isNavigatingBack: Boolean = false,
        topBarActions: @Composable RowScope.() -> Unit = {},
        topBarTitleContent: TopBarTitleContent? = null
) {
    val isTopLevel =
            currentScreen is Screen.AiChat ||
                    currentScreen is Screen.Tools ||
                    currentScreen is Screen.Profile
    val selectedTabIndex =
            when (currentScreen) {
                is Screen.AiChat -> 0
                is Screen.Tools -> 1
                is Screen.Profile -> 2
                else -> -1
            }
    Column(modifier = Modifier.fillMaxSize().background(MeiningMoonWhite)) {
        Box(
                modifier =
                        Modifier.weight(1f).fillMaxWidth()
        ) {
            AppContent(
                currentRouteEntry = currentRouteEntry,
                currentScreen = currentScreen,
                selectedItem = selectedItem,
                useTabletLayout = false,
                isTabletSidebarExpanded = false,
                isLoading = isLoading,
                navController = navController,
                scope = scope,
                drawerState = drawerState,
                showFpsCounter = showFpsCounter,
                enableNavigationAnimation = enableNavigationAnimation,
                navigationTransitionSource = navigationTransitionSource,
                onScreenChange = onScreenChange,
                onToggleSidebar = { /* 梅凝无侧边抽屉 */ },
                navigateToTokenConfig = navigateToTokenConfig,
                canGoBack = canGoBack,
                onGoBack = onGoBack,
                isNavigatingBack = isNavigatingBack,
                actions = topBarActions,
                titleContent = topBarTitleContent
            )
        }
        if (isTopLevel) {
            MeiningBottomBar(
                    selectedIndex = selectedTabIndex,
                    onSelect = { index ->
                        val target =
                                when (index) {
                                    0 -> Screen.AiChat
                                    1 -> Screen.Tools
                                    else -> Screen.Profile
                                }
                        onScreenChange(target)
                    }
            )
        }
    }
}

private data class MeiningTabItem(val navItem: NavItem)

@Composable
private fun MeiningBottomBar(selectedIndex: Int, onSelect: (Int) -> Unit) {
    val tabs =
            listOf(
                    MeiningTabItem(NavItem.AiChat),
                    MeiningTabItem(NavItem.Tools),
                    MeiningTabItem(NavItem.Profile),
            )
    // 概念图：深青蓝导航面板 + 金色分隔线 + 古风素材图标
    Surface(color = MeiningDaiQing, shadowElevation = 8.dp) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(MeiningGold.copy(alpha = 0.65f)))
            Row(
                    modifier =
                            Modifier.fillMaxWidth().navigationBarsPadding().height(58.dp),
                    verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEachIndexed { index, tab ->
                    val selected = index == selectedIndex
                    val title = stringResource(id = tab.navItem.titleResId)
                    val iconRes =
                            when (tab.navItem) {
                                NavItem.AiChat -> R.drawable.ic_nav_chat // T049 金色聊天气泡
                                NavItem.Tools -> R.drawable.ic_nav_tools // T041 金铜宝箱
                                else -> R.drawable.ic_nav_profile // T018 修仙男头像
                            }
                    Column(
                            modifier =
                                    Modifier.weight(1f).fillMaxHeight()
                                            .clickable { onSelect(index) },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                                painter = painterResource(iconRes),
                                contentDescription = title,
                                modifier =
                                        Modifier.size(if (selected) 26.dp else 22.dp)
                                                .alpha(if (selected) 1f else 0.6f)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                                text = title,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                color = if (selected) MeiningGold else Color.White.copy(alpha = 0.72f),
                                fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}