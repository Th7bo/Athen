@file:Suppress("ObjectPrivatePropertyName")

package foo.starred.athen.modules.impl.kuudra

import foo.starred.athen.annotations.Load
import foo.starred.athen.annotations.OnlyIn
import foo.starred.athen.api.kuudra.KuudraAPI
import foo.starred.athen.api.kuudra.enums.KuudraPhase
import foo.starred.athen.api.kuudra.enums.KuudraSupply
import foo.starred.athen.api.location.island.impl.PresetSkyBlockIsland
import foo.starred.athen.api.messaging.impl.MessagingAPI.mod
import foo.starred.athen.api.minecraft.text.measurer.VanillaFontMeasurer
import foo.starred.athen.api.minecraft.text.renderer.VanillaFontRenderer
import foo.starred.athen.api.rendering.level.impl.extensions.impl.extractFrameBox
import foo.starred.athen.api.scheduling.Ticking
import foo.starred.athen.config.dsl.impl.category.ConfigCategory
import foo.starred.athen.config.theme.impl.catppuccin.MochaColorScheme
import foo.starred.athen.events.KuudraEvent
import foo.starred.athen.events.WorldRenderEvent
import foo.starred.athen.events.core.runWhen
import foo.starred.athen.modules.Module
import foo.starred.athen.utils.render.fcs
import foo.starred.snowbird.api.text.parser.impl.parse
import foo.starred.snowbird.utils.alert

@Load
@OnlyIn(islands = [PresetSkyBlockIsland.KUUDRA])
object BuildInfo : Module(
    "Build info",
    "Shows information about the ballista build process in phase 2.",
    ConfigCategory.KUUDRA
) {
    private val waypoints = config.switch("Unfinished build waypoint", true).unique("waypoints")
    private val color by config.colorPicker("Color", MochaColorScheme.Red.argb)
    private val stun by config.switch("Stun notification", true)
    private val `stun$percent` by config.slider("Notify at", 90, 1, 100, "%")
    private val `stun$message` by config.input("Notification message", "<red>Stun!")

    private val display = Ticking {
        listOf("§7Builders: §c${KuudraAPI.buildPlayers}", "§7Progress: §c${KuudraAPI.buildProgress.value}%").fcs
    }

    private val render: Boolean
        get() = KuudraAPI.inRun && KuudraAPI.phase == KuudraPhase.Build

    private var sent: Boolean = false

    init {
        KuudraAPI.buildProgress.onChange {
            if (!stun) return@onChange
            if (sent) return@onChange
            if (it <= `stun$percent`) return@onChange

            val prs = `stun$message`.parse(true)
            prs.alert()
            prs.mod()
            sent = true
        }

        config.hud("Build info") {
            val example = listOf("§7Builders: §c3", "§7Progress: §c47%").fcs

            constrain {
                VanillaFontMeasurer.constrain(example)
            }

            preview {
                VanillaFontRenderer.extract(graphics, example, 0, 0)
            }

            render {
                if (!render) return@render

                VanillaFontRenderer.extract(graphics, display.value ?: return@render, 0, 0)
            }
        }

        on<KuudraEvent.Start> {
            sent = false
        }

        on<WorldRenderEvent.Extract> {
            if (!render) return@on

            for (e in KuudraSupply.every) if (!e.built) extractFrameBox(e.buildAABB, color, depth = false)
        }.runWhen(waypoints.state)
    }
}
