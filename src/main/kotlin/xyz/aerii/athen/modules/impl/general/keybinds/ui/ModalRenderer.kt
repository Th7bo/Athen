package xyz.aerii.athen.modules.impl.general.keybinds.ui

import net.minecraft.client.gui.GuiGraphics
import tech.thatgravyboat.skyblockapi.api.area.dungeon.DungeonFloor
import xyz.aerii.athen.api.dungeon.enums.DungeonClass
import xyz.aerii.athen.api.location.SkyBlockIsland
import xyz.aerii.athen.api.rendering.ui.effects.outline.outline
import xyz.aerii.athen.api.rendering.ui.shapes.rectangle.rectangle
import xyz.aerii.athen.api.rendering.ui.text.vanilla.extensions.extractText
import xyz.aerii.athen.modules.impl.general.keybinds.KeybindCondition
import xyz.aerii.athen.modules.impl.general.keybinds.KeybindWorkIn
import xyz.aerii.athen.modules.impl.general.keybinds.Keybinds
import xyz.aerii.athen.modules.impl.general.keybinds.ui.BindingsListRenderer.Companion.str
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
) : AbstractModalRenderer<BindingEntry>(mw, mh, fh, padding) {
    val allPhases = listOf(1, 2, 3, 4, 5)

    override val create = "Create Keybind"
    override val edit = "Edit Keybind"
    override val zone0: IZoneType = UIZoneType.MODAL_SAVE
    override val zone1: IZoneType = UIZoneType.MODAL_CANCEL

    val cmdField = InputField("Command or message")
    var keysBuf = mutableListOf<Int>()
    var keysListening = false
    val recorded = mutableSetOf<Int>()

    var category = ""
    var categoryOpen = false
    var categoryDropdownY = 0

    var condition = KeybindCondition()

    var workInOpen = false
    var workInDropdownY = 0

    var islandOpen = false
    var islandDropdownY = 0
    var islandScroll = 0

    var floorOpen = false
    var floorDropdownY = 0
    var floorScroll = 0

    var classOpen = false
    var classDropdownY = 0
    var classScroll = 0

    var f7PhaseOpen = false
    var f7PhaseDropdownY = 0
    var f7PhaseScroll = 0

    override val dropdown: Boolean
        get() = categoryOpen || workInOpen || islandOpen || floorOpen || classOpen || f7PhaseOpen

    override fun fields(graphics: GuiGraphics, mx: Int, my: Int, x0: Int, y0: Int, cy: Int, fw: Int, zones: MutableList<UIZone>) {
        var cy = cy
        val halfW = fw / 2 - 4

        graphics.extractText("Command", x0 + padding, cy, false, Mocha.Subtext0.argb)
        graphics.extractText("Keys", x0 + padding + halfW + 8, cy, false, Mocha.Subtext0.argb)
        cy += client.font.lineHeight + 2

        cmdField.draw(graphics, mx, my, x0 + padding, cy, halfW) { zx, zy, zw, zh -> zones.add(UIZone(zx, zy, zw, zh, UIZoneType.MODAL_CMD)) }
        drawKeysField(graphics, mx, my, x0 + padding + halfW + 8, cy, halfW, zones)
        cy += fh + 8

        graphics.extractText("Category", x0 + padding, cy, false, Mocha.Subtext0.argb)
        graphics.extractText("Work In", x0 + padding + halfW + 8, cy, false, Mocha.Subtext0.argb)
        cy += client.font.lineHeight + 2

        categoryDropdownY = cy
        workInDropdownY = cy
        drawCategoryDropdown(graphics, mx, my, x0 + padding, cy, halfW, zones)
        drawWorkInDropdown(graphics, mx, my, x0 + padding + halfW + 8, cy, halfW, zones)
        cy += fh + 8

        graphics.extractText("Islands", x0 + padding, cy, false, Mocha.Subtext0.argb)
        graphics.extractText("Dungeon Floors", x0 + padding + halfW + 8, cy, false, Mocha.Subtext0.argb)
        cy += client.font.lineHeight + 2

        islandDropdownY = cy
        floorDropdownY = cy
        drawIslandDropdown(graphics, mx, my, x0 + padding, cy, halfW, zones)
        drawFloorDropdown(graphics, mx, my, x0 + padding + halfW + 8, cy, halfW, zones)
        cy += fh + 8

        graphics.extractText("Dungeon Classes", x0 + padding, cy, false, Mocha.Subtext0.argb)
        graphics.extractText("F7 Phases", x0 + padding + halfW + 8, cy, false, Mocha.Subtext0.argb)
        cy += client.font.lineHeight + 2

        classDropdownY = cy
        f7PhaseDropdownY = cy
        drawClassDropdown(graphics, mx, my, x0 + padding, cy, halfW, zones)
        drawF7PhaseDropdown(graphics, mx, my, x0 + padding + halfW + 8, cy, halfW, zones)

    }

    override fun overlays(graphics: GuiGraphics, mx: Int, my: Int, x0: Int, y0: Int, fw: Int, zones: MutableList<UIZone>) {
        val halfW = fw / 2 - 4

        if (categoryOpen) drawCategoryMenu(graphics, mx, my, x0 + padding, categoryDropdownY + fh, halfW)
        if (workInOpen) drawWorkInMenu(graphics, mx, my, x0 + padding + halfW + 8, workInDropdownY + fh, halfW)
        if (islandOpen) drawIslandMenu(graphics, mx, my, x0 + padding, islandDropdownY + fh, halfW)
        if (floorOpen) drawFloorMenu(graphics, mx, my, x0 + padding + halfW + 8, floorDropdownY + fh, halfW)
        if (classOpen) drawClassMenu(graphics, mx, my, x0 + padding, classDropdownY + fh, halfW)
        if (f7PhaseOpen) drawF7PhaseMenu(graphics, mx, my, x0 + padding + halfW + 8, f7PhaseDropdownY + fh, halfW)

        if (keysListening) {
            val sw = x0 * 2 + mw
            val tt = "Press Enter to confirm | Escape to cancel"
            val tw = client.font.width(tt)
            val bx = (sw - tw - 12) / 2
            graphics.rectangle(bx, y0 + mh + 6, tw + 12, client.font.lineHeight + 8, Mocha.Base.argb)
            graphics.outline(bx, y0 + mh + 6, tw + 12, client.font.lineHeight + 8, 1, Mocha.Overlay0.argb)
            graphics.extractText(tt, bx + 6, y0 + mh + 10, false, Mocha.Text.argb)
        }
    }

    private fun drawCategoryDropdown(guiGraphics: GuiGraphics, mx: Int, my: Int, x: Int, y: Int, w: Int, zones: MutableList<UIZone>) {
        val hov = (!dropdown || categoryOpen) && mx in x until x + w && my in y until y + fh

        guiGraphics.rectangle(x, y, w, fh, if (hov) Mocha.Surface2.argb else Mocha.Surface1.argb)
        guiGraphics.outline(x, y, w, fh, 1, if (categoryOpen) Mocha.Mauve.argb else Mocha.Overlay0.argb)

        guiGraphics.enableScissor(x + 2, y, x + w - 14, y + fh)
        guiGraphics.extractText(category.ifEmpty { "Uncategorized" }, x + 4, y + (fh - client.font.lineHeight) / 2 + 1, false, Mocha.Text.argb)
        guiGraphics.disableScissor()

        guiGraphics.extractText(if (categoryOpen) "▾" else "▸", x + w - client.font.width(if (categoryOpen) "▾" else "▸") - 4, y + (fh - client.font.lineHeight) / 2 + 1, false, Mocha.Overlay0.argb)
        zones.add(UIZone(x, y, w, fh, UIZoneType.MODAL_CATEGORY))
    }

    private fun drawCategoryMenu(guiGraphics: GuiGraphics, mx: Int, my: Int, x: Int, y: Int, w: Int) {
        val cats = Keybinds.categories.value
        val menuH = ((cats.size + 1) * 14).coerceAtMost(80)

        guiGraphics.rectangle(x, y, w, menuH, Mocha.Base.argb)
        guiGraphics.outline(x, y, w, menuH, 1, Mocha.Mauve.argb)
        guiGraphics.enableScissor(x, y, x + w, y + menuH)

        var cy = y
        if (cy + 14 > y && cy < y + menuH) {
            if (mx in x until x + w && my in cy until cy + 14) guiGraphics.rectangle(x, cy, w, 14, Mocha.Surface1.argb)

            guiGraphics.extractText("Uncategorized", x + 4, cy + (14 - client.font.lineHeight) / 2 + 1, false, if (category.isEmpty()) Mocha.Mauve.argb else Mocha.Text.argb)
            if (category.isEmpty()) guiGraphics.extractText("✔", x + w - 12, cy + (14 - client.font.lineHeight) / 2 + 1, false, Mocha.Mauve.argb)
        }
        cy += 14

        for (cat in cats) {
            if (cy + 14 > y && cy < y + menuH) {
                if (mx in x until x + w && my in cy until cy + 14) guiGraphics.rectangle(x, cy, w, 14, Mocha.Surface1.argb)

                val sel = category == cat.name
                guiGraphics.extractText(cat.name, x + 4, cy + (14 - client.font.lineHeight) / 2 + 1, false, if (sel) Mocha.Mauve.argb else Mocha.Text.argb)
                if (sel) guiGraphics.extractText("✔", x + w - 12, cy + (14 - client.font.lineHeight) / 2 + 1, false, Mocha.Mauve.argb)
            }

            cy += 14
        }
        guiGraphics.disableScissor()
    }

    private fun drawWorkInDropdown(guiGraphics: GuiGraphics, mx: Int, my: Int, x: Int, y: Int, w: Int, zones: MutableList<UIZone>) {
        val hov = (!dropdown || workInOpen) && mx in x until x + w && my in y until y + fh
        guiGraphics.rectangle(x, y, w, fh, if (hov) Mocha.Surface2.argb else Mocha.Surface1.argb)
        guiGraphics.outline(x, y, w, fh, 1, if (workInOpen) Mocha.Mauve.argb else Mocha.Overlay0.argb)

        guiGraphics.enableScissor(x + 2, y, x + w - 14, y + fh)
        guiGraphics.extractText(condition.workIn.displayName, x + 4, y + (fh - client.font.lineHeight) / 2 + 1, false, Mocha.Text.argb)
        guiGraphics.disableScissor()

        guiGraphics.extractText(if (workInOpen) "▾" else "▸", x + w - client.font.width(if (workInOpen) "▾" else "▸") - 4, y + (fh - client.font.lineHeight) / 2 + 1, false, Mocha.Overlay0.argb)
        zones.add(UIZone(x, y, w, fh, UIZoneType.MODAL_WORK_IN))
    }

    private fun drawWorkInMenu(guiGraphics: GuiGraphics, mx: Int, my: Int, x: Int, y: Int, w: Int) {
        val entries = KeybindWorkIn.entries
        val menuH = entries.size * 14

        guiGraphics.rectangle(x, y, w, menuH, Mocha.Base.argb)
        guiGraphics.outline(x, y, w, menuH, 1, Mocha.Mauve.argb)
        guiGraphics.enableScissor(x, y, x + w, y + menuH)

        var cy = y
        for (e in entries) {
            if (mx in x until x + w && my in cy until cy + 14) guiGraphics.rectangle(x, cy, w, 14, Mocha.Surface1.argb)

            val sel = condition.workIn == e
            guiGraphics.extractText(e.displayName, x + 4, cy + (14 - client.font.lineHeight) / 2 + 1, false, if (sel) Mocha.Mauve.argb else Mocha.Text.argb)
            if (sel) guiGraphics.extractText("✔", x + w - 12, cy + (14 - client.font.lineHeight) / 2 + 1, false, Mocha.Mauve.argb)
            cy += 14
        }

        guiGraphics.disableScissor()
    }

    private fun drawIslandDropdown(guiGraphics: GuiGraphics, mx: Int, my: Int, x: Int, y: Int, w: Int, zones: MutableList<UIZone>) {
        val hov = (!dropdown || islandOpen) && mx in x until x + w && my in y until y + fh
        guiGraphics.rectangle(x, y, w, fh, if (hov) Mocha.Surface2.argb else Mocha.Surface1.argb)
        guiGraphics.outline(x, y, w, fh, 1, if (islandOpen) Mocha.Mauve.argb else Mocha.Overlay0.argb)

        val s = condition.islands.size
        val str = if (s == 0) "Any" else if (s == 1) condition.islands.first().displayName else "$s Islands"

        guiGraphics.enableScissor(x + 2, y, x + w - 14, y + fh)
        guiGraphics.extractText(str, x + (w - 14 - client.font.width(str)) / 2, y + (fh - client.font.lineHeight) / 2 + 1, false, Mocha.Text.argb)
        guiGraphics.disableScissor()

        guiGraphics.extractText(if (islandOpen) "▾" else "▸", x + w - client.font.width(if (islandOpen) "▾" else "▸") - 4, y + (fh - client.font.lineHeight) / 2 + 1, false, Mocha.Overlay0.argb)
        zones.add(UIZone(x, y, w, fh, UIZoneType.MODAL_ISLAND))
    }

    private fun drawIslandMenu(guiGraphics: GuiGraphics, mx: Int, my: Int, x: Int, y: Int, w: Int) {
        val menuH = ((SkyBlockIsland.entries.size + 1) * 14).coerceAtMost(100)

        guiGraphics.rectangle(x, y, w, menuH, Mocha.Base.argb)
        guiGraphics.outline(x, y, w, menuH, 1, Mocha.Mauve.argb)
        guiGraphics.enableScissor(x, y, x + w, y + menuH)

        var cy = y + islandScroll
        if (cy + 14 > y && cy < y + menuH) {
            if (mx in x until x + w && my in cy until cy + 14) guiGraphics.rectangle(x, cy, w, 14, Mocha.Surface1.argb)
            guiGraphics.extractText("Any", x + 4, cy + (14 - client.font.lineHeight) / 2 + 1, false, if (condition.islands.isEmpty()) Mocha.Mauve.argb else Mocha.Text.argb)
        }

        cy += 14
        for (island in SkyBlockIsland.entries) {
            if (cy + 14 > y && cy < y + menuH) {
                if (mx in x until x + w && my in cy until cy + 14) guiGraphics.rectangle(x, cy, w, 14, Mocha.Surface1.argb)

                val sel = island in condition.islands
                guiGraphics.extractText(island.displayName, x + 4, cy + (14 - client.font.lineHeight) / 2 + 1, false, if (sel) Mocha.Mauve.argb else Mocha.Text.argb)
                if (sel) guiGraphics.extractText("✔", x + w - 12, cy + (14 - client.font.lineHeight) / 2 + 1, false, Mocha.Mauve.argb)
            }

            cy += 14
        }

        guiGraphics.disableScissor()
    }

    private fun drawFloorDropdown(guiGraphics: GuiGraphics, mx: Int, my: Int, x: Int, y: Int, w: Int, zones: MutableList<UIZone>) {
        val hov = (!dropdown || floorOpen) && mx in x until x + w && my in y until y + fh
        guiGraphics.rectangle(x, y, w, fh, if (hov) Mocha.Surface2.argb else Mocha.Surface1.argb)
        guiGraphics.outline(x, y, w, fh, 1, if (floorOpen) Mocha.Mauve.argb else Mocha.Overlay0.argb)

        val s = condition.floors.size
        val str = if (s == 0) "Any" else if (s == 1) condition.floors.first().name else "$s Floors"

        guiGraphics.enableScissor(x + 2, y, x + w - 14, y + fh)
        guiGraphics.extractText(str, x + (w - 14 - client.font.width(str)) / 2, y + (fh - client.font.lineHeight) / 2 + 1, false, Mocha.Text.argb)
        guiGraphics.disableScissor()

        guiGraphics.extractText(if (floorOpen) "▾" else "▸", x + w - client.font.width(if (floorOpen) "▾" else "▸") - 4, y + (fh - client.font.lineHeight) / 2 + 1, false, Mocha.Overlay0.argb)
        zones.add(UIZone(x, y, w, fh, UIZoneType.MODAL_FLOOR))
    }

    private fun drawFloorMenu(guiGraphics: GuiGraphics, mx: Int, my: Int, x: Int, y: Int, w: Int) {
        val menuH = ((DungeonFloor.entries.size + 1) * 14).coerceAtMost(100)

        guiGraphics.rectangle(x, y, w, menuH, Mocha.Base.argb)
        guiGraphics.outline(x, y, w, menuH, 1, Mocha.Mauve.argb)
        guiGraphics.enableScissor(x, y, x + w, y + menuH)

        var cy = y + floorScroll
        if (cy + 14 > y && cy < y + menuH) {
            if (mx in x until x + w && my in cy until cy + 14) guiGraphics.rectangle(x, cy, w, 14, Mocha.Surface1.argb)
            guiGraphics.extractText("Any", x + 4, cy + (14 - client.font.lineHeight) / 2 + 1, false, if (condition.floors.isEmpty()) Mocha.Mauve.argb else Mocha.Text.argb)
        }

        cy += 14
        for (floor in DungeonFloor.entries) {
            if (cy + 14 > y && cy < y + menuH) {
                if (mx in x until x + w && my in cy until cy + 14) guiGraphics.rectangle(x, cy, w, 14, Mocha.Surface1.argb)

                val sel = floor in condition.floors
                guiGraphics.extractText(floor.name, x + 4, cy + (14 - client.font.lineHeight) / 2 + 1, false, if (sel) Mocha.Mauve.argb else Mocha.Text.argb)
                if (sel) guiGraphics.extractText("✔", x + w - 12, cy + (14 - client.font.lineHeight) / 2 + 1, false, Mocha.Mauve.argb)
            }

            cy += 14
        }

        guiGraphics.disableScissor()
    }

    private fun drawClassDropdown(guiGraphics: GuiGraphics, mx: Int, my: Int, x: Int, y: Int, w: Int, zones: MutableList<UIZone>) {
        val hov = (!dropdown || classOpen) && mx in x until x + w && my in y until y + fh
        guiGraphics.rectangle(x, y, w, fh, if (hov) Mocha.Surface2.argb else Mocha.Surface1.argb)
        guiGraphics.outline(x, y, w, fh, 1, if (classOpen) Mocha.Mauve.argb else Mocha.Overlay0.argb)

        val s = condition.classes.size
        val str = if (s == 0) "Any" else if (s == 1) condition.classes.first().name.lowercase().replaceFirstChar { it.uppercase() } else "$s Classes"

        guiGraphics.enableScissor(x + 2, y, x + w - 14, y + fh)
        guiGraphics.extractText(str, x + (w - 14 - client.font.width(str)) / 2, y + (fh - client.font.lineHeight) / 2 + 1, false, Mocha.Text.argb)
        guiGraphics.disableScissor()

        guiGraphics.extractText(if (classOpen) "▾" else "▸", x + w - client.font.width(if (classOpen) "▾" else "▸") - 4, y + (fh - client.font.lineHeight) / 2 + 1, false, Mocha.Overlay0.argb)
        zones.add(UIZone(x, y, w, fh, UIZoneType.MODAL_CLASS))
    }

    private fun drawClassMenu(guiGraphics: GuiGraphics, mx: Int, my: Int, x: Int, y: Int, w: Int) {
        val all = DungeonClass.entries.filter { it != DungeonClass.DEAD && it != DungeonClass.UNKNOWN }
        val menuH = ((all.size + 1) * 14).coerceAtMost(100)

        guiGraphics.rectangle(x, y, w, menuH, Mocha.Base.argb)
        guiGraphics.outline(x, y, w, menuH, 1, Mocha.Mauve.argb)
        guiGraphics.enableScissor(x, y, x + w, y + menuH)

        var cy = y + classScroll
        if (cy + 14 > y && cy < y + menuH) {
            if (mx in x until x + w && my in cy until cy + 14) guiGraphics.rectangle(x, cy, w, 14, Mocha.Surface1.argb)
            guiGraphics.extractText("Any", x + 4, cy + (14 - client.font.lineHeight) / 2 + 1, false, if (condition.classes.isEmpty()) Mocha.Mauve.argb else Mocha.Text.argb)
        }

        cy += 14
        for (c in all) {
            if (cy + 14 > y && cy < y + menuH) {
                if (mx in x until x + w && my in cy until cy + 14) guiGraphics.rectangle(x, cy, w, 14, Mocha.Surface1.argb)

                val sel = c in condition.classes
                guiGraphics.extractText(c.displayName, x + 4, cy + (14 - client.font.lineHeight) / 2 + 1, false, if (sel) Mocha.Mauve.argb else Mocha.Text.argb)
                if (sel) guiGraphics.extractText("✔", x + w - 12, cy + (14 - client.font.lineHeight) / 2 + 1, false, Mocha.Mauve.argb)
            }

            cy += 14
        }

        guiGraphics.disableScissor()
    }

    private fun drawF7PhaseDropdown(guiGraphics: GuiGraphics, mx: Int, my: Int, x: Int, y: Int, w: Int, zones: MutableList<UIZone>) {
        val hov = (!dropdown || f7PhaseOpen) && mx in x until x + w && my in y until y + fh
        guiGraphics.rectangle(x, y, w, fh, if (hov) Mocha.Surface2.argb else Mocha.Surface1.argb)
        guiGraphics.outline(x, y, w, fh, 1, if (f7PhaseOpen) Mocha.Mauve.argb else Mocha.Overlay0.argb)

        val s = condition.phases.size
        val str = if (s == 0) "Any" else if (s == 1) "Phase ${condition.phases.first()}" else "$s Phases"

        guiGraphics.enableScissor(x + 2, y, x + w - 14, y + fh)
        guiGraphics.extractText(str, x + (w - 14 - client.font.width(str)) / 2, y + (fh - client.font.lineHeight) / 2 + 1, false, Mocha.Text.argb)
        guiGraphics.disableScissor()

        guiGraphics.extractText(if (f7PhaseOpen) "▾" else "▸", x + w - client.font.width(if (f7PhaseOpen) "▾" else "▸") - 4, y + (fh - client.font.lineHeight) / 2 + 1, false, Mocha.Overlay0.argb)
        zones.add(UIZone(x, y, w, fh, UIZoneType.MODAL_F7_PHASE))
    }

    private fun drawF7PhaseMenu(guiGraphics: GuiGraphics, mx: Int, my: Int, x: Int, y: Int, w: Int) {
        val menuH = ((allPhases.size + 1) * 14).coerceAtMost(100)

        guiGraphics.rectangle(x, y, w, menuH, Mocha.Base.argb)
        guiGraphics.outline(x, y, w, menuH, 1, Mocha.Mauve.argb)
        guiGraphics.enableScissor(x, y, x + w, y + menuH)

        var cy = y + f7PhaseScroll
        if (cy + 14 > y && cy < y + menuH) {
            if (mx in x until x + w && my in cy until cy + 14) guiGraphics.rectangle(x, cy, w, 14, Mocha.Surface1.argb)
            guiGraphics.extractText("Any", x + 4, cy + (14 - client.font.lineHeight) / 2 + 1, false, if (condition.phases.isEmpty()) Mocha.Mauve.argb else Mocha.Text.argb)
        }

        cy += 14
        for (p in allPhases) {
            if (cy + 14 > y && cy < y + menuH) {
                if (mx in x until x + w && my in cy until cy + 14) guiGraphics.rectangle(x, cy, w, 14, Mocha.Surface1.argb)

                val sel = p in condition.phases
                guiGraphics.extractText("Phase $p", x + 4, cy + (14 - client.font.lineHeight) / 2 + 1, false, if (sel) Mocha.Mauve.argb else Mocha.Text.argb)
                if (sel) guiGraphics.extractText("✔", x + w - 12, cy + (14 - client.font.lineHeight) / 2 + 1, false, Mocha.Mauve.argb)
            }

            cy += 14
        }

        guiGraphics.disableScissor()
    }

    private fun drawKeysField(guiGraphics: GuiGraphics, mx: Int, my: Int, x: Int, y: Int, w: Int, zones: MutableList<UIZone>) {
        val hov = !dropdown && (mx in x until x + w && my in y until y + fh)
        guiGraphics.rectangle(x, y, w, fh, if (keysListening) Mocha.Peach.withAlpha(0.3f) else if (hov) Mocha.Surface1.argb else Mocha.Surface0.argb)
        guiGraphics.outline(x, y, w, fh, 1, if (keysListening) Mocha.Peach.argb else Mocha.Overlay0.argb)

        val str = when {
            keysListening -> if (recorded.isEmpty()) "Press keys..." else recorded.toList().str()
            keysBuf.isEmpty() -> "Click to bind"
            else -> keysBuf.str()
        }

        guiGraphics.enableScissor(x + 2, y, x + w - 2, y + fh)
        guiGraphics.extractText(str, x + (w - client.font.width(str)) / 2, y + (fh - client.font.lineHeight) / 2 + 1, false, Mocha.Text.argb)
        guiGraphics.disableScissor()
        zones.add(UIZone(x, y, w, fh, UIZoneType.MODAL_KEYS))
    }

    fun open() {
        entry = null
        reset()
        open = true
    }

    fun open(e0: BindingEntry) {
        entry = e0
        cmdField.reset(true)
        cmdField.value = e0.binding.command
        cmdField.cursor = cmdField.value.length
        cmdField.focused = true
        keysBuf = e0.binding.keys.toMutableList()
        condition = e0.condition.copy()
        category = e0.binding.category
        keysListening = false
        recorded.clear()

        categoryOpen = false
        workInOpen = false
        islandOpen = false
        floorOpen = false
        classOpen = false
        f7PhaseOpen = false

        open = true
    }

    override fun onClose() {
        cmdField.focused = false
    }

    fun clickCategory(mouseX: Int, mouseY: Int) {
        val sw = client.window.guiScaledWidth
        val mx0 = (sw - mw) / 2
        val fw = mw - padding * 2
        val halfW = fw / 2 - 4
        val menuX = mx0 + padding

        if (mouseX !in menuX until menuX + halfW) {
            categoryOpen = false
            return
        }

        val menuY = categoryDropdownY + fh
        val cats = Keybinds.categories.value
        val menuH = ((cats.size + 1) * 14).coerceAtMost(80)

        if (mouseY in menuY until menuY + menuH) {
            var iy = menuY
            if (mouseY in iy until iy + 14) {
                category = ""
                categoryOpen = false
                return
            }

            iy += 14
            for (cat in cats) {
                if (mouseY in iy until iy + 14) {
                    category = cat.name
                    categoryOpen = false
                    return
                }

                iy += 14
            }
        }

        categoryOpen = false
    }

    fun clickWorkIn(mouseX: Int, mouseY: Int) {
        val sw = client.window.guiScaledWidth
        val mx0 = (sw - mw) / 2
        val fw = mw - padding * 2
        val halfW = fw / 2 - 4
        val menuX = mx0 + padding + halfW + 8

        if (mouseX !in menuX until menuX + halfW) {
            workInOpen = false
            return
        }

        val menuY = workInDropdownY + fh
        val entries = KeybindWorkIn.entries
        val menuH = entries.size * 14

        if (mouseY in menuY until menuY + menuH) {
            var iy = menuY

            for (w in entries) {
                if (mouseY in iy until iy + 14) {
                    condition.workIn = w
                    workInOpen = false
                    return
                }

                iy += 14
            }
        }

        workInOpen = false
    }

    fun clickIsland(mouseX: Int, mouseY: Int) {
        val sw = client.window.guiScaledWidth
        val mx0 = (sw - mw) / 2
        val fw = mw - padding * 2
        val halfW = fw / 2 - 4
        val menuX = mx0 + padding

        if (mouseX !in menuX until menuX + halfW) {
            islandOpen = false
            return
        }

        val menuY = islandDropdownY + fh
        val menuH = ((SkyBlockIsland.entries.size + 1) * 14).coerceAtMost(100)

        if (mouseY in menuY until menuY + menuH) {
            var cy = menuY + islandScroll
            if (mouseY in cy until cy + 14) return condition.islands.clear()
            cy += 14

            for (island in SkyBlockIsland.entries) {
                if (mouseY in cy until cy + 14) {
                    if (island in condition.islands) condition.islands.remove(island) else condition.islands.add(island)
                    return
                }

                cy += 14
            }
        }

        islandOpen = false
    }

    fun clickFloor(mouseX: Int, mouseY: Int) {
        val sw = client.window.guiScaledWidth
        val mx0 = (sw - mw) / 2
        val fw = mw - padding * 2
        val halfW = fw / 2 - 4
        val menuX = mx0 + padding + halfW + 8

        if (mouseX !in menuX until menuX + halfW) {
            floorOpen = false
            return
        }

        val menuY = floorDropdownY + fh
        val menuH = ((DungeonFloor.entries.size + 1) * 14).coerceAtMost(100)

        if (mouseY in menuY until menuY + menuH) {
            var cy = menuY + floorScroll
            if (mouseY in cy until cy + 14) return condition.floors.clear()
            cy += 14

            for (f in DungeonFloor.entries) {
                if (mouseY in cy until cy + 14) {
                    if (f in condition.floors) condition.floors.remove(f) else condition.floors.add(f)
                    return
                }

                cy += 14
            }
        }

        floorOpen = false
    }

    fun clickClass(mouseX: Int, mouseY: Int) {
        val sw = client.window.guiScaledWidth
        val mx0 = (sw - mw) / 2
        val fw = mw - padding * 2
        val halfW = fw / 2 - 4
        val menuX = mx0 + padding

        if (mouseX !in menuX until menuX + halfW) {
            classOpen = false
            return
        }

        val menuY = classDropdownY + fh
        val allClasses = DungeonClass.entries.filter { it != DungeonClass.DEAD && it != DungeonClass.UNKNOWN }
        val menuH = ((allClasses.size + 1) * 14).coerceAtMost(100)

        if (mouseY in menuY until menuY + menuH) {
            var cy = menuY + classScroll
            if (mouseY in cy until cy + 14) return condition.classes.clear()
            cy += 14

            for (c in allClasses) {
                if (mouseY in cy until cy + 14) {
                    if (c in condition.classes) condition.classes.remove(c) else condition.classes.add(c)
                    return
                }

                cy += 14
            }
        }

        classOpen = false
    }

    fun clickF7Phase(mouseX: Int, mouseY: Int) {
        val sw = client.window.guiScaledWidth
        val mx0 = (sw - mw) / 2
        val fw = mw - padding * 2
        val halfW = fw / 2 - 4
        val menuX = mx0 + padding + halfW + 8

        if (mouseX !in menuX until menuX + halfW) {
            f7PhaseOpen = false
            return
        }

        val menuY = f7PhaseDropdownY + fh
        val menuH = ((allPhases.size + 1) * 14).coerceAtMost(100)

        if (mouseY in menuY until menuY + menuH) {
            var cy = menuY + f7PhaseScroll
            if (mouseY in cy until cy + 14) return condition.phases.clear()
            cy += 14

            for (p in allPhases) {
                if (mouseY in cy until cy + 14) {
                    if (p in condition.phases) condition.phases.remove(p) else condition.phases.add(p)
                    return
                }

                cy += 14
            }
        }

        f7PhaseOpen = false
    }

    fun scroll(amount: Int) {
        if (islandOpen) {
            val contentH = (SkyBlockIsland.entries.filter { it.name != "UNKNOWN" }.size + 1) * 14
            islandScroll = (islandScroll + amount).coerceIn(-maxOf(0, contentH - 100), 0)
        }

        if (floorOpen) {
            val contentH = (DungeonFloor.entries.filter { it.name != "UNKNOWN" }.size + 1) * 14
            floorScroll = (floorScroll + amount).coerceIn(-maxOf(0, contentH - 100), 0)
        }

        if (classOpen) {
            val contentH = (DungeonClass.entries.filter { it.name != "DEAD" }.size + 1) * 14
            classScroll = (classScroll + amount).coerceIn(-maxOf(0, contentH - 100), 0)
        }

        if (f7PhaseOpen) {
            val contentH = (5 + 1) * 14
            f7PhaseScroll = (f7PhaseScroll + amount).coerceIn(-maxOf(0, contentH - 100), 0)
        }
    }

    private fun reset() {
        cmdField.reset(true)
        cmdField.focused = true
        keysBuf = mutableListOf()
        condition = KeybindCondition()
        category = ""
        keysListening = false
        recorded.clear()
        
        categoryOpen = false
        workInOpen = false
        islandOpen = false
        floorOpen = false
        classOpen = false
        f7PhaseOpen = false
        
        islandScroll = 0
        floorScroll = 0
        classScroll = 0
        f7PhaseScroll = 0
    }
}
