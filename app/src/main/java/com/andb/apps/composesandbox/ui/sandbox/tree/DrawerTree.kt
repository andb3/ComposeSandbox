package com.andb.apps.composesandbox.ui.sandbox.tree

import androidx.compose.animation.animate
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawShadow
import androidx.compose.ui.drawLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.gesture.DragObserver
import androidx.compose.ui.gesture.dragGestureFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Position
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.data.model.Component
import com.andb.apps.composesandbox.state.ActionHandlerAmbient
import com.andb.apps.composesandbox.state.UserAction
import com.andb.apps.composesandbox.ui.common.BottomSheetState
import com.andb.apps.composesandbox.ui.common.BottomSheetValue

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DrawerTree(opened: Component, sheetState: BottomSheetState, moving: Component? = null) {
    val actionHandler = ActionHandlerAmbient.current
    val density = DensityAmbient.current

    val dragPosition = remember { mutableStateOf(Position(0.dp, 0.dp)) }
    val onRelease = { actionHandler.invoke(UserAction.UpdateTree(opened)) }
    Column(
        modifier = Modifier
            .dragGestureFilter(
                dragObserver = object : DragObserver {
                    override fun onStart(downPosition: Offset) { dragPosition.value = downPosition.toDpPosition(density) }
                    override fun onDrag(dragDistance: Offset): Offset {
                        dragPosition.value = dragPosition.value + dragDistance.toDpPosition(density)
                        return dragDistance
                    }
                    override fun onStop(velocity: Offset) { onRelease.invoke() }
                    override fun onCancel() { onRelease.invoke() }
                },
                canDrag = { moving != null},
                startDragImmediately = false
            )
    ) {
        DrawerTreeHeader(opened, sheetState)
        Tree(
            parent = opened,
            modifier = Modifier.padding(start = 32.dp, end = 32.dp)
        )
    }
    if (moving != null) {
        ComponentDragDropItem(component = moving, position = dragPosition.value)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DrawerTreeHeader(opened: Component, sheetState: BottomSheetState) {
    val actionHandler = ActionHandlerAmbient.current
    Row(
        verticalGravity = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.padding(32.dp).fillMaxWidth()
    ) {
        Row(verticalGravity = Alignment.CenterVertically) {
            val iconRotation = animate(target = if (sheetState.targetValue != BottomSheetValue.Peek) 180f else 0f)
            Icon(
                asset = Icons.Default.KeyboardArrowUp,
                modifier = Modifier
                    .clickable { if (sheetState.isPeek) sheetState.open() else sheetState.peek() }
                    .drawLayer(rotationZ = iconRotation)
            )
            Text(
                text = opened.name,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        Icon(asset = Icons.Default.Add, modifier = Modifier.clickable { actionHandler.invoke(UserAction.OpenComponentList) })
    }
}

@Composable
private fun ComponentDragDropItem(component: Component, position: Position) {
    ComponentItem(
        component = component,
        modifier = Modifier
            .offset(position.x, position.y)
            .drawShadow(4.dp, RoundedCornerShape(8.dp))
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    )
}

private fun Offset.toDpPosition(density: Density) = with(density) { Position(this@toDpPosition.x.toDp(), this@toDpPosition.y.toDp()) }