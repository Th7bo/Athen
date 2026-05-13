package xyz.aerii.athen.modules.impl.general.messageactions.ui.actions

import net.minecraft.client.gui.GuiGraphics
import xyz.aerii.athen.api.rendering.ui.effects.outline.outline
import xyz.aerii.athen.api.rendering.ui.shapes.rectangle.rectangle
import xyz.aerii.athen.api.rendering.ui.text.vanilla.extensions.extractText
import xyz.aerii.athen.modules.impl.general.messageactions.data.MatchType
import xyz.aerii.athen.modules.impl.general.messageactions.MessageActions
import xyz.aerii.athen.modules.impl.general.messageactions.actions.IMessageAction
import xyz.aerii.athen.modules.impl.general.messageactions.ui.UIZoneType
import xyz.aerii.athen.ui.IZoneType
import xyz.aerii.athen.ui.InputField
import xyz.aerii.athen.ui.UIZone
import xyz.aerii.athen.ui.base.AbstractModalRenderer
import xyz.aerii.athen.ui.themes.Catppuccin.Mocha
import xyz.aerii.library.api.client

class ActionModalRenderer(
    mw: Int,
    mh: Int,
    fh: Int,
    padding: Int
) : AbstractModalRenderer<ActionEntryView>(mw, mh, fh, padding) {
    override val create = "Create Action"
    override val edit = "Edit Action"
    override val zone0: IZoneType = UIZoneType.MODAL_SAVE
    override val zone1: IZoneType = UIZoneType.MODAL_CANCEL

    val patternField = InputField("Pattern to match")
    val valueField = InputField("Action value")
    val delayField = InputField("0")
    var match = MatchType.CONTAINS
    var action = 0
    var cancel = false
    var category = ""

    var matchOpen = false
    var matchY = 0

    var catOpen = false
    var catY = 0

    override val dropdown: Boolean
        get() = matchOpen || catOpen

    override fun fields(graphics: GuiGraphics, mx: Int, my: Int, x0: Int, y0: Int, cy: Int, fw: Int, zones: MutableList<UIZone>) {
        var cy = cy
        val hw = fw / 2 - 4

        graphics.extractText("Pattern", x0 + padding, cy, false, Mocha.Subtext0.argb)
        graphics.extractText("Match Type", x0 + padding + hw + 8, cy, false, Mocha.Subtext0.argb)
        cy += client.font.lineHeight + 2

        patternField.draw(graphics, mx, my, x0 + padding, cy, hw) { zx, zy, zw, zh -> zones.add(UIZone(zx, zy, zw, zh, UIZoneType.MODAL_PATTERN)) }
        matchY = cy
        dropdown0(graphics, mx, my, x0 + padding + hw + 8, cy, hw, zones)
        cy += fh + 8

        graphics.extractText("Action", x0 + padding, cy, false, Mocha.Subtext0.argb)
        cy += client.font.lineHeight + 2

        val actions = IMessageAction.all()
        val aw = (fw - (actions.size - 1) * 4) / actions.size
        for (a in actions) {
            val i = a.id
            val idx = actions.indexOf(a)
            val ax = x0 + padding + idx * (aw + 4)
            val selected = action == i
            val hovered = !dropdown && mx in ax until ax + aw && my in cy until cy + 14

            graphics.rectangle(ax, cy, aw, 14, if (selected) Mocha.Mauve.argb else if (hovered) Mocha.Surface2.argb else Mocha.Surface1.argb)
            graphics.outline(ax, cy, aw, 14, 1, if (selected) Mocha.Mauve.argb else Mocha.Overlay0.argb)
            graphics.enableScissor(ax + 2, cy, ax + aw - 2, cy + 14)
            graphics.extractText(a.name, ax + (aw - client.font.width(a.name)) / 2, cy + (14 - client.font.lineHeight) / 2 + 1, false, if (selected) Mocha.Base.argb else Mocha.Text.argb)
            graphics.disableScissor()
            zones.add(UIZone(ax, cy, aw, 14, UIZoneType.MODAL_ACTION_TYPE, a.id))
        }
        cy += 14 + 8

        graphics.extractText(if (action == 0) "Value" else IMessageAction.all().firstOrNull { it.id == action }?.name ?: "Value", x0 + padding, cy, false, if (action == 0) Mocha.Overlay0.argb else Mocha.Subtext0.argb)
        graphics.extractText("Category", x0 + padding + hw + 8, cy, false, Mocha.Subtext0.argb)
        cy += client.font.lineHeight + 2

        if (action == 0) {
            graphics.rectangle(x0 + padding, cy, hw, fh, Mocha.Crust.argb)
            graphics.outline(x0 + padding, cy, hw, fh, 1, Mocha.Surface0.argb)
        } else {
            valueField.draw(graphics, mx, my, x0 + padding, cy, hw) { zx, zy, zw, zh -> zones.add(UIZone(zx, zy, zw, zh, UIZoneType.MODAL_ACTION_VALUE)) }
        }

        catY = cy
        dropdown1(graphics, mx, my, x0 + padding + hw + 8, cy, hw, zones)
        cy += fh + 8

        val c = "Cancel message"
        val c0 = client.font.width(c)
        val cx = x0 + padding
        val cy0 = cy + (fh - 14) / 2
        val ch = !dropdown && mx in cx until cx + 14 && my in cy0 until cy0 + 14

        graphics.rectangle(cx, cy0, 14, 14, if (ch) Mocha.Surface2.argb else Mocha.Base.argb)
        graphics.outline(cx, cy0, 14, 14, 1, if (cancel) Mocha.Red.argb else Mocha.Overlay0.argb)

        if (cancel) graphics.rectangle(cx + 3, cy0 + 3, 8, 8, Mocha.Red.argb)
        graphics.extractText(c, cx + 18, cy0 + (14 - client.font.lineHeight) / 2 + 1, false, Mocha.Subtext0.argb)
        zones.add(UIZone(cx, cy0, 14 + 4 + c0, 14, UIZoneType.MODAL_CANCEL_TOGGLE))

        graphics.extractText("Delay (s)", x0 + padding + hw + 8, cy + (fh - client.font.lineHeight) / 2 + 1, false, Mocha.Subtext0.argb)
        delayField.draw(graphics, mx, my, x0 + padding + hw + 8 + client.font.width("Delay (s)") + 4, cy, hw - client.font.width("Delay (s)") - 4) { zx, zy, zw, zh -> zones.add(UIZone(zx, zy, zw, zh, UIZoneType.MODAL_DELAY)) }

        graphics.extractText("Regex: use $0 for full message, and $1, $2, $3... for groups", x0 + padding, y0 + mh - fh - padding - 8 - client.font.lineHeight - 2, false, Mocha.Overlay0.argb)
    }

    override fun overlays(graphics: GuiGraphics, mx: Int, my: Int, x0: Int, y0: Int, fw: Int, zones: MutableList<UIZone>) {
        val hw = fw / 2 - 4
        if (matchOpen) matchType(graphics, mx, my, x0 + padding + hw + 8, matchY + fh, hw)
        if (catOpen) category(graphics, mx, my, x0 + padding + hw + 8, catY + fh, hw)
    }

    private fun dropdown0(graphics: GuiGraphics, mx: Int, my: Int, x: Int, y: Int, w: Int, zones: MutableList<UIZone>) {
        val hov = (!dropdown || matchOpen) && mx in x until x + w && my in y until y + fh
        graphics.rectangle(x, y, w, fh, if (hov) Mocha.Surface2.argb else Mocha.Surface1.argb)
        graphics.outline(x, y, w, fh, 1, if (matchOpen) Mocha.Mauve.argb else Mocha.Overlay0.argb)
        graphics.extractText(match.displayName, x + 4, y + (fh - client.font.lineHeight) / 2 + 1, false, Mocha.Text.argb)
        graphics.extractText(if (matchOpen) "▾" else "▸", x + w - client.font.width(if (matchOpen) "▾" else "▸") - 4, y + (fh - client.font.lineHeight) / 2 + 1, false, Mocha.Overlay0.argb)
        zones.add(UIZone(x, y, w, fh, UIZoneType.MODAL_MATCH_TYPE))
    }

    private fun matchType(graphics: GuiGraphics, mx: Int, my: Int, x: Int, y: Int, w: Int) {
        val entries = MatchType.entries
        val menuH = entries.size * 14
        graphics.rectangle(x, y, w, menuH, Mocha.Base.argb)
        graphics.outline(x, y, w, menuH, 1, Mocha.Mauve.argb)

        var cy = y
        for (e in entries) {
            if (mx in x until x + w && my in cy until cy + 14) graphics.rectangle(x, cy, w, 14, Mocha.Surface1.argb)

            val sel = match == e
            graphics.extractText(e.displayName, x + 4, cy + (14 - client.font.lineHeight) / 2 + 1, false, if (sel) Mocha.Mauve.argb else Mocha.Text.argb)
            if (sel) graphics.extractText("✔", x + w - 12, cy + (14 - client.font.lineHeight) / 2 + 1, false, Mocha.Mauve.argb)
            cy += 14
        }
    }

    private fun dropdown1(graphics: GuiGraphics, mx: Int, my: Int, x: Int, y: Int, w: Int, zones: MutableList<UIZone>) {
        val hov = (!dropdown || catOpen) && mx in x until x + w && my in y until y + fh

        graphics.rectangle(x, y, w, fh, if (hov) Mocha.Surface2.argb else Mocha.Surface1.argb)
        graphics.outline(x, y, w, fh, 1, if (catOpen) Mocha.Mauve.argb else Mocha.Overlay0.argb)
        graphics.enableScissor(x + 2, y, x + w - 14, y + fh)
        graphics.extractText(category.ifEmpty { "Uncategorized" }, x + 4, y + (fh - client.font.lineHeight) / 2 + 1, false, Mocha.Text.argb)
        graphics.disableScissor()
        graphics.extractText(if (catOpen) "▾" else "▸", x + w - client.font.width(if (catOpen) "▾" else "▸") - 4, y + (fh - client.font.lineHeight) / 2 + 1, false, Mocha.Overlay0.argb)
        zones.add(UIZone(x, y, w, fh, UIZoneType.MODAL_CATEGORY))
    }

    private fun category(graphics: GuiGraphics, mx: Int, my: Int, x: Int, y: Int, w: Int) {
        val cats = MessageActions.categories
        val menuH = ((cats.size + 1) * 14).coerceAtMost(80)
        graphics.rectangle(x, y, w, menuH, Mocha.Base.argb)
        graphics.outline(x, y, w, menuH, 1, Mocha.Mauve.argb)
        graphics.enableScissor(x, y, x + w, y + menuH)

        var cy = y
        if (mx in x until x + w && my in cy until cy + 14) graphics.rectangle(x, cy, w, 14, Mocha.Surface1.argb)
        graphics.extractText("Uncategorized", x + 4, cy + (14 - client.font.lineHeight) / 2 + 1, false, if (category.isEmpty()) Mocha.Mauve.argb else Mocha.Text.argb)
        if (category.isEmpty()) graphics.extractText("✔", x + w - 12, cy + (14 - client.font.lineHeight) / 2 + 1, false, Mocha.Mauve.argb)
        cy += 14

        for (cat in cats) {
            if (cy + 14 > y && cy < y + menuH) {
                if (mx in x until x + w && my in cy until cy + 14) graphics.rectangle(x, cy, w, 14, Mocha.Surface1.argb)
                val sel = category == cat.name
                graphics.extractText(cat.name, x + 4, cy + (14 - client.font.lineHeight) / 2 + 1, false, if (sel) Mocha.Mauve.argb else Mocha.Text.argb)
                if (sel) graphics.extractText("✔", x + w - 12, cy + (14 - client.font.lineHeight) / 2 + 1, false, Mocha.Mauve.argb)
            }

            cy += 14
        }

        graphics.disableScissor()
    }

    fun open() {
        entry = null
        reset()
        open = true
    }

    fun open(a: ActionEntryView) {
        entry = a
        matchOpen = false
        catOpen = false
        open = true

        patternField.reset(true)
        patternField.value = a.entry.pattern
        patternField.cursor = patternField.value.length
        patternField.focused = true
        match = a.entry.match
        action = a.entry.id

        valueField.reset(true)
        valueField.value = a.entry.value
        valueField.cursor = valueField.value.length
        cancel = a.entry.cancel
        category = a.entry.category

        delayField.reset(true)
        delayField.value = if (a.entry.delay > 0.0) a.entry.delay.toString() else ""
        delayField.cursor = delayField.value.length
    }

    override fun onClose() {
        patternField.focused = false
        valueField.focused = false
        delayField.focused = false
    }

    fun click0(mouseX: Int, mouseY: Int) {
        val sw = client.window.guiScaledWidth
        val mx0 = (sw - mw) / 2
        val fw = mw - padding * 2
        val hw = fw / 2 - 4
        val x0 = mx0 + padding + hw + 8

        if (mouseX !in x0 until x0 + hw) {
            matchOpen = false
            return
        }

        val y0 = matchY + fh
        val e = MatchType.entries
        val h = e.size * 14

        if (mouseY in y0 until y0 + h) {
            var iy = y0
            for (e in e) {
                if (mouseY !in iy until iy + 14) {
                    iy += 14
                    continue
                }

                match = e
                matchOpen = false
                return
            }
        }

        matchOpen = false
    }

    fun click1(mouseX: Int, mouseY: Int) {
        val sw = client.window.guiScaledWidth
        val mx0 = (sw - mw) / 2
        val fw = mw - padding * 2
        val halfW = fw / 2 - 4
        val menuX = mx0 + padding + halfW + 8

        if (mouseX !in menuX until menuX + halfW) {
            catOpen = false
            return
        }

        val y0 = catY + fh
        val e = MessageActions.categories
        val h = ((e.size + 1) * 14).coerceAtMost(80)

        if (mouseY in y0 until y0 + h) {
            var iy = y0
            if (mouseY in iy until iy + 14) {
                category = ""
                catOpen = false
                return
            }

            iy += 14
            for (cat in e) {
                if (mouseY !in iy until iy + 14) {
                    iy += 14
                    continue
                }

                category = cat.name
                catOpen = false
                return
            }
        }

        catOpen = false
    }

    private fun reset() {
        patternField.reset(true)
        patternField.focused = true
        valueField.reset(true)
        match = MatchType.CONTAINS
        action = 0
        cancel = false
        category = ""
        delayField.reset(true)
        matchOpen = false
        catOpen = false
    }
}