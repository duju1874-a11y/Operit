package com.ai.assistance.operit.ui.main.components

import android.content.Context
import com.ai.assistance.operit.R
import com.ai.assistance.operit.ui.main.screens.Screen

/**
 * 梅凝：最近使用记录存储（SharedPreferences 实现）。
 * 记录用户最近打开过的工具页面，供铜铃快捷面板展示。
 */
object RecentUsageStore {
    private const val PREFS = "meining_recent_usage"
    private const val KEY_TOOLS = "recent_tools"
    private const val MAX_TOOLS = 8

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    /** 记录一次工具打开，最近使用排最前，去重 */
    fun recordTool(context: Context, screen: Screen) {
        val key = screenKey(screen) ?: return
        val current = prefs(context).getString(KEY_TOOLS, "") ?: ""
        val list = current.split("|").filter { it.isNotBlank() && it != key }.toMutableList()
        list.add(0, key)
        while (list.size > MAX_TOOLS) list.removeAt(list.size - 1)
        prefs(context).edit().putString(KEY_TOOLS, list.joinToString("|")).apply()
    }

    /** 读取最近工具（新→旧），并解析为可展示的条目 */
    fun recentTools(context: Context): List<RecentToolEntry> {
        val current = prefs(context).getString(KEY_TOOLS, "") ?: ""
        return current.split("|").filter { it.isNotBlank() }.mapNotNull { key ->
            toolEntryOf(key)?.let { it to key }
        }.map { it.first }
    }

    private fun screenKey(screen: Screen): String? =
        when (screen) {
            is Screen.Terminal -> "terminal"
            is Screen.MemoryBase -> "memory"
            is Screen.Toolbox -> "toolbox"
            is Screen.Packages -> "packages"
            is Screen.Workflow -> "workflow"
            is Screen.AssistantConfig -> "assistant"
            is Screen.StorageManage -> "storage"
            else -> null
        }

    private fun toolEntryOf(key: String): RecentToolEntry? =
        when (key) {
            "terminal" -> RecentToolEntry("命令终端", R.drawable.ic_tool_terminal) { it is Screen.Terminal }
            "memory" -> RecentToolEntry("记忆库", R.drawable.ic_tool_memory) { it is Screen.MemoryBase }
            "toolbox" -> RecentToolEntry("工具箱", R.drawable.ic_tool_toolbox) { it is Screen.Toolbox }
            "packages" -> RecentToolEntry("插件市场", R.drawable.ic_tool_packages) { it is Screen.Packages }
            "workflow" -> RecentToolEntry("工作流", R.drawable.ic_tool_workflow) { it is Screen.Workflow }
            "assistant" -> RecentToolEntry("助手配置", R.drawable.ic_tool_assistant) { it is Screen.AssistantConfig }
            "storage" -> RecentToolEntry("存储管理", R.drawable.ic_storage) { it is Screen.StorageManage }
            else -> null
        }
}

/** 最近工具条目 */
data class RecentToolEntry(
    val title: String,
    val iconRes: Int,
    val matches: (Screen) -> Boolean
)
