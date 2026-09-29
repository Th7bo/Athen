package foo.starred.athen.modules.impl.slayer

import foo.starred.athen.annotations.Load
import foo.starred.athen.annotations.OnlyIn
import foo.starred.athen.api.minecraft.text.measurer.VanillaFontMeasurer
import foo.starred.athen.api.minecraft.text.renderer.VanillaFontRenderer
import foo.starred.athen.api.scheduling.Ticking
import foo.starred.athen.api.slayers.SlayerAPI
import foo.starred.athen.api.slayers.enums.type.impl.SlayerBoss
import foo.starred.athen.config.dsl.impl.category.ConfigCategory
import foo.starred.athen.ducks.entity.EntityDuck.Companion.attachedNames
import foo.starred.athen.modules.Module
import foo.starred.athen.utils.render.fcs
import foo.starred.snowbird.utils.stripped
import net.minecraft.network.chat.Component

@Load
@OnlyIn(skyblock = true)
object SlayerDisplay : Module(
    "Slayer display",
    "Displays the slayer boss's nametags on your screen.",
    ConfigCategory.SLAYER
) {
    private val display = Ticking(2) {
        val entity = SlayerAPI.slayer?.entity ?: return@Ticking null

        val lines = entity.attachedNames
        var colon: Component? = null
        var name: Component? = null

        for (l in lines) {
            val s = l.stripped()
            if ("Spawned by:" in s) continue

            colon = colon ?: l.takeIf { ":" in s }
            name = name ?: l.takeIf { SlayerBoss.NAMES.any { it in s } }

            if (colon != null && name != null) break
        }

        listOfNotNull(colon, name)
    }

    init {
        config.hud("Display HUD") {
            val example = listOf("§c02:46", "§c☠ §bRevenant Horror I §a500§c❤").fcs

            constrain {
                VanillaFontMeasurer.constrain(example)
            }

            preview {
                VanillaFontRenderer.extract(graphics, example, 0, 0, center = listOf(0))
            }

            render {
                VanillaFontRenderer.extract(graphics, display.value ?: return@render, 0, 0, center = listOf(0))
            }
        }
    }
}
