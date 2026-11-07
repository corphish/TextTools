package com.corphish.quicktools.ui.common.walkthrough

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.GlobalPositionAwareModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.currentValueOf
import com.corphish.quicktools.data.WalkthroughStep

/**
 * Window bounds of the walkthrough targets on screen, keyed by target id. Only visible targets
 * are kept: a target scrolled fully out of view or removed from the layout is dropped.
 */
class WalkthroughTargetRegistry {
    private val _bounds: SnapshotStateMap<String, Rect> = mutableStateMapOf()

    /** Visible bounds in window coordinates per target id; reading it is observable. */
    val bounds: Map<String, Rect> get() = _bounds

    /**
     * Records the visible window bounds of the target [id].
     *
     * @param windowBounds Visible bounds; an empty rect removes the target.
     */
    fun update(id: String, windowBounds: Rect) {
        when {
            windowBounds.isEmpty -> _bounds.remove(id)
            _bounds[id] != windowBounds -> _bounds[id] = windowBounds
        }
    }

    /** Forgets the target [id]. */
    fun remove(id: String) {
        _bounds.remove(id)
    }
}

/** Registry of the enclosing [WalkthroughHost]; null outside one, where targets do nothing. */
internal val LocalWalkthroughRegistry = staticCompositionLocalOf<WalkthroughTargetRegistry?> { null }

/**
 * Marks this composable as the target [id] of walkthrough steps, so the enclosing
 * [WalkthroughHost] can highlight it. Outside a host it has no effect, e.g. in previews.
 * Ids must be unique on the screen; targets in dialogs or popups are not supported, as they
 * live in a separate window.
 *
 * @param id Target id used by [WalkthroughStep.targetId].
 */
fun Modifier.walkthroughTarget(id: String): Modifier = this then WalkthroughTargetElement(id)

private data class WalkthroughTargetElement(val id: String) : ModifierNodeElement<WalkthroughTargetNode>() {
    override fun create() = WalkthroughTargetNode(id)

    override fun update(node: WalkthroughTargetNode) {
        node.updateId(id)
    }
}

/** Reports the bounds of its layout to the [LocalWalkthroughRegistry] on every placement. */
private class WalkthroughTargetNode(
    private var id: String,
) : Modifier.Node(), GlobalPositionAwareModifierNode, CompositionLocalConsumerModifierNode {
    private var registry: WalkthroughTargetRegistry? = null

    override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
        val target = registry ?: currentValueOf(LocalWalkthroughRegistry)?.also { registry = it } ?: return
        target.update(id, coordinates.boundsInWindow())
    }

    /** Moves the registration to [newId]; the bounds follow on the next placement. */
    fun updateId(newId: String) {
        if (newId == id) return
        registry?.remove(id)
        id = newId
    }

    override fun onDetach() {
        registry?.remove(id)
        registry = null
    }
}
