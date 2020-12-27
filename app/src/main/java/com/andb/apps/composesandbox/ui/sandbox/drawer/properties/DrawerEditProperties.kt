package com.andb.apps.composesandbox.ui.sandbox.drawer.properties

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ControlPointDuplicate
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.model.PrototypeComponent
import com.andb.apps.composesandbox.state.ActionHandler
import com.andb.apps.composesandbox.state.ActionHandlerAmbient
import com.andb.apps.composesandbox.state.DrawerScreen
import com.andb.apps.composesandbox.state.UserAction
import com.andb.apps.composesandbox.ui.sandbox.drawer.DrawerHeader
import com.andb.apps.composesandbox.ui.sandbox.drawer.toShadow

@Composable
fun DrawerEditProperties(component: PrototypeComponent, actionHandler: ActionHandler, onUpdate: (PrototypeComponent) -> Unit) {
    val scrollState = rememberScrollState()
    Column {
        DrawerEditPropertiesHeader(
            component,
            modifier = Modifier
                .shadow(scrollState.toShadow())
                .background(AmbientElevationOverlay.current?.apply(color = MaterialTheme.colors.surface, elevation = AmbientAbsoluteElevation.current + scrollState.toShadow()) ?: MaterialTheme.colors.surface)
        )
        ScrollableColumn(scrollState = scrollState, modifier = Modifier.padding(horizontal = 32.dp)) {
            when (component) {
                is PrototypeComponent.Text -> TextProperties(component.properties) {
                    onUpdate(component.copy(properties = it))
                }
                is PrototypeComponent.Icon -> IconProperties(component.properties) {
                    onUpdate(component.copy(properties = it))
                }
                is PrototypeComponent.Group.Column -> ColumnProperties(component.properties) {
                    onUpdate(component.copy(properties = it))
                }
                is PrototypeComponent.Group.Row -> RowProperties(component.properties) {
                    onUpdate(component.copy(properties = it))
                }
                is PrototypeComponent.Group.Box -> {}
                is PrototypeComponent.Slotted.TopAppBar -> TopAppBarProperties(properties = component.properties) {
                    onUpdate(component.copy(properties = it))
                }
                is PrototypeComponent.Slotted.BottomAppBar -> BottomAppBarProperties(properties = component.properties) {
                    onUpdate(component.copy(properties = it))
                }
                is PrototypeComponent.Slotted.ExtendedFloatingActionButton -> ExtendedFloatingActionButtonProperties(properties = component.properties) {
                    onUpdate(component.copy(properties = it))
                }
                is PrototypeComponent.Slotted.Scaffold -> ScaffoldProperties(properties = component.properties) {
                    onUpdate(component.copy(properties = it))
                }
            }
            ModifiersEditor(
                modifiers = component.modifiers,
                modifier = Modifier.padding(vertical = 32.dp),
                onAdd = { actionHandler.invoke(UserAction.OpenDrawerScreen(DrawerScreen.AddModifier)) },
                onOpenModifier = { actionHandler.invoke(UserAction.OpenDrawerScreen(DrawerScreen.EditModifier(it.id))) },
                onUpdate = {
                    onUpdate(component.copy(modifiers = it))
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DrawerEditPropertiesHeader(component: PrototypeComponent, modifier: Modifier = Modifier){
    val actionHandler = ActionHandlerAmbient.current
    DrawerHeader(title = component.name, screenName = "Edit Component".toUpperCase(), modifier = modifier, onIconClick = { actionHandler.invoke(UserAction.Back) }) {
        Icon(imageVector = Icons.Default.ControlPointDuplicate)
    }
}

