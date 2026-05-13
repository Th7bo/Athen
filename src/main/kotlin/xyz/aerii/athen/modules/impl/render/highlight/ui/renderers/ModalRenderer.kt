package xyz.aerii.athen.modules.impl.render.highlight.ui.renderers

import net.minecraft.client.gui.GuiGraphics
import xyz.aerii.athen.api.rendering.ui.effects.outline.outline
import xyz.aerii.athen.api.rendering.ui.shapes.rectangle.rectangle
import xyz.aerii.athen.api.rendering.ui.text.vanilla.extensions.extractText
import xyz.aerii.athen.modules.impl.render.highlight.ui.data.HighlightEntry
import xyz.aerii.athen.modules.impl.render.highlight.ui.data.UIZoneType
import xyz.aerii.athen.ui.IZoneType
import xyz.aerii.athen.ui.InputField
import xyz.aerii.athen.ui.UIZone
import xyz.aerii.athen.ui.base.AbstractModalRenderer
import xyz.aerii.athen.ui.themes.Catppuccin.Mocha
import xyz.aerii.library.api.client

class ModalRenderer(
    mw: Int,
    mh: Int,
    fh: Int,
    padding: Int
) : AbstractModalRenderer<HighlightEntry>(mw, mh, fh, padding) {
    override val create = "Create Highlight"
    override val edit = "Edit Highlight"
    override val zone0: IZoneType = UIZoneType.MODAL_SAVE
    override val zone1: IZoneType = UIZoneType.MODAL_CANCEL

    val nameField = InputField("Name or entity type")
    val colorField = InputField("Hex color (e.g. ff0000)")
    val maxHpField = InputField("-1 for any")
    var typed = false

    override fun fields(graphics: GuiGraphics, mx: Int, my: Int, x0: Int, y0: Int, cy: Int, fw: Int, zones: MutableList<UIZone>) {
        var cy = cy

        graphics.extractText(if (typed) "Entity Type" else "Name", x0 + padding, cy, false, Mocha.Subtext0.argb)
        cy += client.font.lineHeight + 2
        nameField.draw(graphics, mx, my, x0 + padding, cy, fw) { zx, zy, zw, zh -> zones.add(UIZone(zx, zy, zw, zh, if (typed) UIZoneType.MODAL_TYPE else UIZoneType.MODAL_NAME)) }
        cy += fh + 8

        val hw = fw / 2 - 4
        graphics.extractText("Color", x0 + padding, cy, false, Mocha.Subtext0.argb)
        graphics.extractText("Max HP", x0 + padding + hw + 8, cy, false, Mocha.Subtext0.argb)
        cy += client.font.lineHeight + 2

        val preview = colorField.value.removePrefix("#")
        val parsed = preview.toIntOrNull(16)
        val swatchW = 18
        val colorX = x0 + padding

        if (parsed != null) {
            val pc = parsed or 0xFF000000.toInt()
            graphics.rectangle(colorX, cy + (fh - 14) / 2, 14, 14, pc)
            graphics.outline(colorX, cy + (fh - 14) / 2, 14, 14, 1, Mocha.Overlay0.argb)
        } else {
            graphics.rectangle(colorX, cy + (fh - 14) / 2, 14, 14, Mocha.Surface0.argb)
            graphics.outline(colorX, cy + (fh - 14) / 2, 14, 14, 1, Mocha.Overlay0.argb)
        }

        colorField.draw(graphics, mx, my, colorX + swatchW, cy, hw - swatchW) { zx, zy, zw, zh -> zones.add(UIZone(zx, zy, zw, zh, UIZoneType.MODAL_COLOR)) }
        maxHpField.draw(graphics, mx, my, x0 + padding + hw + 8, cy, hw) { zx, zy, zw, zh -> zones.add(UIZone(zx, zy, zw, zh, UIZoneType.MODAL_MAX_HP)) }
    }

    fun open(isTyped: Boolean) {
        entry = null
        typed = isTyped
        reset()
        open = true
    }

    fun open(e: HighlightEntry) {
        entry = e
        typed = e.typed
        open = true

        nameField.reset(true)
        nameField.value = e.name
        nameField.cursor = e.name.length
        nameField.focused = true

        colorField.reset(true)
        colorField.value = e.color.toHexString()
        colorField.cursor = colorField.value.length

        maxHpField.reset(true)
        maxHpField.value = if (e.max == -1) "" else e.max.toString()
        maxHpField.cursor = maxHpField.value.length
    }

    override fun onClose() {
        nameField.focused = false
        colorField.focused = false
        maxHpField.focused = false
    }

    private fun reset() {
        nameField.reset(true)
        nameField.focused = true
        colorField.reset(true)
        colorField.value = "ff0000"
        colorField.cursor = 6
        maxHpField.reset(true)
    }

    private fun Int.toHexString(): String = Integer.toHexString(this).padStart(6, '0')
}