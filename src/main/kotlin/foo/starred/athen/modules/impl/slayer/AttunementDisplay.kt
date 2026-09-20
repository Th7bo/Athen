package foo.starred.athen.modules.impl.slayer

import foo.starred.athen.annotations.Load
import foo.starred.athen.annotations.OnlyIn
import foo.starred.athen.api.location.SkyBlockIsland
import foo.starred.athen.api.minecraft.text.measurer.VanillaFontMeasurer
import foo.starred.athen.api.minecraft.text.renderer.VanillaFontRenderer
import foo.starred.athen.api.slayers.SlayerAPI
import foo.starred.athen.config.Category
import foo.starred.athen.ducks.entity.EntityDuck.Companion.parent
import foo.starred.athen.events.EntityEvent
import foo.starred.athen.events.SlayerEvent
import foo.starred.athen.modules.Module
import foo.starred.athen.utils.render.fcs
import foo.starred.snowbird.utils.literal
import net.minecraft.util.FormattedCharSequence
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findGroups

@Load
@OnlyIn(islands = [SkyBlockIsland.CRIMSON_ISLE])
object AttunementDisplay : Module(
    "Attunement display",
    "Displays the current attunement for blaze slayer, does not work with demons.",
    Category.SLAYER
) {
    private val count by config.switch("Display count")
    private val regex = Regex("^(?<attunement>[A-Z]+) ♨(?<count>\\d) \\d\\d:\\d\\d$")
    private var last: FormattedCharSequence? = null

    init {
        config.hud("Attunement display") {
            val example = "§l§eAURIC ♨5".fcs

            constrain {
                VanillaFontMeasurer.constrain(example)
            }

            preview {
                VanillaFontRenderer.extract(graphics, example, 0, 0)
            }

            render {
                VanillaFontRenderer.extract(graphics, last ?: return@render, 0, 0)
            }
        }

        on<EntityEvent.Update.Named> {
            val e = entity.parent ?: return@on
            val s = SlayerAPI.bosses[e] ?: return@on
            if (!s.owned) return@on

            val a = regex.findGroups(stripped, "attunement", "count") ?: return@on
            val b = a["attunement"]?.fn() ?: return@on
            val c = a["count"]?.toIntOrNull() ?: return@on

            last = b.fn0(c).literal().visualOrderText
        }

        on<SlayerEvent.Boss.Death> {
            last = null
        }

        on<SlayerEvent.Reset.Any> {
            last = null
        }
    }

    private fun String.fn(): String? = when (this) {
        "ASHEN" -> "§l§8$this"
        "AURIC" -> "§l§e$this"
        "CRYSTAL" -> "§l§b$this"
        "SPIRIT" -> "§l§f$this"
        else -> null
    }

    private fun String.fn0(int: Int): String {
        if (!count) return this
        return "$this ♨$int"
    }
}
