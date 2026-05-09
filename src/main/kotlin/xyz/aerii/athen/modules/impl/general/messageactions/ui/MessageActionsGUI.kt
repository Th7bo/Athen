package xyz.aerii.athen.modules.impl.general.messageactions.ui

import net.minecraft.client.gui.GuiGraphics
import org.lwjgl.glfw.GLFW
import xyz.aerii.athen.handlers.Scram
import xyz.aerii.athen.modules.impl.general.messageactions.data.ActionEntry
import xyz.aerii.athen.modules.impl.general.messageactions.MessageActions
import xyz.aerii.athen.modules.impl.general.messageactions.ui.actions.ActionCategoryBar
import xyz.aerii.athen.modules.impl.general.messageactions.ui.actions.ActionEntryView
import xyz.aerii.athen.modules.impl.general.messageactions.ui.actions.ActionModalRenderer
import xyz.aerii.athen.modules.impl.general.messageactions.ui.actions.ActionsListRenderer
import xyz.aerii.athen.ui.UIZone
import xyz.aerii.athen.ui.themes.Catppuccin
import xyz.aerii.athen.utils.render.Render2D.drawOutline
import xyz.aerii.athen.utils.render.Render2D.drawRectangle
import xyz.aerii.athen.utils.render.Render2D.text
import xyz.aerii.library.api.client

object MessageActionsGUI : Scram("Message actions [Athen]") {
    private val entries = mutableListOf<ActionEntryView>()
    private val zones = mutableListOf<UIZone>()
    private val categoryBar = ActionCategoryBar(110, 20)
    private val listRenderer = ActionsListRenderer(28, 4, 16, 6)
    private val modal = ActionModalRenderer(380, 260, 16, 6)

    override fun onScramInit() {
        recreate()
        modal.close()
        categoryBar.create1()
    }

    override fun onScramClose() {
        MessageActions.disk()
    }

    override fun isPauseScreen(): Boolean {
        return false
    }

    override fun onScramRender(graphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        zones.clear()
        for (e in entries) e.toggle += ((if (e.entry.enabled) 1f else 0f) - e.toggle) * delta * 0.4f

        graphics.drawRectangle(0, 0, width, height, Catppuccin.Mocha.Crust.withAlpha(0.6f))

        val px = (width - 576) / 2
        val py = (height - 300) / 2

        categoryBar.draw(graphics, mouseX, mouseY, px, py, 300, modal.open, zones)

        val x = px + 116
        graphics.drawRectangle(x, py, 460, 300, Catppuccin.Mocha.Base.argb)
        graphics.drawOutline(x, py, 460, 300, 1, Catppuccin.Mocha.Surface0.argb)

        val list = categoryBar.selected?.let { s -> entries.filter { it.entry.category == s } } ?: entries
        listRenderer.draw(graphics, mouseX, mouseY, x + 6, py + 6, 448, 260, list, modal.open, zones)
        footer(graphics, mouseX, mouseY, x, py)

        if (!modal.open) categoryBar.tooltip(graphics)
        if (modal.open) modal.draw(graphics, mouseX, mouseY, width, height, zones)
    }

    private fun footer(graphics: GuiGraphics, mx: Int, my: Int, mainX: Int, py: Int) {
        val y0 = py + 272
        graphics.drawRectangle(mainX, y0, 460, 1, Catppuccin.Mocha.Surface0.argb)

        val x0 = mainX + 170
        val y1 = y0 + 6
        graphics.drawRectangle(x0, y1, 120, 16, if (!modal.open && mx in x0 until x0 + 120 && my in y1 until y1 + 16) Catppuccin.Mocha.Surface2.argb else Catppuccin.Mocha.Surface1.argb)
        graphics.drawOutline(x0, y1, 120, 16, 1, Catppuccin.Mocha.Green.argb)
        graphics.text("+ Create Action", x0 + (120 - client.font.width("+ Create Action")) / 2, y1 + (16 - client.font.lineHeight) / 2 + 1, false, Catppuccin.Mocha.Green.argb)
        zones.add(UIZone(x0, y1, 120, 16, UIZoneType.BUTTON_CREATE))
    }

    override fun onScramMouseClick(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (modal.open) {
            click(mouseX, mouseY, button)
            return true
        }

        if (categoryBar.creating) {
            val z = zones.firstOrNull { it.type == UIZoneType.CATEGORY_ADD }
            if (z != null && mouseX in z.x until z.x + z.w && mouseY in z.y until z.y + z.h) {
                if (button == 0) {
                    categoryBar.nameField.focused = true
                    categoryBar.nameField.updateClick(mouseX, z.x)
                }

                return true
            }

            categoryBar.create0()
            recreate()
            return true
        }

        if (button == 1) {
            val z = zones.lastOrNull { it.type == UIZoneType.CATEGORY_TAB && it.category.isNotEmpty() && mouseX in it.x until it.x + it.w && mouseY in it.y until it.y + it.h }
            categoryBar.deleting = if (z == null || categoryBar.deleting == z.category) null else z.category
            return true
        }

        if (button != 0) {
            return false
        }

        val hit = zones.lastOrNull { mouseX in it.x until it.x + it.w && mouseY in it.y until it.y + it.h } ?: return false

        if (hit.type == UIZoneType.BUTTON_CREATE) {
            modal.open()
            return true
        }

        if (hit.type == UIZoneType.ENTRY_EDIT) {
            entries.firstOrNull { it.index == hit.data }?.let { modal.open(it) }
            return true
        }

        if (hit.type == UIZoneType.ENTRY_DELETE) {
            entries.firstOrNull { it.index == hit.data }?.let { if (MessageActions.remove(it.index)) recreate() }
            return true
        }

        if (hit.type == UIZoneType.ENTRY_TOGGLE) {
            entries.firstOrNull { it.index == hit.data }?.let {
                MessageActions.update(it.index, it.entry.copy(enabled = !it.entry.enabled))
                recreate()
            }
            return true
        }

        if (hit.type == UIZoneType.CATEGORY_TAB) {
            if (categoryBar.deleting != null && hit.category == categoryBar.deleting) {
                MessageActions.remove(hit.category)
                if (categoryBar.selected == hit.category) categoryBar.selected = null
                categoryBar.deleting = null
                recreate()
                return true
            }

            categoryBar.deleting = null
            categoryBar.selected = hit.category.ifEmpty { null }
            return true
        }

        if (hit.type == UIZoneType.CATEGORY_TOGGLE) {
            categoryBar.deleting = null
            MessageActions.categories.getOrNull(hit.data)?.let {
                MessageActions.toggle(it.name)
                recreate()
            }
            return true
        }

        if (hit.type == UIZoneType.CATEGORY_ADD) {
            categoryBar.create()
            return true
        }

        return true
    }

    private fun click(mouseX: Int, mouseY: Int, button: Int) {
        if (modal.matchOpen) return modal.click0(mouseX, mouseY)
        if (modal.catOpen) return modal.click1(mouseX, mouseY)
        if (button != 0) return

        val hit = zones.lastOrNull { mouseX in it.x until it.x + it.w && mouseY in it.y until it.y + it.h }
        val p0 = modal.patternField.focused
        val p1 = modal.valueField.focused
        val p2 = modal.delayField.focused
        modal.patternField.focused = false
        modal.valueField.focused = false
        modal.delayField.focused = false

        if (hit == null) return

        if (hit.type == UIZoneType.MODAL_PATTERN) {
            modal.patternField.focused = true
            if (p0) modal.patternField.updateClick(mouseX, hit.x)
            return
        }

        if (hit.type == UIZoneType.MODAL_ACTION_VALUE) {
            modal.valueField.focused = true
            if (p1) modal.valueField.updateClick(mouseX, hit.x)
            return
        }

        if (hit.type == UIZoneType.MODAL_MATCH_TYPE) {
            modal.matchOpen = !modal.matchOpen
            return
        }

        if (hit.type == UIZoneType.MODAL_ACTION_TYPE) {
            modal.action = hit.data
            return
        }

        if (hit.type == UIZoneType.MODAL_CATEGORY) {
            modal.catOpen = !modal.catOpen
            return
        }

        if (hit.type == UIZoneType.MODAL_CANCEL_TOGGLE) {
            modal.cancel = !modal.cancel
            return
        }

        if (hit.type == UIZoneType.MODAL_DELAY) {
            modal.delayField.focused = true
            if (p2) modal.delayField.updateClick(mouseX, hit.x)
            return
        }

        if (hit.type == UIZoneType.MODAL_SAVE) {
            modal()
            return
        }

        if (hit.type == UIZoneType.MODAL_CANCEL) {
            modal.close()
            return
        }
    }

    override fun onScramMouseScroll(mouseX: Int, mouseY: Int, horizontal: Double, vertical: Double): Boolean {
        val amount = (vertical * 10).toInt()
        if (modal.open) return true

        if (mouseX < (width - 576) / 2 + 110) {
            categoryBar.scroll(amount, 300)
            return true
        }

        if (entries.isEmpty()) return false
        listRenderer.scroll(amount)
        return true
    }

    override fun onScramKeyPress(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (modal.open) {
            when {
                modal.patternField.focused && modal.patternField.handleKey(keyCode, modifiers) -> return true
                modal.valueField.focused && modal.valueField.handleKey(keyCode, modifiers) -> return true
                modal.delayField.focused && modal.delayField.handleKey(keyCode, modifiers) -> return true
            }

            when (keyCode) {
                GLFW.GLFW_KEY_TAB -> {
                    when {
                        modal.patternField.focused -> {
                            modal.patternField.focused = false
                            modal.valueField.focused = true
                        }

                        modal.valueField.focused -> {
                            modal.valueField.focused = false
                            modal.patternField.focused = true
                        }
                    }
                }

                GLFW.GLFW_KEY_ESCAPE -> {
                    modal.close()
                }
            }

            return true
        }

        if (categoryBar.creating) {
            when (keyCode) {
                GLFW.GLFW_KEY_ENTER -> {
                    categoryBar.create0()
                    recreate()
                }

                GLFW.GLFW_KEY_ESCAPE -> {
                    categoryBar.create1()
                }

                else -> {
                    if (categoryBar.nameField.focused) categoryBar.nameField.handleKey(keyCode, modifiers)
                }
            }

            return true
        }

        return super.onScramKeyPress(keyCode, scanCode, modifiers)
    }

    override fun onScramCharType(char: Char): Boolean {
        if (modal.open) {
            when {
                modal.patternField.focused -> modal.patternField.handleChar(char)
                modal.valueField.focused -> modal.valueField.handleChar(char)
                modal.delayField.focused && (char.isDigit() || char == '.') -> modal.delayField.handleChar(char)
            }

            return true
        }

        if (categoryBar.creating && categoryBar.nameField.focused) {
            categoryBar.nameField.handleChar(char)
            return true
        }

        return super.onScramCharType(char)
    }

    private fun modal() {
        if (modal.patternField.value.isEmpty()) return
        val delay = modal.delayField.value.toDoubleOrNull() ?: 0.0

        val entry = ActionEntry(modal.patternField.value, modal.match, modal.action, modal.valueField.value, modal.entry?.entry?.enabled ?: true, modal.category, modal.cancel, delay)
        if (modal.entry == null) MessageActions.add(entry.pattern, entry.match, entry.id, entry.value, entry.category, entry.cancel, entry.delay)
        else MessageActions.update(modal.entry!!.index, entry)

        recreate()
        modal.close()
    }

    private fun recreate() {
        entries.clear()
        for (i in MessageActions.actions.indices) entries.add(ActionEntryView(i, MessageActions.actions[i]))
    }
}