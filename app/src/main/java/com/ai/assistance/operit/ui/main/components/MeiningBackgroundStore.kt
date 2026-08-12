package com.ai.assistance.operit.ui.main.components

import android.content.Context
import com.ai.assistance.operit.R
import java.io.File

/**
 * 梅凝：页面背景偏好存储。
 * 支持为每个页面设置内置背景或自定义背景图片（用户从图库选择后复制到应用私有目录）。
 */
object MeiningBackgroundStore {
    private const val PREFS = "meining_background_prefs"
    private const val KEY_PREFIX = "bg_"

    /** 页面标识：与页面背景默认资源对应 */
    const val PAGE_CHAT = "chat"          // 聊天主页 B013
    const val PAGE_TOOLS = "tools"        // 工具页 B006
    const val PAGE_PROFILE = "profile"    // 我的页 B014
    const val PAGE_WELCOME = "welcome"    // 首启欢迎页 B016
    const val PAGE_MARKET = "market"      // 插件市场 B012
    const val PAGE_MEMORY = "memory"      // 记忆库 B002
    const val PAGE_TERMINAL = "terminal"  // 终端 B018
    const val PAGE_ASSISTANT = "assistant"// 助手配置 B006
    const val PAGE_WORKFLOW = "workflow"  // 工作流 B005
    const val PAGE_TOOLBOX = "toolbox"    // 工具箱 B017
    const val PAGE_AGREEMENT = "agreement"// 协议弹窗 B009

    /** 内置背景列表：页面 key → (显示名, 默认资源) */
    data class BuiltinBg(val pageKey: String, val label: String, val defaultRes: Int)

    fun builtinBackgrounds(): List<BuiltinBg> =
        listOf(
            BuiltinBg(PAGE_CHAT, "聊天主页", R.drawable.bg_chat_home),
            BuiltinBg(PAGE_TOOLS, "工具页", R.drawable.bg_tools),
            BuiltinBg(PAGE_PROFILE, "我的页", R.drawable.bg_profile),
            BuiltinBg(PAGE_WELCOME, "启动页", R.drawable.bg_welcome),
            BuiltinBg(PAGE_MARKET, "插件市场", R.drawable.bg_market),
            BuiltinBg(PAGE_MEMORY, "记忆库", R.drawable.bg_memory),
            BuiltinBg(PAGE_TERMINAL, "终端", R.drawable.bg_terminal),
            BuiltinBg(PAGE_ASSISTANT, "助手配置", R.drawable.bg_assistant),
            BuiltinBg(PAGE_WORKFLOW, "工作流", R.drawable.bg_workflow),
            BuiltinBg(PAGE_TOOLBOX, "工具箱", R.drawable.bg_toolbox),
            BuiltinBg(PAGE_AGREEMENT, "协议弹窗", R.drawable.bg_agreement),
        )

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    /** 设置自定义背景（复制图片到私有目录） */
    fun setCustomBackground(context: Context, pageKey: String, sourceFile: File): Boolean {
        return try {
            val dir = File(context.filesDir, "meining_bg").apply { mkdirs() }
            val dest = File(dir, "${pageKey}_custom.jpg")
            sourceFile.copyTo(dest, overwrite = true)
            prefs(context).edit().putString("${KEY_PREFIX}$pageKey", dest.absolutePath).apply()
            true
        } catch (_: Exception) {
            false
        }
    }

    /** 恢复内置背景 */
    fun resetBackground(context: Context, pageKey: String) {
        prefs(context).edit().remove("${KEY_PREFIX}$pageKey").apply()
    }

    /** 读取页面背景：返回 null 表示用内置默认资源 */
    fun getCustomBackground(context: Context, pageKey: String): String? =
        prefs(context).getString("${KEY_PREFIX}$pageKey", null)?.takeIf { File(it).exists() }

    /** 判断是否为内置默认 */
    fun isCustom(context: Context, pageKey: String): Boolean =
        getCustomBackground(context, pageKey) != null
}
