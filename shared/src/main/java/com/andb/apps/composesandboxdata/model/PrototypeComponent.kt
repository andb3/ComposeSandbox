package com.andb.apps.composesandboxdata.model

import com.andb.apps.composesandboxdata.plusElement
import kotlinx.serialization.Serializable
import java.util.*

val allComponents = listOf(
    PrototypeComponent.Text(),
    PrototypeComponent.Icon(),
    PrototypeComponent.Group.Row(),
    PrototypeComponent.Group.Column(),
    PrototypeComponent.Group.Box(),
    PrototypeComponent.Slotted.TopAppBar(),
    PrototypeComponent.Slotted.BottomAppBar(),
    PrototypeComponent.Slotted.ExtendedFloatingActionButton(),
    PrototypeComponent.Slotted.Scaffold(),
)

@Serializable
sealed class PrototypeComponent {
    abstract val id: String
    abstract val modifiers: List<PrototypeModifier>
    abstract val properties: Properties

    @Serializable
    data class Text(
        override val properties: Properties.Text = Properties.Text("Text"),
        override val id: String = UUID.randomUUID().toString(), override val modifiers: List<PrototypeModifier> = emptyList(),
    ) : PrototypeComponent()

    @Serializable
    data class Icon(
        override val properties: Properties.Icon = Properties.Icon(PrototypeIcon.Image),
        override val id: String = UUID.randomUUID().toString(), override val modifiers: List<PrototypeModifier> = emptyList(),
    ) : PrototypeComponent()

    @Serializable
    sealed class Group : PrototypeComponent() {

        abstract val children: List<PrototypeComponent>
        abstract override val properties: Properties.Group

        @Serializable
        data class Row(
            override val properties: Properties.Group.Row = Properties.Group.Row(),
            override val children: List<PrototypeComponent> = emptyList(),
            override val id: String = UUID.randomUUID().toString(), override val modifiers: List<PrototypeModifier> = emptyList(),
        ) : Group()

        @Serializable
        data class Column(
            override val properties: Properties.Group.Column = Properties.Group.Column(),
            override val children: List<PrototypeComponent> = emptyList(),
            override val id: String = UUID.randomUUID().toString(), override val modifiers: List<PrototypeModifier> = emptyList(),
        ) : Group()
        @Serializable
        data class Box(
            override val properties: Properties.Group.Box = Properties.Group.Box,
            override val children: List<PrototypeComponent> = emptyList(),
            override val id: String = UUID.randomUUID().toString(), override val modifiers: List<PrototypeModifier> = emptyList(),
        ) : Group()
    }

    @Serializable
    sealed class Slotted : PrototypeComponent() {

        abstract val slots: List<Slot>
        abstract override val properties: Properties.Slotted

        val Slot.enabled get() = !this.optional || properties.slotsEnabled[this.name] == true

        @Serializable
        data class TopAppBar(
            override val properties: Properties.Slotted.TopAppBar = Properties.Slotted.TopAppBar(),
            override val slots: List<Slot> = listOf(Slot("Navigation Icon"), Slot("Title", optional = false), Slot("Actions", PrototypeComponent.Group.Row())),
            override val id: String = UUID.randomUUID().toString(), override val modifiers: List<PrototypeModifier> = emptyList(),
        ) : Slotted()

        @Serializable
        data class BottomAppBar(
            override val properties: Properties.Slotted.BottomAppBar = Properties.Slotted.BottomAppBar(),
            override val slots: List<Slot> = listOf(Slot("Content", PrototypeComponent.Group.Row(), optional = false)),
            override val id: String = UUID.randomUUID().toString(), override val modifiers: List<PrototypeModifier> = emptyList(),
        ) : Slotted()

        @Serializable
        data class ExtendedFloatingActionButton(
            override val properties: Properties.Slotted.ExtendedFloatingActionButton = Properties.Slotted.ExtendedFloatingActionButton(),
            override val slots: List<Slot> = listOf(Slot("Icon"), Slot("Text", optional = false)),
            override val id: String = UUID.randomUUID().toString(), override val modifiers: List<PrototypeModifier> = emptyList(),
        ) : Slotted()

        @Serializable
        data class Scaffold(
            override val properties: Properties.Slotted.Scaffold = Properties.Slotted.Scaffold(),
            override val slots: List<Slot> = listOf(Slot("Top App Bar"), Slot("Bottom App Bar"), Slot("Floating Action Button"), Slot("Drawer"), Slot("Body Content", optional = false)),
            override val id: String = UUID.randomUUID().toString(), override val modifiers: List<PrototypeModifier> = emptyList(),
        ) : Slotted()
    }

    @Serializable
    data class Custom(val treeID: String, override val properties: Properties = Properties.Blank, override val id: String = UUID.randomUUID().toString(), override val modifiers: List<PrototypeModifier> = emptyList()) : PrototypeComponent()

    fun copy(
        id: String = this.id,
        modifiers: List<PrototypeModifier> = this.modifiers,
        properties: Properties = this.properties
    ): PrototypeComponent = when (this) {
        is Text -> this.copy(id = id, modifiers = modifiers, properties = properties as Properties.Text)
        is Icon -> this.copy(id = id, modifiers = modifiers, properties = properties as Properties.Icon)
        is Group.Row -> this.copy(id = id, modifiers = modifiers, properties = properties as Properties.Group.Row)
        is Group.Column -> this.copy(id = id, modifiers = modifiers, properties = properties as Properties.Group.Column)
        is Group.Box -> this.copy(id = id, modifiers = modifiers, properties = properties as Properties.Group.Box)
        is Slotted.TopAppBar -> this.copy(id = id, modifiers = modifiers, properties = properties as Properties.Slotted.TopAppBar)
        is Slotted.BottomAppBar -> this.copy(id = id, modifiers = modifiers, properties = properties as Properties.Slotted.BottomAppBar)
        is Slotted.ExtendedFloatingActionButton -> this.copy(id = id, modifiers = modifiers, properties = properties as Properties.Slotted.ExtendedFloatingActionButton)
        is Slotted.Scaffold -> this.copy(id = id, modifiers = modifiers, properties = properties as Properties.Slotted.Scaffold)
        is Custom -> this.copy(treeID = this.treeID, id = id, modifiers = modifiers, properties = properties as Properties.Blank)
    }

    fun name(project: Project) = when (this) {
        is Text -> "Text"
        is Icon -> "Icon"
        is Group.Row -> "Row"
        is Group.Column -> "Column"
        is Group.Box -> "Box"
        is Slotted.TopAppBar -> "TopAppBar"
        is Slotted.BottomAppBar -> "BottomAppBar"
        is Slotted.ExtendedFloatingActionButton -> "ExtendedFloatingActionButton"
        is Slotted.Scaffold -> "Scaffold"
        is Custom -> project.trees.first { it.id == this.treeID }.name
    }
}

@Serializable
sealed class Properties {

    @Serializable
    data class Text(val text: String, val weight: Weight = Weight.Normal, val size: Int = 14, val color: PrototypeColor = PrototypeColor.ThemeColor.OnBackground) : Properties() {

        enum class Weight {
            Thin, ExtraLight, Light, Normal, Medium, SemiBold, Bold, ExtraBold, Black,
        }
    }

    @Serializable
    data class Icon(val icon: PrototypeIcon, val tint: PrototypeColor = PrototypeColor.ThemeColor.OnBackground) : Properties()


    @Serializable
    sealed class Group : Properties() {

        @Serializable
        data class Row(
            val horizontalArrangement: PrototypeArrangement = PrototypeArrangement.Horizontal.Start,
            val verticalAlignment: PrototypeAlignment.Vertical = PrototypeAlignment.Vertical.Top,
        ) : Group()

        @Serializable
        data class Column(
            val verticalArrangement: PrototypeArrangement = PrototypeArrangement.Vertical.Top,
            val horizontalAlignment: PrototypeAlignment.Horizontal = PrototypeAlignment.Horizontal.Start,
        ) : Group()
        @Serializable
        object Box : Group()
    }


    @Serializable
    sealed class Slotted : Properties() {

        abstract val slotsEnabled: Map<String, Boolean>

        @Serializable
        data class TopAppBar(
            override val slotsEnabled: Map<String, Boolean> = mapOf("Navigation Icon" to true, "Actions" to true),
            val backgroundColor: PrototypeColor = PrototypeColor.ThemeColor.Primary,
            val contentColor: PrototypeColor = PrototypeColor.ThemeColor.OnPrimary,
            val elevation: Int = 4
        ) : Slotted()

        @Serializable
        data class BottomAppBar(
            override val slotsEnabled: Map<String, Boolean> = emptyMap(),
            val backgroundColor: PrototypeColor = PrototypeColor.ThemeColor.Primary,
            val contentColor: PrototypeColor = PrototypeColor.ThemeColor.OnPrimary,
            val elevation: Int = 4
        ) : Slotted()
        @Serializable
        data class ExtendedFloatingActionButton(
            override val slotsEnabled: Map<String, Boolean> = mapOf("Icon" to true),
            val backgroundColor: PrototypeColor = PrototypeColor.ThemeColor.Secondary,
            val contentColor: PrototypeColor = PrototypeColor.ThemeColor.OnSecondary,
            val defaultElevation: Int = 6,
            val pressedElevation: Int = 12,
        ) : Slotted()

        @Serializable
        data class Scaffold(
            override val slotsEnabled: Map<String, Boolean> = mapOf("Top App Bar" to true, "Bottom App Bar" to false, "Floating Action Button" to true, "Drawer" to false),
            val backgroundColor: PrototypeColor = PrototypeColor.ThemeColor.Background,
            val contentColor: PrototypeColor = PrototypeColor.ThemeColor.OnBackground,
            val drawerBackgroundColor: PrototypeColor = PrototypeColor.ThemeColor.Background,
            val drawerContentColor: PrototypeColor = PrototypeColor.ThemeColor.OnBackground,
            val drawerElevation: Int = 16,
            val floatingActionButtonPosition: FabPosition = FabPosition.End,
            val isFloatingActionButtonDocked: Boolean = false,
        ) : Slotted() {
            enum class FabPosition {
                Center, End
            }

            fun FabPosition.toCode() = when(this) {
                FabPosition.Center -> "FabPosition.Center"
                FabPosition.End -> "FabPosition.End"
            }
        }
    }

    object Blank : Properties()
}

fun <T: Properties.Slotted> T.withSlotsEnabled(slotsEnabled: Map<String, Boolean>): T = when(this) {
    is Properties.Slotted.TopAppBar -> this.copy(slotsEnabled = slotsEnabled) as T
    is Properties.Slotted.BottomAppBar -> this.copy(slotsEnabled = slotsEnabled) as T
    is Properties.Slotted.ExtendedFloatingActionButton -> this.copy(slotsEnabled = slotsEnabled) as T
    is Properties.Slotted.Scaffold -> this.copy(slotsEnabled = slotsEnabled) as T
    else -> throw Error("Not possible")
}

@Serializable
data class Slot(val name: String, val group: PrototypeComponent.Group = PrototypeComponent.Group.Box(), val optional: Boolean = true)

fun PrototypeComponent.Group.withChildren(children: List<PrototypeComponent> = this.children): PrototypeComponent.Group {
    return when (this) {
        is PrototypeComponent.Group.Column -> this.copy(children = children)
        is PrototypeComponent.Group.Row -> this.copy(children = children)
        is PrototypeComponent.Group.Box -> this.copy(children = children)
    }
}

fun PrototypeComponent.Slotted.withSlots(slots: List<Slot>): PrototypeComponent.Slotted {
    return when (this) {
        is PrototypeComponent.Slotted.ExtendedFloatingActionButton -> this.copy(slots = slots)
        is PrototypeComponent.Slotted.TopAppBar -> this.copy(slots = slots)
        is PrototypeComponent.Slotted.BottomAppBar -> this.copy(slots = slots)
        is PrototypeComponent.Slotted.Scaffold -> this.copy(slots = slots)
    }
}

/**
 * Creates a copy of a component tree with a component added next to (or in some cases nested in) its sibling.
 * [adding] is nested as the first child of [sibling] only if sibling is an instance of [Component.Group] and [addBefore] is false.
 * Used recursively, and returns copy of component tree with no changes if [sibling] can't be found.
 * @param adding the component to add to the tree
 * @param sibling the component that [adding] is inserted next to (or or in some cases nested in)
 * @param addBefore whether [adding] should be inserted before or after [sibling]
 */
fun PrototypeComponent.plusChildInTree(adding: PrototypeComponent, parent: PrototypeComponent.Group, indexInParent: Int): PrototypeComponent {
    println("adding child to tree - adding = $adding, parent = $parent, indexInParent = $indexInParent, this = $this")
    return when {
        this == parent -> {
            if (this !is PrototypeComponent.Group) throw Error("Can only add a child to a component that is a PrototypeComponent.Group")
            this.withChildren(children.plusElement(adding, indexInParent))
        }
        this is PrototypeComponent.Slotted -> {
            val newSlots = slots.map { slot ->
                val newTree = slot.group.plusChildInTree(adding, parent, indexInParent)
                println("old = ${slot.group}")
                println("new = $newTree")
                slot.copy(group = newTree as PrototypeComponent.Group)
            }
            this.withSlots(newSlots)
        }
        this is PrototypeComponent.Group -> {
            this.withChildren(children = children.map { it.plusChildInTree(adding, parent, indexInParent) })
        }
        else -> this
    }
}

/**
 * Creates a copy of a component tree with a component removed from it.
 * Used recursively, and returns copy of component tree with no changes if [component] can't be found.
 * @param component the component to remove from the tree
 */
fun PrototypeComponent.minusChildFromTree(component: PrototypeComponent): PrototypeComponent {
    return when {
        this is PrototypeComponent.Slotted -> this.withSlots(slots = this.slots.map { slot -> slot.copy(group = slot.group.minusChildFromTree(component) as PrototypeComponent.Group) })
        this !is PrototypeComponent.Group -> this
        component !in this.children -> this.withChildren(children = this.children.map { it.minusChildFromTree(component) })
        else -> this.withChildren(children = this.children - component)
    }
}

/**
 * Creates a copy of a component tree with a component updated it. Finds original component in tree based on [PrototypeComponent.id]
 * Used recursively, and returns copy of component tree with no changes if [component] can't be found.
 * @param component the component to update from the tree
 */
fun PrototypeComponent.updatedChildInTree(component: PrototypeComponent): PrototypeComponent {
    return when {
        this.id == component.id -> component
        this is PrototypeComponent.Group -> this.withChildren(children = this.children.map { it.updatedChildInTree(component) })
        this is PrototypeComponent.Slotted -> this.withSlots(slots = this.slots.map { it.copy(group = it.group.updatedChildInTree(component) as PrototypeComponent.Group) })
        else -> this
    }
}

fun PrototypeComponent.updatedModifier(modifier: PrototypeModifier): PrototypeComponent {
    val updatedModifiers = this.modifiers.map { if (it.id == modifier.id) modifier else it }
    return this.copy(modifiers = updatedModifiers)
}

fun PrototypeComponent.findByIDInTree(id: String): PrototypeComponent? {
    if (this.id == id) return this
    if (this is PrototypeComponent.Group) {
        for (child in this.children) {
            child.findByIDInTree(id)?.let { return it }
        }
    }
    if (this is PrototypeComponent.Slotted) {
        this.slots.forEach { slot ->
            slot.group.findByIDInTree(id)?.let { return it }
        }
    }
    return null
}

/**
 * Recursively traverses a tree and finds the parent and child index of a component
 * @param component the component to find the parent of
 */
fun PrototypeComponent.findParentOfComponent(component: PrototypeComponent): Pair<PrototypeComponent.Group, Int>? =
    when (this) {
        is PrototypeComponent.Slotted -> this.slots.map { it.group.findParentOfComponent(component) }.filterNotNull().firstOrNull()
        is PrototypeComponent.Group -> {
            println("finding parent for $component, this = $this")
            val index = children.indexOf(component)
            println("index = $index")
            val parentPair = if (index == -1) children.map { it.findParentOfComponent(component) }.filterNotNull().firstOrNull() else Pair(this, index)
            println("parentPair = $parentPair")
            parentPair
        }
        else -> null
    }

fun PrototypeComponent.findModifierByIDInTree(id: String): PrototypeModifier? {
    //try to find the id in this component's modifiers
    modifiers.find { it.id == id }?.let { return it }

    //if not try to find it in children
    return when (this) {
        is PrototypeComponent.Group -> children.mapNotNull { it.findModifierByIDInTree(id) }.firstOrNull()
        is PrototypeComponent.Slotted -> slots.mapNotNull { it.group.findModifierByIDInTree(id) }.firstOrNull()
        else -> null
    }
}

/**
 * Creates a copy of a component tree with a custom component replaced by another tree. Finds original component in tree based on [PrototypeComponent.id]
 * Used recursively, and returns copy of component tree with no changes if [component] can't be found.
 * @param customTreeID the id of the custom component to replace in the tree
 * @param replacementComponent the component to replace it with
 */
fun PrototypeComponent.replaceCustomWith(customTreeID: String, replacementComponent: PrototypeComponent) : PrototypeComponent {
    return when {
        this is PrototypeComponent.Custom && this.treeID == customTreeID -> replacementComponent.copy(id = UUID.randomUUID().toString(), modifiers = this.modifiers + replacementComponent.modifiers)
        this is PrototypeComponent.Group -> this.withChildren(children = this.children.map { it.replaceCustomWith(customTreeID, replacementComponent) })
        this is PrototypeComponent.Slotted -> this.withSlots(slots = this.slots.map { it.copy(group = it.group.replaceCustomWith(customTreeID, replacementComponent) as PrototypeComponent.Group) })
        else -> this
    }
}

fun PrototypeComponent.replaceWithCustom(oldTreeID: String, replacementCustomComponent: PrototypeComponent.Custom): PrototypeComponent {
    return when {
        this.id == oldTreeID -> replacementCustomComponent.copy(id = UUID.randomUUID().toString())
        this is PrototypeComponent.Group -> this.withChildren(this.children.map { it.replaceWithCustom(oldTreeID, replacementCustomComponent) })
        this is PrototypeComponent.Slotted -> this.withSlots(slots = this.slots.map { it.copy(group = it.group.replaceWithCustom(oldTreeID, replacementCustomComponent) as PrototypeComponent.Group) })
        else -> this
    }
}

fun PrototypeComponent.containsCustomComponent(customTreeID: String): Boolean {
    return when {
        this is PrototypeComponent.Custom && this.treeID == customTreeID -> true
        this is PrototypeComponent.Group -> this.children.any { it.containsCustomComponent(customTreeID) }
        this is PrototypeComponent.Slotted -> this.slots.any { it.group.containsCustomComponent(customTreeID) }
        else -> false
    }
}

fun PrototypeComponent.replaceParent(replacementComponent: PrototypeComponent): Pair<PrototypeComponent, Boolean> {
    val losesChildren = when (this) {
        is PrototypeComponent.Group -> this.children.isNotEmpty() && !(replacementComponent is PrototypeComponent.Group || replacementComponent is PrototypeComponent.Slotted)
        is PrototypeComponent.Slotted -> this.slots.flatMap { it.group.children }.isNotEmpty() && !(replacementComponent is PrototypeComponent.Group || replacementComponent is PrototypeComponent.Slotted)
        else -> false
    }
    val oldChildrenSlots = when(this) {
        is PrototypeComponent.Group -> listOf(this.children)
        is PrototypeComponent.Slotted -> this.slots.map { it.group.children }
        else -> emptyList()
    }
    val newParent = when (replacementComponent) {
        is PrototypeComponent.Group -> replacementComponent.withChildren(oldChildrenSlots.flatten())
        is PrototypeComponent.Slotted -> {
            val newSlots = replacementComponent.slots.mapIndexed { index, slot ->
                val isLastNewSlot = index == replacementComponent.slots.size - 1
                val oldSlotChildren: List<PrototypeComponent> = when (isLastNewSlot) {
                    false -> oldChildrenSlots.getOrNull(index) ?: emptyList()
                    true -> oldChildrenSlots.slice(index.coerceAtMost(oldChildrenSlots.size - 1) until oldChildrenSlots.size).flatten()
                }
                val newGroup = slot.group.withChildren(slot.group.children + oldSlotChildren)
                if (index == 0) slot.copy(group = newGroup) else slot
            }
            replacementComponent.withSlots(newSlots)
        }
        else -> replacementComponent
    }
    val newComponent = newParent.copy(id = this.id, modifiers = this.modifiers, properties = if (this::class == replacementComponent::class) this.properties else replacementComponent.properties)
    return Pair(newComponent, losesChildren)
}
