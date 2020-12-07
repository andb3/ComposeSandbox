package com.andb.apps.composesandbox.ui.sandbox.drawer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.model.PrototypeColor
import com.andb.apps.composesandbox.model.PrototypeModifier
import com.andb.apps.composesandbox.state.ActionHandlerAmbient
import com.andb.apps.composesandbox.state.UserAction
import com.andb.apps.composesandbox.ui.sandbox.drawer.properties.ModifierItem

@Composable
fun AddModifierList(onSelect: (PrototypeModifier) -> Unit) {
    val actionHandler = ActionHandlerAmbient.current
    Column {
        DrawerHeader("Add Modifier", onIconClick = { actionHandler.invoke(UserAction.Back) })
        val searchTerm = savedInstanceState { "" }

        AddComponentHeader(text = "Common Components")
        AddModifierItem(PrototypeModifier.Padding.All(16), onSelect)
        AddModifierItem(PrototypeModifier.Border(1, PrototypeColor.ThemeColor.OnBackground, 0), onSelect)
        AddModifierItem(PrototypeModifier.Background(PrototypeColor.ThemeColor.Primary, 0), onSelect)
        AddModifierItem(PrototypeModifier.Width(32), onSelect)
        AddModifierItem(PrototypeModifier.Height(32), onSelect)
        AddModifierItem(PrototypeModifier.FillMaxWidth(), onSelect)
        AddModifierItem(PrototypeModifier.FillMaxHeight(), onSelect)
    }
}

@Composable
private fun AddModifierItem(prototypeModifier: PrototypeModifier, onSelect: (PrototypeModifier) -> Unit) {
    ModifierItem(
        prototypeModifier = prototypeModifier,
        modifier = Modifier
            .clickable { onSelect.invoke(prototypeModifier) }
            .padding(horizontal = 32.dp, vertical = 8.dp)
            .fillMaxWidth()
    )
}

