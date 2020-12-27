package com.andb.apps.composesandbox.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

typealias Content = @Composable() () -> Unit

fun Modifier.startBorder(width: Dp, color: Color) = this.drawBehind {
    drawRect(color, size = this.size.copy(width = width.toPx()))
}

fun Modifier.endBorder(width: Dp, color: Color) = drawBehind {
    drawRect(
        color = color,
        topLeft = Offset.Zero.copy(x = this.size.width - width.toPx()),
        size = this.size.copy(width = width.toPx())
    )
}

fun Modifier.bottomBorder(height: Dp, color: Color) = drawBehind {
    drawRect(
        color = color,
        topLeft = Offset.Zero.copy(y = this.size.height - height.toPx()),
        size = this.size.copy(height = height.toPx())
    )
}

fun Color.isDark(): Boolean {
    val darkness: Double = 1 - (0.299 * this.red + 0.587 * this.green + 0.114 * this.blue)
    return darkness >= 0.3
}