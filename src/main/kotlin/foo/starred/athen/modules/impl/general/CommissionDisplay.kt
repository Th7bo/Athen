@file:Suppress("Unused", "ObjectPrivatePropertyName")

package foo.starred.athen.modules.impl.general

import foo.starred.athen.annotations.Load
import foo.starred.athen.annotations.OnlyIn
import foo.starred.athen.api.location.island.impl.PresetSkyBlockIsland
import foo.starred.athen.api.minecraft.text.measurer.VanillaFontMeasurer
import foo.starred.athen.api.minecraft.text.renderer.VanillaFontRenderer
import foo.starred.athen.api.scheduling.Ticking
import foo.starred.athen.config.dsl.impl.category.ConfigCategory
import foo.starred.athen.config.theme.impl.catppuccin.MochaColorScheme
import foo.starred.athen.modules.Module
import foo.starred.athen.utils.render.fcs
import foo.starred.snowbird.api.text.parser.impl.parse
import foo.starred.snowbird.utils.formatted
import net.minecraft.util.FormattedCharSequence
import tech.thatgravyboat.skyblockapi.api.area.mining.Commission
import tech.thatgravyboat.skyblockapi.api.area.mining.CommissionArea
import tech.thatgravyboat.skyblockapi.api.area.mining.CommissionsAPI

@Load
@OnlyIn(islands = [PresetSkyBlockIsland.DWARVEN_MINES, PresetSkyBlockIsland.MINESHAFT, PresetSkyBlockIsland.CRYSTAL_HOLLOWS])
object CommissionDisplay : Module(
    "Commission display",
    "Displays your commissions without you having to open the tab menu!",
    ConfigCategory.GENERAL
) {
    private val titleStyle = config.input("General title", "<red>Commissions:").unique("titleStyle")
    private val noneStyle = config.input("None available text", "<red>No commissions available!").unique("noneStyle")
    private val commissionStyle by config.input("Commission text", "<gray>- <r>#name: #progress")
    private val `commissionStyle$perc` by config.switch("Colored percent", true)
    private val unused by config.variables("#name", "#progress")

    private var fcs0 = noneStyle.value.parse().visualOrderText
    private var fcs1 = titleStyle.value.parse().visualOrderText
    private val display = Ticking(20) {
        val area = CommissionArea.currentArea ?: return@Ticking listOf(fcs1, fcs0)
        val commissions = CommissionsAPI.commissions.filter { it.area == area }.takeIf { it.isNotEmpty() } ?: return@Ticking listOf(fcs1, fcs0)

        buildList {
            add(fcs1)
            for (c in commissions) add(c.prs())
        }
    }

    init {
        noneStyle.state.onChange { fcs0 = it.parse().visualOrderText }
        titleStyle.state.onChange { fcs1 = it.parse().visualOrderText }

        config.hud("Commission display") {
            val example = listOf("§cCommissions:", "§7- §fExample: §640%", "§7- §fExample: §e70%", "§7- §fExample: §c7%").fcs

            constrain {
                VanillaFontMeasurer.constrain(example)
            }

            preview {
                VanillaFontRenderer.extract(graphics,example, 0, 0)
            }

            render {
                VanillaFontRenderer.extract(graphics, display.value ?: return@render, 0, 0)
            }
        }
    }

    private fun Commission.prs(): FormattedCharSequence {
        val p = (progress * 100f).coerceIn(0f, 100f)
        val c = when {
            !`commissionStyle$perc` -> ""
            p >= 100f -> "<${MochaColorScheme.Green.argb}>"
            p >= 75f -> "<${MochaColorScheme.Teal.argb}>"
            p >= 50f -> "<yellow>"
            p >= 25f -> "<gold>"
            else -> "<red>"
        }

        return commissionStyle
            .replace("#name", name)
            .replace("#progress", "$c${p.formatted()}%")
            .parse(true)
            .visualOrderText
    }
}
