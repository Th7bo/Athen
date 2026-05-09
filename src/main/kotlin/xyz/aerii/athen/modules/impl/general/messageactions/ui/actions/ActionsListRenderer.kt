package xyz.aerii.athen.modules.impl.general.messageactions.ui.actions

import net.minecraft.client.gui.GuiGraphics
import xyz.aerii.athen.modules.impl.general.messageactions.MessageActions
import xyz.aerii.athen.modules.impl.general.messageactions.ui.UIZoneType
import xyz.aerii.athen.ui.UIZone
import xyz.aerii.athen.ui.themes.Catppuccin.Mocha
import xyz.aerii.athen.utils.render.Render2D.drawOutline
import xyz.aerii.athen.utils.render.Render2D.drawRectangle
import xyz.aerii.athen.utils.render.Render2D.text
import xyz.aerii.library.api.client

class ActionsListRenderer(
    private val entryH: Int,
    private val entrySpacing: Int,
    private val fh: Int,
    private val padding: Int
) {
    private var scroll = 0
    private var max = 0

    fun draw(graphics: GuiGraphics, mx: Int, my: Int, lx: Int, ly: Int, lw: Int, lh: Int, entries: List<ActionEntryView>, modalOpen: Boolean, zones: MutableList<UIZone>) {
        if (entries.isEmpty()) return graphics.text("No actions", lx + (lw - client.font.width("No actions")) / 2, ly + lh / 2, false, Mocha.Subtext0.argb)

        max = maxOf(0, entries.size * (entryH + entrySpacing) - entrySpacing - lh)
        scroll = scroll.coerceIn(-max, 0)

        graphics.enableScissor(lx - 2, ly - 2, lx + lw + 2, ly + lh + 2)

        var cy = ly + scroll
        for (e in entries) {
            if (cy + entryH > ly - 5 && cy < ly + lh + 5) entry(graphics, mx, my, lx, cy, lw, e, modalOpen, zones)
            cy += entryH + entrySpacing
        }

        graphics.disableScissor()
    }

    private fun entry(graphics: GuiGraphics, mx: Int, my: Int, x: Int, y: Int, w: Int, view: ActionEntryView, open: Boolean, zones: MutableList<UIZone>) {
        val a = !open && mx in x until x + w && my in y until y + entryH
        val b = view.entry.enabled && (view.entry.category.isEmpty() || MessageActions.categories.find { it.name == view.entry.category }?.enabled != false)

        graphics.drawRectangle(x, y, w, entryH, if (a) Mocha.Surface1.argb else if (!b) Mocha.Red.withAlpha(0.15f) else Mocha.Surface0.argb)
        graphics.drawOutline(x, y, w, entryH, 1, if (!b) Mocha.Red.withAlpha(0.6f) else Mocha.Overlay0.argb)

        var c = x + padding
        val d = y + (entryH - 14) / 2
        val e = !open && mx in c until c + 14 && my in d until d + 14

        graphics.drawRectangle(c, d, 14, 14, if (e) Mocha.Surface2.argb else Mocha.Base.argb)
        graphics.drawOutline(c, d, 14, 14, 1, if (b) Mocha.Green.argb else Mocha.Overlay0.argb)
        if (view.toggle > 0.05f) graphics.drawRectangle(c + 3, d + 3, 8, 8, Mocha.Green.withAlpha(view.toggle))
        zones.add(UIZone(c, d, 14, 14, UIZoneType.ENTRY_TOGGLE, view.index))
        c += 14 + padding

        val a0 = view.entry.match.displayName
        val b0 = client.font.width(a0) + 8
        graphics.drawRectangle(c, y + (entryH - fh) / 2, b0, fh, Mocha.Surface2.argb)
        graphics.drawOutline(c, y + (entryH - fh) / 2, b0, fh, 1, Mocha.Crust.argb)
        graphics.text(a0, c + 4, y + (entryH - client.font.lineHeight) / 2 + 1, false, Mocha.Mauve.argb)
        c += b0 + padding

        if (view.entry.cancel) {
            val cw = client.font.width("✕") + 6
            graphics.drawRectangle(c, y + (entryH - fh) / 2, cw, fh, Mocha.Red.withAlpha(0.2f))
            graphics.drawOutline(c, y + (entryH - fh) / 2, cw, fh, 1, Mocha.Red.withAlpha(0.6f))
            graphics.text("✕", c + 3, y + (entryH - client.font.lineHeight) / 2 + 1, false, Mocha.Red.argb)
            c += cw + padding
        }

        graphics.enableScissor(c, y, c + (w - (c - x) - 60 - padding * 2), y + entryH)
        graphics.text(view.entry.pattern, c, y + (entryH - client.font.lineHeight) / 2 + 1, false, if (b) Mocha.Text.argb else Mocha.Red.argb)
        graphics.disableScissor()

        val a1 = x + w - 60 - padding * 2
        val b1 = !open && mx in a1 until a1 + 40 && my in y + (entryH - fh) / 2 until y + (entryH - fh) / 2 + fh
        graphics.drawRectangle(a1, y + (entryH - fh) / 2, 40, fh, if (b1) Mocha.Surface2.argb else Mocha.Base.argb)
        graphics.drawOutline(a1, y + (entryH - fh) / 2, 40, fh, 1, Mocha.Overlay0.argb)
        graphics.text("Edit", a1 + (40 - client.font.width("Edit")) / 2, y + (entryH - client.font.lineHeight) / 2 + 1, false, Mocha.Text.argb)
        zones.add(UIZone(a1, y + (entryH - fh) / 2, 40, fh, UIZoneType.ENTRY_EDIT, view.index))

        val a2 = x + w - 20 - padding
        val b2 = !open && mx in a2 until a2 + 20 && my in y + (entryH - fh) / 2 until y + (entryH - fh) / 2 + fh
        graphics.drawRectangle(a2, y + (entryH - fh) / 2, 20, fh, if (b2) Mocha.Red.argb else Mocha.Base.argb)
        graphics.drawOutline(a2, y + (entryH - fh) / 2, 20, fh, 1, Mocha.Overlay0.argb)
        graphics.text("×", a2 + (21 - client.font.width("×")) / 2, y + (entryH - client.font.lineHeight) / 2 + 1, false, Mocha.Text.argb)
        zones.add(UIZone(a2, y + (entryH - fh) / 2, 20, fh, UIZoneType.ENTRY_DELETE, view.index))
    }

    fun scroll(amount: Int) {
        scroll = (scroll + amount).coerceIn(-max, 0)
    }
}