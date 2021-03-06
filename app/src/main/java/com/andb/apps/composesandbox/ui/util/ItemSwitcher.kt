package com.andb.apps.composesandbox.ui.util


import androidx.compose.animation.asDisposableClock
import androidx.compose.animation.core.TransitionDefinition
import androidx.compose.animation.core.TransitionState
import androidx.compose.animation.core.createAnimation
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AmbientAnimationClock

/**
 * [ItemSwitcher] allows to switch between two layouts with a transition defined by
 * [transitionDefinition].
 *
 * @param current is a key representing your current layout state. Every time you change a key
 * the animation will be triggered. The [content] called with the old key will be animated out while
 * the [content] called with the new key will be animated in.
 * @param transitionDefinition is a [TransitionDefinition] using [ItemTransitionState] as
 * the state type.
 * @param modifier Modifier to be applied to the animation container.
 */
@Composable
fun <T> ItemSwitcher(
    current: T,
    modifier: Modifier = Modifier,
    animateIf: (old: T?, current: T) -> Boolean = { old, new -> old != new },
    keyFinder: (item: T) -> Any = { it as Any },
    transitionDefinition: TransitionDefinition<ItemTransitionState>,
    content: @Composable (T, TransitionState) -> Unit
) {
    val state = remember { ItemTransitionInnerState<T>() }

    if (state.items.isEmpty() || animateIf(state.current, current)) {
        state.current = current
        val keys = state.items.map { it.key }.toMutableList()
        if (!keys.contains(current)) {
            keys.add(current)
        }
        state.items.clear()

        keys.mapTo(state.items) { key ->
            ItemTransitionItem(key) { children ->
                val clock = AmbientAnimationClock.current.asDisposableClock()
                val visible = key == current

                val anim = remember(clock, transitionDefinition) {
                    transitionDefinition.createAnimation(
                        clock = clock,
                        initState = when {
                            visible -> ItemTransitionState.BecomingVisible
                            else -> ItemTransitionState.Visible
                        }
                    )
                }

                onCommit(visible) {
                    anim.onStateChangeFinished = { _ ->
                        if (key == state.current) {
                            // leave only the current in the list
                            state.items.removeAll { it.key != state.current }
                            state.invalidate()
                        }
                    }
                    anim.onUpdate = { state.invalidate() }

                    val targetState = when {
                        visible -> ItemTransitionState.Visible
                        else -> ItemTransitionState.BecomingNotVisible
                    }
                    anim.toState(targetState)
                }

                children(anim)
            }
        }
    } else if (current != state.current) {
        state.current = current
        state.items[state.items.size - 1] = state.items.last().copy(key = current)
    }
    Box(modifier) {
        state.invalidate = invalidate
        state.items.forEach { (item, transition) ->
            key(keyFinder.invoke(item)) {
                transition { transitionState ->
                    content(item, transitionState)
                }
            }
        }
    }
}

enum class ItemTransitionState {
    Visible, BecomingNotVisible, BecomingVisible,
}

private class ItemTransitionInnerState<T> {
    // we use Any here as something which will not be equals to the real initial value
    var current: T? = null
    var items = mutableListOf<ItemTransitionItem<T>>()
    var invalidate: () -> Unit = { }
}

private data class ItemTransitionItem<T>(
    val key: T,
    val content: ItemTransitionContent
)

private typealias ItemTransitionContent = @Composable (children: @Composable (TransitionState) -> Unit) -> Unit