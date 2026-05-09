package xyz.aerii.athen.modules.impl.general.messageactions.ui.actions

import net.minecraft.client.gui.GuiGraphics
import xyz.aerii.athen.modules.impl.general.messageactions.MessageActions
import xyz.aerii.athen.modules.impl.general.messageactions.ui.UIZoneType
import xyz.aerii.athen.ui.InputField
import xyz.aerii.athen.ui.UIZone
import xyz.aerii.athen.ui.themes.Catppuccin.Mocha
import xyz.aerii.athen.utils.render.Render2D.drawOutline
import xyz.aerii.athen.utils.render.Render2D.drawRectangle
import xyz.aerii.athen.utils.render.Render2D.text
import xyz.aerii.library.api.client

class ActionCategoryBar(
    private val sidebarW: Int,
    private val rowH: Int
) {
    val nameField = InputField("Name")

    var selected: String? = null
    var creating = false
    var deleting: String? = null
    var scroll = 0

    var tooltipText: String? = null
    var tooltipColor = 0
    var tooltipX = 0
    var tooltipY = 0

    fun draw(graphics: GuiGraphics, mx: Int, my: Int, sx: Int, sy: Int, sh: Int, modalOpen: Boolean, zones: MutableList<UIZone>) {
        graphics.drawRectangle(sx, sy, sidebarW, sh, Mocha.Base.argb)
        graphics.drawOutline(sx, sy, sidebarW, sh, 1, Mocha.Surface0.argb)

        val cats = MessageActions.categories
        val lx = sx + 4
        val lw = sidebarW - 8
        scroll = scroll.coerceIn(-maxOf(0, (cats.size + 1) * rowH + 18 - (sh - 8)), 0)

        graphics.enableScissor(sx + 1, sy + 1, sx + sidebarW - 1, sy + sh - 1)

        var cy = sy + 4 + scroll
        tooltipText = null

        if (selected == null) graphics.drawRectangle(lx, cy, lw, rowH, Mocha.Surface0.argb)
        else if (!modalOpen && mx in lx until lx + lw && my in cy until cy + rowH) graphics.drawRectangle(lx, cy, lw, rowH, Mocha.Surface0.withAlpha(0.5f))
        graphics.text("All", lx + 4, cy + (rowH - client.font.lineHeight) / 2 + 1, false, if (selected == null) Mocha.Mauve.argb else Mocha.Subtext0.argb)
        zones.add(UIZone(lx, cy, lw, rowH, UIZoneType.CATEGORY_TAB, category = ""))
        cy += rowH

        for ((i, cat) in cats.withIndex()) {
            val b = !modalOpen && mx in lx until lx + lw && my in cy until cy + rowH

            if (deleting == cat.name) {
                graphics.drawRectangle(lx + 1, cy + 1, lw - 2, rowH - 2, Mocha.Red.withAlpha(0.15f))
                graphics.drawOutline(lx + 1, cy + 1, lw - 2, rowH - 2, 1, Mocha.Red.argb)
                graphics.enableScissor(lx + 4, cy, lx + lw - 18, cy + rowH)
                graphics.text(cat.name, lx + 4, cy + (rowH - client.font.lineHeight) / 2 + 1, false, Mocha.Red.argb)
                graphics.disableScissor()

                if (b) {
                    tooltipText = "Left click to confirm"
                    tooltipColor = Mocha.Red.argb
                    tooltipX = lx + lw + 4
                    tooltipY = cy
                }
            } else {
                if (selected == cat.name) graphics.drawRectangle(lx, cy, lw, rowH, Mocha.Surface0.argb)
                else if (b) graphics.drawRectangle(lx, cy, lw, rowH, Mocha.Surface0.withAlpha(0.5f))

                if (b) {
                    tooltipText = "Right click to delete"
                    tooltipColor = Mocha.Subtext0.argb
                    tooltipX = lx + lw + 4
                    tooltipY = cy
                }

                graphics.enableScissor(lx + 4, cy, lx + lw - 18, cy + rowH)
                graphics.text(cat.name, lx + 4, cy + (rowH - client.font.lineHeight) / 2 + 1, false, when {
                    !cat.enabled -> Mocha.Overlay0.argb
                    selected == cat.name -> Mocha.Mauve.argb
                    else -> Mocha.Subtext0.argb
                })
                graphics.disableScissor()
            }

            val tx = lx + lw - 14
            val ty = cy + (rowH - 10) / 2
            val th = !modalOpen && mx in tx until tx + 10 && my in ty until ty + 10
            graphics.drawRectangle(tx, ty, 10, 10, if (th) Mocha.Surface2.argb else Mocha.Mantle.argb)
            graphics.drawOutline(tx, ty, 10, 10, 1, if (cat.enabled) Mocha.Green.argb else Mocha.Overlay0.argb)
            if (cat.enabled) graphics.drawRectangle(tx + 2, ty + 2, 6, 6, Mocha.Green.argb)

            zones.add(UIZone(tx, ty, 10, 10, UIZoneType.CATEGORY_TOGGLE, i))
            zones.add(UIZone(lx, cy, lw - 18, rowH, UIZoneType.CATEGORY_TAB, i, category = cat.name))
            cy += rowH
        }

        if (creating) {
            nameField.draw(graphics, mx, my, lx, cy + 2, lw) { zx, zy, zw, zh -> zones.add(UIZone(zx, zy, zw, zh, UIZoneType.CATEGORY_ADD)) }
            graphics.disableScissor()
            return
        }

        if (!modalOpen && mx in lx until lx + lw && my in cy + 2 until cy + 16) graphics.drawRectangle(lx, cy + 2, lw, 14, Mocha.Surface0.withAlpha(0.5f))
        graphics.text("+", lx + (lw - client.font.width("+")) / 2, cy + 2 + (14 - client.font.lineHeight) / 2 + 1, false, Mocha.Overlay0.argb)
        zones.add(UIZone(lx, cy + 2, lw, 14, UIZoneType.CATEGORY_ADD))
        graphics.disableScissor()
    }

    fun tooltip(graphics: GuiGraphics) {
        val str = tooltipText ?: return
        val tw = client.font.width(str)
        graphics.drawRectangle(tooltipX, tooltipY, tw + 8, client.font.lineHeight + 6, Mocha.Base.argb)
        graphics.drawOutline(tooltipX, tooltipY, tw + 8, client.font.lineHeight + 6, 1, Mocha.Overlay0.argb)
        graphics.text(str, tooltipX + 4, tooltipY + 3, false, tooltipColor)
    }

    fun scroll(amount: Int, sh: Int) {
        val f = (MessageActions.categories.size + 1) * rowH + 18
        scroll = (scroll + amount).coerceIn(-maxOf(0, f - (sh - 8)), 0)
    }

    fun create0() {
        val str = nameField.value.trim()
        if (str.isNotEmpty()) MessageActions.add(str)
        nameField.reset()
        creating = false
    }

    fun create1() {
        nameField.reset()
        creating = false
    }

    fun create() {
        creating = true
        deleting = null
        nameField.reset()
        nameField.focused = true
    }
}