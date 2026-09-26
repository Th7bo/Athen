package foo.starred.athen.modules.impl.render.tooltip.custom.renderers.impl

import foo.starred.athen.modules.impl.render.tooltip.custom.CustomTooltip
import foo.starred.athen.modules.impl.render.tooltip.custom.renderers.base.ITooltipRenderer
import foo.starred.athen.modules.impl.render.tooltip.custom.renderers.data.TooltipContext
import foo.starred.cascade.graphics.extensions.blur.blur
import foo.starred.cascade.graphics.extensions.rectangle.hollow.hollowRectangle
import foo.starred.cascade.graphics.extensions.rectangle.solid.rectangle
import foo.starred.cascade.graphics.extensions.scissor.scissor
import foo.starred.cascade.graphics.geometry.CascadeGeometricRadius
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent

object CombinedTooltip : ITooltipRenderer {
    override fun TooltipContext.render() {
        val width1 = width + 8
        val height1 = graphics.guiHeight()
        val thickness = CustomTooltip.`border$width`

        val x0 = x - 4
        val y0 = if (height + 8 < height1 - 40) (y - 4).coerceIn(20, height1 - 20 - (height + 8)) else 20
        val y1 = y0 + 4

        val height2 = minOf(height + 8,height1 - 20 - y0).coerceAtLeast(0)

        val y2 = CustomTooltip.scroll(height, height2 - 8)
        val height3 = if (CustomTooltip.`scroll$infinite`) (if (y2 > 0) height2 - y2 else height + y2 + 8).coerceIn(0, height1 - 20 - y0) else height2

        graphics.box(x0, y0, width1, height3, thickness)
        graphics.component(font, components, x, x0, y0, width1, height3, y1 + minOf(0, y2), width, height)
        graphics.fade(x0, y0, width1, height3, y2, height)
    }

    private fun GuiGraphicsExtractor.box(x: Int, y: Int, width: Int, height: Int, thickness: Int) {
        val x = x.toFloat()
        val y = y.toFloat()
        val width = width.toFloat()
        val height = height.toFloat()
        val thickness = thickness.toFloat()

        if (CustomTooltip.background) {
            if (CustomTooltip.`background$blur`) blur(x, y, width, height, CustomTooltip.`background$color`, CascadeGeometricRadius.ZERO, CustomTooltip.`background$strength`)
            else rectangle(x, y, width, height, CustomTooltip.`background$color`)
        }

        if (CustomTooltip.border && thickness > 0) {
            val color = if (CustomTooltip.`border$rarity`) CustomTooltip.color else CustomTooltip.`border$color`
            hollowRectangle(x, y, width, height, thickness, color, inset = false)
        }
    }

    private fun GuiGraphicsExtractor.component(font: Font, list: List<ClientTooltipComponent>, x0: Int, x1: Int, y1: Int, width: Int, height: Int, y0: Int, width0: Int, height0: Int) {
        scissor(x1, y1, width, height) {
            val list = list.withIndex()

            var y00 = y0
            for ((i, value) in list) {
                value.extractText(this, font, x0, y00)
                y00 += value.getHeight(font) + if (i == 0) 2 else 0
            }

            y00 = y0
            for ((i, value) in list) {
                value.extractImage(font, x0, y00, width0, height0, this)
                y00 += value.getHeight(font) + if (i == 0) 2 else 0
            }
        }
    }

    private fun GuiGraphicsExtractor.fade(x: Int, y: Int, width: Int, height: Int, y1: Int, height1: Int) {
        val color1 = CustomTooltip.`background$color` or 0xFF000000.toInt()
        val color2 = color1 and 0x00FFFFFF

        scissor(x, y, width, height) {
            if (y1 < 0) {
                fillGradient(x, y, x + width, y + 18, color1, color2)
            }

            if (y1 > 0 || height1 + y1 > height) {
                fillGradient(x, y + height - 18, x + width, y + height, color2, color1)
            }
        }
    }
}
