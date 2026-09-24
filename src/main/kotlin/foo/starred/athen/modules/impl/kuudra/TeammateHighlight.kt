package foo.starred.athen.modules.impl.kuudra

import foo.starred.athen.annotations.Load
import foo.starred.athen.annotations.OnlyIn
import foo.starred.athen.api.kuudra.KuudraAPI
import foo.starred.athen.api.location.SkyBlockIsland
import foo.starred.athen.api.rendering.level.impl.extensions.impl.extractFrameBox
import foo.starred.athen.config.dsl.impl.category.ConfigCategory
import foo.starred.athen.config.theme.impl.catppuccin.MochaColorScheme
import foo.starred.athen.events.WorldRenderEvent
import foo.starred.athen.modules.Module
import foo.starred.athen.utils.render.renderBoundingBox
import foo.starred.snowbird.api.name

@Load
@OnlyIn(islands = [SkyBlockIsland.KUUDRA])
object TeammateHighlight : Module(
    "Teammate highlight",
    "Highlights your teammates in kuudra!",
    ConfigCategory.KUUDRA
) {
    private val lineWidth by config.slider("Line width", 2f, 1f, 10f)
    private val color by config.colorPicker("Color", MochaColorScheme.Green.argb)

    init {
        on<WorldRenderEvent.Extract> {
            for (p in KuudraAPI.teammates) {
                if (p.name == name) continue
                val e = p.entity ?: continue

                extractFrameBox(e.renderBoundingBox, color, lineWidth)
            }
        }
    }
}
