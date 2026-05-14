package xyz.aerii.athen.modules.impl.general.messageactions.ui.actions

import net.minecraft.client.gui.GuiGraphics
import xyz.aerii.athen.api.rendering.ui.effects.outline.outline
import xyz.aerii.athen.api.rendering.ui.shapes.rectangle.rectangle
import xyz.aerii.athen.api.rendering.ui.text.vanilla.extensions.extractText
import xyz.aerii.athen.modules.impl.general.messageactions.MessageActions
import xyz.aerii.athen.modules.impl.general.messageactions.ui.UIZoneType
import xyz.aerii.athen.ui.UIZone
import xyz.aerii.athen.ui.base.AbstractListRenderer
import xyz.aerii.athen.ui.themes.Catppuccin.Mocha
import xyz.aerii.library.api.client
import xyz.aerii.library.utils.hovered

class ActionsListRenderer(
    height: Int,
    spacing: Int,
    fh: Int,
    padding: Int
) : AbstractListRenderer<ActionEntryView>(height, spacing, fh, padding) {
    override val string = "No actions"

    override fun entry(graphics: GuiGraphics, mx: Int, my: Int, x: Int, y: Int, w: Int, entry: ActionEntryView, open: Boolean, zones: MutableList<UIZone>) {
        val a = !open && hovered(x, y, w, height, true)
        val b = entry.entry.enabled && (entry.entry.category.isEmpty() || MessageActions.categories.find { it.name == entry.entry.category }?.enabled != false)

        graphics.rectangle(x, y, w, height, if (a) Mocha.Surface1.argb else if (!b) Mocha.Red.withAlpha(0.15f) else Mocha.Surface0.argb)
        graphics.outline(x, y, w, height, 1, if (!b) Mocha.Red.withAlpha(0.6f) else Mocha.Overlay0.argb)

        var c = x + padding
        val d = y + (height - 14) / 2
        val e = !open && hovered(c, d, 14, 14, true)

        graphics.rectangle(c, d, 14, 14, if (e) Mocha.Surface2.argb else Mocha.Base.argb)
        graphics.outline(c, d, 14, 14, 1, if (b) Mocha.Green.argb else Mocha.Overlay0.argb)
        if (entry.toggle > 0.05f) graphics.rectangle(c + 3, d + 3, 8, 8, Mocha.Green.withAlpha(entry.toggle))
        zones.add(UIZone(c, d, 14, 14, UIZoneType.ENTRY_TOGGLE, entry.index))
        c += 14 + padding

        val a0 = entry.entry.match.displayName
        val b0 = client.font.width(a0) + 8
        graphics.rectangle(c, y + (height - fh) / 2, b0, fh, Mocha.Surface2.argb)
        graphics.outline(c, y + (height - fh) / 2, b0, fh, 1, Mocha.Crust.argb)
        graphics.extractText(a0, c + 4, y + (height - client.font.lineHeight) / 2 + 1, false, Mocha.Mauve.argb)
        c += b0 + padding

        if (entry.entry.cancel) {
            val cw = client.font.width("✕") + 6
            graphics.rectangle(c, y + (height - fh) / 2, cw, fh, Mocha.Red.withAlpha(0.2f))
            graphics.outline(c, y + (height - fh) / 2, cw, fh, 1, Mocha.Red.withAlpha(0.6f))
            graphics.extractText("✕", c + 3, y + (height - client.font.lineHeight) / 2 + 1, false, Mocha.Red.argb)
            c += cw + padding
        }

        graphics.enableScissor(c, y, c + (w - (c - x) - 60 - padding * 2), y + height)
        graphics.extractText(entry.entry.pattern, c, y + (height - client.font.lineHeight) / 2 + 1, false, if (b) Mocha.Text.argb else Mocha.Red.argb)
        graphics.disableScissor()

        val a1 = x + w - 60 - padding * 2
        val b1 = !open && hovered(a1, y + (height - fh) / 2, 40, fh, true)
        graphics.rectangle(a1, y + (height - fh) / 2, 40, fh, if (b1) Mocha.Surface2.argb else Mocha.Base.argb)
        graphics.outline(a1, y + (height - fh) / 2, 40, fh, 1, Mocha.Overlay0.argb)
        graphics.extractText("Edit", a1 + (40 - client.font.width("Edit")) / 2, y + (height - client.font.lineHeight) / 2 + 1, false, Mocha.Text.argb)
        zones.add(UIZone(a1, y + (height - fh) / 2, 40, fh, UIZoneType.ENTRY_EDIT, entry.index))

        val a2 = x + w - 20 - padding
        val b2 = !open && hovered(a2, y + (height - fh) / 2, 20, fh, true)
        graphics.rectangle(a2, y + (height - fh) / 2, 20, fh, if (b2) Mocha.Red.argb else Mocha.Base.argb)
        graphics.outline(a2, y + (height - fh) / 2, 20, fh, 1, Mocha.Overlay0.argb)
        graphics.extractText("×", a2 + (21 - client.font.width("×")) / 2, y + (height - client.font.lineHeight) / 2 + 1, false, Mocha.Text.argb)
        zones.add(UIZone(a2, y + (height - fh) / 2, 20, fh, UIZoneType.ENTRY_DELETE, entry.index))
    }
}