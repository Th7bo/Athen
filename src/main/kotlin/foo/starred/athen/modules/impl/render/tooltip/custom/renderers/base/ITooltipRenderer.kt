package foo.starred.athen.modules.impl.render.tooltip.custom.renderers.base

import foo.starred.athen.modules.impl.render.tooltip.custom.renderers.data.TooltipContext

interface ITooltipRenderer {
    fun TooltipContext.render()

    fun r(context: TooltipContext) {
        context.render()
    }
}
