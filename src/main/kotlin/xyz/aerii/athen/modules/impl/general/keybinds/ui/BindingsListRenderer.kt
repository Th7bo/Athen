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

class BindingsListRenderer(
    height: Int,
    spacing: Int,
    fh: Int,
    padding: Int
) : AbstractListRenderer<BindingEntry>(height, spacing, fh, padding) {
    override val string = "No keybinds"

    override fun entry(graphics: GuiGraphics, mx: Int, my: Int, x: Int, y: Int, w: Int, entry: BindingEntry, open: Boolean, zones: MutableList<UIZone>) {
        val hovered = !open && mx in x until x + w && my in y until y + height
        val en = entry.binding.enabled && (entry.binding.category.isEmpty() || Keybinds.categories.value.find { it.name == entry.binding.category }?.enabled != false)

        graphics.rectangle(x, y, w, height, if (hovered) Mocha.Surface1.argb else if (!en) Mocha.Red.withAlpha(0.15f) else Mocha.Surface0.argb)
        graphics.outline(x, y, w, height, 1, if (!en) Mocha.Red.withAlpha(0.6f) else Mocha.Overlay0.argb)

        var cx = x + padding
        val toggleY = y + (height - 14) / 2
        val tHov = !open && mx in cx until cx + 14 && my in toggleY until toggleY + 14

        graphics.rectangle(cx, toggleY, 14, 14, if (tHov) Mocha.Surface2.argb else Mocha.Base.argb)
        graphics.outline(cx, toggleY, 14, 14, 1, if (en) Mocha.Green.argb else Mocha.Overlay0.argb)
        if (entry.toggleAnim > 0.05f) graphics.rectangle(cx + 3, toggleY + 3, 8, 8, Mocha.Green.withAlpha(entry.toggleAnim))
        zones.add(UIZone(cx, toggleY, 14, 14, UIZoneType.ENTRY_TOGGLE, entry.index))
        cx += 14 + padding

        val kStr = entry.binding.keys.str()
        val kw = client.font.width(kStr) + 8
        graphics.rectangle(cx, y + (height - fh) / 2, kw, fh, Mocha.Surface2.argb)
        graphics.outline(cx, y + (height - fh) / 2, kw, fh, 1, Mocha.Crust.argb)
        graphics.extractText(kStr, cx + 4, y + (height - client.font.lineHeight) / 2 + 1, false, Mocha.Text.argb)
        cx += kw + padding

        graphics.enableScissor(cx, y, cx + (w - (cx - x) - 60 - padding * 2), y + height)
        graphics.extractText(entry.binding.command, cx, y + (height - client.font.lineHeight) / 2 + 1, false, if (en) Mocha.Text.argb else Mocha.Red.argb)
        graphics.disableScissor()

        val ex = x + w - 60 - padding * 2
        val eHov = !open && mx in ex until ex + 40 && my in y + (height - fh) / 2 until y + (height - fh) / 2 + fh
        graphics.rectangle(ex, y + (height - fh) / 2, 40, fh, if (eHov) Mocha.Surface2.argb else Mocha.Base.argb)
        graphics.outline(ex, y + (height - fh) / 2, 40, fh, 1, Mocha.Overlay0.argb)
        graphics.extractText("Edit", ex + (40 - client.font.width("Edit")) / 2, y + (height - client.font.lineHeight) / 2 + 1, false, Mocha.Text.argb)
        zones.add(UIZone(ex, y + (height - fh) / 2, 40, fh, UIZoneType.ENTRY_EDIT, entry.index))

        val dx = x + w - 20 - padding
        val dHov = !open && mx in dx until dx + 20 && my in y + (height - fh) / 2 until y + (height - fh) / 2 + fh
        graphics.rectangle(dx, y + (height - fh) / 2, 20, fh, if (dHov) Mocha.Red.argb else Mocha.Base.argb)
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
