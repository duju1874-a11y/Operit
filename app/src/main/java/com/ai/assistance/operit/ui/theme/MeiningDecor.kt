package com.ai.assistance.operit.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

/**
 * 梅凝水墨背景：月白底 + 淡墨远山 + 鎏金圆点缀 + 金色地平线。
 * 供工具页 / 我的页等页面作为底层背景使用。
 */
@Composable
fun MeiningMountainBackground(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().background(MeiningMoonWhite)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            // 远山（最淡、最高）
            val far = Path().apply {
                moveTo(0f, h * 0.55f)
                lineTo(w * 0.18f, h * 0.42f)
                lineTo(w * 0.36f, h * 0.52f)
                lineTo(w * 0.58f, h * 0.40f)
                lineTo(w * 0.78f, h * 0.50f)
                lineTo(w, h * 0.42f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(far, Color(0x142E5E63))
            // 中山
            val mid = Path().apply {
                moveTo(0f, h * 0.68f)
                lineTo(w * 0.25f, h * 0.55f)
                lineTo(w * 0.5f, h * 0.66f)
                lineTo(w * 0.72f, h * 0.53f)
                lineTo(w, h * 0.63f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(mid, Color(0x1A2E5E63))
            // 近山
            val near = Path().apply {
                moveTo(0f, h * 0.80f)
                lineTo(w * 0.3f, h * 0.70f)
                lineTo(w * 0.6f, h * 0.78f)
                lineTo(w * 0.85f, h * 0.68f)
                lineTo(w, h * 0.74f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(near, Color(0x0F1D3A3F))
            // 鎏金圆（淡月）点缀右上
            drawCircle(
                color = MeiningGold.copy(alpha = 0.28f),
                radius = w * 0.09f,
                center = Offset(w * 0.82f, h * 0.14f)
            )
            // 金色地平线
            drawLine(
                color = MeiningGold.copy(alpha = 0.30f),
                start = Offset(0f, h * 0.83f),
                end = Offset(w, h * 0.83f),
                strokeWidth = 1.5f
            )
        }
    }
}