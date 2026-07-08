package xyz.aerii.athen.api.rendering.ui.dsl.constraints.impl.size

import xyz.aerii.athen.api.rendering.ui.dsl.constraints.base.ISizeConstraint
import xyz.aerii.athen.api.rendering.ui.dsl.elements.primitives.base.impl.IPrimitiveElement

class MixedSizeConstraint(val w: ISizeConstraint, val h: ISizeConstraint) : ISizeConstraint {
    override fun width(element: IPrimitiveElement<*>, parent: IPrimitiveElement<*>): Int {
        return w.width(element, parent)
    }

    override fun height(element: IPrimitiveElement<*>, parent: IPrimitiveElement<*>): Int {
        return h.height(element, parent)
    }
}