package com.ai.assistance.operit.ui.main.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter
import java.io.File

/**
 * 梅凝：页面背景组件。
 * 优先显示用户在"背景头像"设置中自定义的背景图片；未设置时显示内置默认资源。
 */
@Composable
fun MeiningPageBackground(pageKey: String, defaultRes: Int, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val custom = MeiningBackgroundStore.getCustomBackground(context, pageKey)
    if (custom != null) {
        Image(
            painter = rememberAsyncImagePainter(model = File(custom)),
            contentDescription = null,
            modifier = modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    } else {
        Image(
            painter = painterResource(defaultRes),
            contentDescription = null,
            modifier = modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}