package com.andb.apps.composesandbox.state

import com.andb.apps.composesandbox.data.model.Project
import com.andb.apps.composesandbox.data.model.PrototypeComponent
import com.andb.apps.composesandbox.data.model.minusChildFromTree
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalCoroutinesApi::class)
class Machine {
    val screens: MutableStateFlow<List<Screen>> = MutableStateFlow(listOf(Screen.Projects(listOf(Project("Demo Project"), Project("Hello World")))))

    operator fun plusAssign(action: Action) = handleAction(action)

    fun handleAction(action: Action){
        when(action){
            UserAction.Back -> handleBack()
            is UserAction.OpenScreen -> screens.value += action.screen
            is UserAction.AddProject -> addProject(Project(action.name))
            is UserAction.OpenComponent -> screens.updateSandbox { it.copy(drawerStack = it.drawerStack + DrawerState.EditComponent(action.componentID)) }
            is UserAction.OpenComponentList -> screens.updateSandbox { it.copy(drawerStack = it.drawerStack + DrawerState.AddComponent) }
            is UserAction.OpenModifierList -> screens.updateSandbox { it.copy(drawerStack = it.drawerStack + DrawerState.AddModifier) }
            is UserAction.EditModifier -> screens.updateSandbox { it.copy(drawerStack = it.drawerStack + DrawerState.EditModifier(action.modifierID)) }
            is UserAction.MoveComponent -> moveComponent(action.moving)
            is UserAction.UpdateTree -> updateTree(action.updated)
        }
    }

    private fun handleBack() {
        val currentScreen = screens.value.last()
        when {
            currentScreen is Screen.Sandbox && currentScreen.state.drawerStack.last() !is DrawerState.Tree -> {
                screens.updateSandbox { it.copy(drawerStack = it.drawerStack.dropLast(1)) }
            }
            screens.value.size > 1 -> screens.value = screens.value.dropLast(1)
        }
    }

    private fun addProject(project: Project){

    }

    private fun moveComponent(moving: PrototypeComponent) {
        screens.updateSandbox { sandboxState ->
            sandboxState.updatedTree(sandboxState.openedTree.minusChildFromTree(moving)).copy(drawerStack = listOf(DrawerState.Tree(moving)))
        }
    }

    private fun updateTree(updated: PrototypeComponent) {
        screens.updateSandbox { sandboxState ->
            sandboxState.updatedTree(updated)
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
private fun MutableStateFlow<List<Screen>>.updateEach(transform: (Screen) -> Screen) {
    value = value.map(transform)
}

@OptIn(ExperimentalCoroutinesApi::class)
private inline fun <reified T> MutableStateFlow<List<Screen>>.updateScreen(transform: (T) -> Screen) {
    value = value.map {
        when (it) {
            is T -> transform(it)
            else -> it
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
private inline fun MutableStateFlow<List<Screen>>.updateSandbox(transform: (sandboxState: SandboxState) -> SandboxState) {
    updateScreen<Screen.Sandbox> { it.copy(state = transform(it.state)) }
}

private fun SandboxState.updatedTree(updated: PrototypeComponent): SandboxState {
    return this.copy(
        openedTree = updated,
        project = project.copy(
            screens = project.screens.map {
                when (it.id) {
                    updated.id -> updated
                    else -> it
                }
            }
        )
    ).withDrawerTree { DrawerState.Tree() }
}

private fun SandboxState.withDrawerTree(transform: (DrawerState.Tree) -> DrawerState.Tree): SandboxState = this.copy(
    drawerStack = drawerStack.map {
        return@map when (it) {
            is DrawerState.Tree -> transform(it)
            else -> it
        }
    }
)

private fun SandboxState.withEditingComponent(transform: (DrawerState.EditComponent) -> DrawerState.EditComponent): SandboxState = this.copy(
    drawerStack = drawerStack.map {
        return@map when (it) {
            is DrawerState.EditComponent -> transform(it)
            else -> it
        }
    }
)

private fun SandboxState.withEditingModifier(transform: (DrawerState.EditModifier) -> DrawerState.EditModifier): SandboxState = this.copy(
    drawerStack = drawerStack.map {
        return@map when (it) {
            is DrawerState.EditModifier -> transform(it)
            else -> it
        }
    }
)