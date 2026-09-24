package foo.starred.athen.modules.impl.slayer

import foo.starred.athen.annotations.Load
import foo.starred.athen.annotations.OnlyIn
import foo.starred.athen.api.location.island.impl.PresetSkyBlockIsland
import foo.starred.athen.api.minecraft.text.measurer.VanillaFontMeasurer
import foo.starred.athen.api.minecraft.text.renderer.VanillaFontRenderer
import foo.starred.athen.api.slayers.SlayerAPI
import foo.starred.athen.config.dsl.impl.category.ConfigCategory
import foo.starred.athen.ducks.entity.EntityDuck.Companion.parent
import foo.starred.athen.events.EntityEvent
import foo.starred.athen.events.SlayerEvent
import foo.starred.athen.events.TickEvent
import foo.starred.athen.events.core.runWhen
import foo.starred.athen.modules.Module
import foo.starred.snowbird.api.data.Observable
import foo.starred.snowbird.api.held
import foo.starred.snowbird.utils.toDuration
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData

@Load
@OnlyIn(islands = [PresetSkyBlockIsland.CRIMSON_ISLE])
object VengeanceTimer : Module(
    "Vengeance timer",
    "Shows the time until your vengeance damage should activate.",
    ConfigCategory.SLAYER
) {
    private val compact = config.switch("Compact display").unique("compact")
    private val useTicks by config.switch("Use ticks", true)
    private val abilityIds = listOf("HEARTFIRE_DAGGER", "BURSTFIRE_DAGGER", "FIREDUST_DAGGER")
    private var count: Observable<Boolean> = Observable(false)
    private var countDown: Int = 120

    private val hud by config.hud("Vengeance") {
        constrain {
            VanillaFontMeasurer.constrain(if (compact.value) "120" else "§cVengeance: §f120")
        }

        preview {
            VanillaFontRenderer.extract(graphics, if (compact.value) "120" else "§cVengeance: §f120", 0, 0)
        }

        render {
            if (!count.value) return@render

            val value = if (useTicks) "$countDown" else (countDown / 20.0).toDuration(secondsDecimals = 1)
            VanillaFontRenderer.extract(graphics, if (compact.value) value else "§cVengeance: §f$value", 0, 0)
        }
    }

    init {
        compact.state.onChange {
            hud.constrain()
        }

        on<TickEvent.Server> {
            if (countDown > 0) countDown-- else reset()
        }.runWhen(count)

        on<EntityEvent.Update.Named> {
            if (count.value) return@on
            val entity = entity.parent ?: return@on
            val slayerInfo = SlayerAPI.bosses[entity] ?: return@on

            if (!slayerInfo.owned) return@on
            if (!stripped.contains("ASHEN ♨7")) return@on
            if (held?.getData(DataTypes.SKYBLOCK_ID)?.skyblockId !in abilityIds) return@on

            count.value = true
        }

        on<SlayerEvent.Boss.Death> {
            reset()
        }

        on<SlayerEvent.Reset.Any> {
            reset()
        }
    }

    private fun reset() {
        count.value = false
        countDown = 120
    }
}
