package foo.starred.athen.modules.impl.general

import foo.starred.athen.annotations.Load
import foo.starred.athen.annotations.OnlyIn
import foo.starred.athen.api.messaging.impl.MessagingAPI.mod
import foo.starred.athen.api.minecraft.text.measurer.VanillaFontMeasurer
import foo.starred.athen.api.minecraft.text.renderer.VanillaFontRenderer
import foo.starred.athen.config.Category
import foo.starred.athen.events.LocationEvent
import foo.starred.athen.events.TickEvent
import foo.starred.athen.events.core.runWhen
import foo.starred.athen.modules.Module
import foo.starred.athen.utils.render.fcs
import foo.starred.snowbird.api.command
import foo.starred.snowbird.api.player

@Load
@OnlyIn(skyblock = true)
object LagDetector : Module(
    "Lag detector",
    "Displays a timer since the last server tick if it was older than the threshold.",
    Category.GENERAL
) {
    private val threshold by config.slider("Threshold", 750, 100, 5000, "ms")
    private val notify = config.switch("Send message").unique("notify")
    private val party by config.switch("Notify party")
    private val text by config.input("Message", "Lag detected!")

    private var last = 0L
    private var bool = false

    init {
        config.hud("Lag display") {
            val example = "§c67ms".fcs

            constrain {
                VanillaFontMeasurer.constrain(example)
            }

            preview {
                VanillaFontRenderer.extract(graphics, example, 0, 0)
            }

            render {
                if (last == 0L) return@render
                if (player == null) return@render

                val t = System.currentTimeMillis() - last
                if (t <= threshold) return@render

                VanillaFontRenderer.extract(graphics, "§c${t}ms", 0, 0)
            }
        }

        on<TickEvent.Client.End> {
            if (last == 0L) return@on
            if (player == null) return@on

            val t = System.currentTimeMillis() - last
            if (t <= threshold) return@on
            if (bool) return@on

            bool = true
            text.mod()
            if (party) "/pc $text".command()
        }.runWhen(notify.state)

        on<TickEvent.Server> {
            last = System.currentTimeMillis()
            bool = false
        }

        on<LocationEvent.Server.Connect> {
            last = 0
            bool = false
        }
    }
}
