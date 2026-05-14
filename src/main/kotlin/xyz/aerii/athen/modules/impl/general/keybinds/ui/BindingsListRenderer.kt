package xyz.aerii.athen.modules.impl.general.keybinds.ui

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.GuiGraphics
import xyz.aerii.athen.api.rendering.ui.effects.outline.outline
import xyz.aerii.athen.api.rendering.ui.shapes.rectangle.rectangle
import xyz.aerii.athen.api.rendering.ui.text.vanilla.extensions.extractText
import xyz.aerii.athen.modules.impl.general.keybinds.Keybinds
import xyz.aerii.athen.ui.UIZone
import xyz.aerii.athen.ui.base.AbstractListRenderer
import xyz.aerii.athen.ui.themes.Catppuccin.Mocha
import xyz.aerii.library.api.client
import xyz.aerii.library.utils.hovered

class BindingsListRenderer(
    height: Int,
    spacing: Int,
    fh: Int,
    padding: Int
) : AbstractListRenderer<BindingEntry>(height, spacing, fh, padding) {
    override val string = "No keybinds"

    override fun entry(graphics: GuiGraphics, mx: Int, my: Int, x: Int, y: Int, w: Int, entry: BindingEntry, open: Boolean, zones: MutableList<UIZone>) {
        val b0 = !open && hovered(x, y, w, height, true)
        val en = entry.binding.enabled && (entry.binding.category.isEmpty() || Keybinds.categories.value.find { it.name == entry.binding.category }?.enabled != false)

        graphics.rectangle(x, y, w, height, if (b0) Mocha.Surface1.argb else if (!en) Mocha.Red.withAlpha(0.15f) else Mocha.Surface0.argb)
        graphics.outline(x, y, w, height, 1, if (!en) Mocha.Red.withAlpha(0.6f) else Mocha.Overlay0.argb)

        var cx = x + padding
        val y0 = y + (height - 14) / 2
        val b1 = !open && hovered(cx, y0, 14, 14, true)

        graphics.rectangle(cx, y0, 14, 14, if (b1) Mocha.Surface2.argb else Mocha.Base.argb)
        graphics.outline(cx, y0, 14, 14, 1, if (en) Mocha.Green.argb else Mocha.Overlay0.argb)
        if (entry.toggleAnim > 0.05f) graphics.rectangle(cx + 3, y0 + 3, 8, 8, Mocha.Green.withAlpha(entry.toggleAnim))
        zones.add(UIZone(cx, y0, 14, 14, UIZoneType.ENTRY_TOGGLE, entry.index))
        cx += 14 + padding

        val text = entry.binding.keys.str()
        val w0 = client.font.width(text) + 8
        graphics.rectangle(cx, y + (height - fh) / 2, w0, fh, Mocha.Surface2.argb)
        graphics.outline(cx, y + (height - fh) / 2, w0, fh, 1, Mocha.Crust.argb)
        graphics.extractText(text, cx + 4, y + (height - client.font.lineHeight) / 2 + 1, false, Mocha.Text.argb)
        cx += w0 + padding

        graphics.enableScissor(cx, y, cx + (w - (cx - x) - 60 - padding * 2), y + height)
        graphics.extractText(entry.binding.command, cx, y + (height - client.font.lineHeight) / 2 + 1, false, if (en) Mocha.Text.argb else Mocha.Red.argb)
        graphics.disableScissor()

        val ex = x + w - 60 - padding * 2
        val b2 = !open && hovered(ex, y + (height - fh) / 2, 40, fh, true)
        graphics.rectangle(ex, y + (height - fh) / 2, 40, fh, if (b2) Mocha.Surface2.argb else Mocha.Base.argb)
        graphics.outline(ex, y + (height - fh) / 2, 40, fh, 1, Mocha.Overlay0.argb)
        graphics.extractText("Edit", ex + (40 - client.font.width("Edit")) / 2, y + (height - client.font.lineHeight) / 2 + 1, false, Mocha.Text.argb)
        zones.add(UIZone(ex, y + (height - fh) / 2, 40, fh, UIZoneType.ENTRY_EDIT, entry.index))

        val dx = x + w - 20 - padding
        val b3 = !open && hovered(dx, y + (height - fh) / 2, 20, fh, true)
        graphics.rectangle(dx, y + (height - fh) / 2, 20, fh, if (b3) Mocha.Red.argb else Mocha.Base.argb)
        graphics.outline(dx, y + (height - fh) / 2, 20, fh, 1, Mocha.Overlay0.argb)
        graphics.extractText("×", dx + (21 - client.font.width("×")) / 2, y + (height - client.font.lineHeight) / 2 + 1, false, Mocha.Text.argb)
        zones.add(UIZone(dx, y + (height - fh) / 2, 20, fh, UIZoneType.ENTRY_DELETE, entry.index))
    }

    companion object {
        fun List<Int>.str(): String = if (isEmpty()) "None" else joinToString(" + ") { it.str() }

        fun Int.str(): String = when (this) {
            -1 -> "LMB"
            -2 -> "RMB"
            -3 -> "MMB"
            in Int.MIN_VALUE..-4 -> "M${-this - 1}"
            else -> InputConstants.Type.KEYSYM.getOrCreate(this).displayName.string.let {
                if (it.length == 1) it.uppercase() else it
            }
        }
    }
}
