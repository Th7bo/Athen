@file:Suppress("ConstPropertyName")

package foo.starred.athen.modules.impl.render

import foo.starred.athen.annotations.Load
import foo.starred.athen.api.minecraft.text.measurer.VanillaFontMeasurer
import foo.starred.athen.api.minecraft.text.renderer.VanillaFontRenderer
import foo.starred.athen.config.Category
import foo.starred.athen.config.hud.impl.HudRenderer
import foo.starred.athen.modules.Module
import foo.starred.athen.utils.render.fcs

@Load
object ItemNamePosition : Module(
    "Item name position",
    "Changes the positions of item display names",
    Category.RENDER
) {
    private val example = "§cEpic item".fcs
    private val int by lazy {
        VanillaFontMeasurer.width(example)
    }

    val hud by config.hud("Item name") {
        constrain {
            VanillaFontMeasurer.constrain(example)
        }

        preview {
            VanillaFontRenderer.extract(graphics, example, 0, 0)
        }
    }

    @JvmStatic
    fun x(): Int {
        return ((hud.coordinate.x + int / 2) * HudRenderer.scale).toInt()
    }

    @JvmStatic
    fun y(): Int {
        return (hud.coordinate.y * HudRenderer.scale).toInt()
    }
}
