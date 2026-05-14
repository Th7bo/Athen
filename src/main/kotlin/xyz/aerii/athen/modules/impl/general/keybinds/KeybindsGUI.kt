package xyz.aerii.athen.modules.impl.general.keybinds

import net.minecraft.client.gui.GuiGraphics
import org.lwjgl.glfw.GLFW
import xyz.aerii.athen.api.rendering.ui.effects.outline.outline
import xyz.aerii.athen.api.rendering.ui.shapes.rectangle.rectangle
import xyz.aerii.athen.api.rendering.ui.text.vanilla.extensions.extractText
import xyz.aerii.athen.handlers.Scram
import xyz.aerii.athen.modules.impl.general.keybinds.Keybinds.add
import xyz.aerii.athen.modules.impl.general.keybinds.Keybinds.remove
import xyz.aerii.athen.modules.impl.general.keybinds.Keybinds.update
import xyz.aerii.athen.modules.impl.general.keybinds.ui.*
import xyz.aerii.athen.ui.UIZone
import xyz.aerii.athen.ui.themes.Catppuccin.Mocha
import xyz.aerii.library.api.client
import xyz.aerii.library.utils.hovered

object KeybindsGUI : Scram("Keybinds Manager [Athen]") {
    private val entries = mutableListOf<BindingEntry>()
    private val zones = mutableListOf<UIZone>()
    private val categoryBar = CategoryBar(110, 20)
    private val listRenderer = BindingsListRenderer(28, 4, 16, 6)
    private val modal = ModalRenderer(380, 260, 16, 6)

    override fun onScramInit() {
        recreate()
        modal.close()
        categoryBar.create1()
    }

    override fun onScramClose() {
        Keybinds.storage.save()
    }

    override fun isPauseScreen(): Boolean {
        return false
    }

    override fun onScramRender(graphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        zones.clear()
        for (e in entries) e.toggleAnim += ((if (e.binding.enabled) 1f else 0f) - e.toggleAnim) * delta * 0.4f

        graphics.rectangle(0, 0, width, height, Mocha.Crust.withAlpha(0.6f))

        val px = (width - 576) / 2
        val py = (height - 300) / 2

        categoryBar.draw(graphics, px, py, 300, modal.open, zones)

        val mainX = px + 116
        graphics.rectangle(mainX, py, 460, 300, Mocha.Base.argb)
        graphics.outline(mainX, py, 460, 300, 1, Mocha.Surface0.argb)

        val list = categoryBar.selected?.let { s -> entries.filter { it.binding.category == s } } ?: entries
        listRenderer.draw(graphics, mouseX, mouseY, mainX + 6, py + 6, 448, 260, list, modal.open, zones)
        drawFooter(graphics, mainX, py)

        if (!modal.open) categoryBar.tooltip(graphics)
        if (modal.open) modal.draw(graphics, mouseX, mouseY, width, height, zones)
    }

    private fun drawFooter(graphics: GuiGraphics, mainX: Int, py: Int) {
        val fy = py + 272
        graphics.rectangle(mainX, fy, 460, 1, Mocha.Surface0.argb)

        val x = mainX + 170
        val y1 = fy + 6
        graphics.rectangle(x, y1, 120, 16, if (!modal.open && hovered(x, y1, 120, 16, true)) Mocha.Surface2.argb else Mocha.Surface1.argb)
        graphics.outline(x, y1, 120, 16, 1, Mocha.Green.argb)
        graphics.extractText("+ Create Keybind", x + (120 - client.font.width("+ Create Keybind")) / 2, y1 + (16 - client.font.lineHeight) / 2 + 1, false, Mocha.Green.argb)
        zones.add(UIZone(x, y1, 120, 16, UIZoneType.BUTTON_CREATE))
    }

    override fun onScramMouseClick(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (modal.open) {
            clickModal(mouseX, button)
            return true
        }

        if (categoryBar.creating) {
            val z = zones.firstOrNull { it.type == UIZoneType.CATEGORY_ADD }
            if (z != null && hovered(z.x, z.y, z.w, z.h, true)) {
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
            val z = zones.lastOrNull { it.type == UIZoneType.CATEGORY_TAB && it.category.isNotEmpty() && hovered(it.x, it.y, it.w, it.h, true) }
            categoryBar.deleting = if (z == null || categoryBar.deleting == z.category) null else z.category
            return true
        }

        if (button != 0) return false

        val hit = zones.lastOrNull { hovered(it.x, it.y, it.w, it.h, true) } ?: return false
        if (hit.type == UIZoneType.BUTTON_CREATE) {
            modal.open()
            return true
        }

        if (hit.type == UIZoneType.ENTRY_EDIT) {
            entries.firstOrNull { it.index == hit.data }?.let { modal.open(it) }
            return true
        }

        if (hit.type == UIZoneType.ENTRY_DELETE) {
            entries.firstOrNull { it.index == hit.data }?.let { if (it.index.remove()) recreate() }
            return true
        }

        if (hit.type == UIZoneType.ENTRY_TOGGLE) {
            entries.firstOrNull { it.index == hit.data }?.let {
                it.index.update(it.binding.keys, it.binding.command, !it.binding.enabled, it.binding.category, it.condition)
                recreate()
            }

            return true
        }

        if (hit.type == UIZoneType.CATEGORY_TAB) {
            if (categoryBar.deleting != null && hit.category == categoryBar.deleting) {
                Keybinds.removeCategory(hit.category)
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
            Keybinds.categories.value.getOrNull(hit.data)?.let {
                Keybinds.toggleCategory(it.name)
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

    private fun clickModal(mouseX: Int, button: Int) {
        if (modal.keysListening) {
            val z = zones.firstOrNull { it.type == UIZoneType.MODAL_KEYS }
            if (z != null && hovered(z.x, z.y, z.w, z.h, true)) {
                modal.recorded.add(-(button + 1))
                return
            }

            if (modal.recorded.isNotEmpty()) {
                modal.keysListening = false
                modal.keysBuf = modal.recorded.toMutableList()
            }

            return
        }

        if (modal.categoryOpen) return modal.clickCategory(mouseX)
        if (modal.workInOpen) return modal.clickWorkIn(mouseX)
        if (modal.islandOpen) return modal.clickIsland(mouseX)
        if (modal.floorOpen) return modal.clickFloor(mouseX)
        if (modal.classOpen) return modal.clickClass(mouseX)
        if (modal.f7PhaseOpen) return modal.clickF7Phase(mouseX)
        if (button != 0) return

        val hit = zones.lastOrNull { hovered(it.x, it.y, it.w, it.h, true) }
        val pre = modal.cmdField.focused
        modal.cmdField.focused = false

        if (hit == null) return

        if (hit.type == UIZoneType.MODAL_CMD) {
            modal.cmdField.focused = true
            if (pre) modal.cmdField.updateClick(mouseX, hit.x)
            return
        }

        if (hit.type == UIZoneType.MODAL_KEYS) {
            modal.keysListening = true
            modal.recorded.clear()
            return
        }

        if (hit.type == UIZoneType.MODAL_CATEGORY) {
            modal.categoryOpen = !modal.categoryOpen
            return
        }

        if (hit.type == UIZoneType.MODAL_WORK_IN) {
            modal.workInOpen = !modal.workInOpen
            return
        }

        if (hit.type == UIZoneType.MODAL_ISLAND) {
            modal.islandOpen = !modal.islandOpen
            return
        }

        if (hit.type == UIZoneType.MODAL_FLOOR) {
            modal.floorOpen = !modal.floorOpen
            return
        }

        if (hit.type == UIZoneType.MODAL_CLASS) {
            modal.classOpen = !modal.classOpen
            return
        }

        if (hit.type == UIZoneType.MODAL_F7_PHASE) {
            modal.f7PhaseOpen = !modal.f7PhaseOpen
            return
        }

        if (hit.type == UIZoneType.MODAL_SAVE) return saveModal()

        if (hit.type == UIZoneType.MODAL_CANCEL) return modal.close()
    }

    override fun onScramMouseScroll(mouseX: Int, mouseY: Int, horizontal: Double, vertical: Double): Boolean {
        val amount = (vertical * 10).toInt()

        if (modal.open) {
            modal.scroll(amount)
            return true
        }

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
            if (modal.keysListening) return when (keyCode) {
                GLFW.GLFW_KEY_ENTER -> {
                    if (modal.recorded.isEmpty()) return true
                    modal.keysListening = false
                    modal.keysBuf = modal.recorded.toMutableList()
                    true
                }

                GLFW.GLFW_KEY_ESCAPE -> {
                    modal.keysListening = false
                    modal.recorded.clear()
                    true
                }

                else -> {
                    if (keyCode <= 0) return false
                    modal.recorded.add(keyCode)
                    true
                }
            }

            if (modal.cmdField.focused && modal.cmdField.handleKey(keyCode, modifiers)) return true
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) modal.close()
            return true
        }

        if (categoryBar.creating) {
            if (keyCode == GLFW.GLFW_KEY_ENTER) {
                categoryBar.create0()
                recreate()
                return true
            }

            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                categoryBar.create1()
                return true
            }

            if (categoryBar.nameField.focused) categoryBar.nameField.handleKey(keyCode, modifiers)
            return true
        }

        return super.onScramKeyPress(keyCode, scanCode, modifiers)
    }

    override fun onScramCharType(char: Char): Boolean {
        if (modal.open) {
            if (modal.cmdField.focused && modal.cmdField.handleChar(char)) return true
            return true
        }

        if (categoryBar.creating && categoryBar.nameField.focused) {
            if (categoryBar.nameField.handleChar(char)) return true
            return true
        }

        return super.onScramCharType(char)
    }

    private fun saveModal() {
        if (modal.cmdField.value.isEmpty() || modal.keysBuf.isEmpty()) return

        if (modal.entry == null) {
            modal.keysBuf.add(modal.cmdField.value, modal.category, modal.condition)
            recreate()
            modal.close()
            return
        }

        val e = modal.entry!!
        e.index.update(modal.keysBuf, modal.cmdField.value, e.binding.enabled, modal.category, modal.condition)

        recreate()
        modal.close()
    }

    private fun recreate() {
        entries.clear()
        for ((i, b) in Keybinds.bindings.value.withIndex()) entries.add(BindingEntry(i, b))
    }
}