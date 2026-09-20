package foo.starred.athen.config.hud.data.element

import foo.starred.athen.config.dsl.impl.builders.hud.ConfigHudBuilder
import net.minecraft.client.gui.GuiGraphicsExtractor

data class HudElement(
    val coordinate: HudElementCoordinateData,
    val config: ConfigHudBuilder
) {
    val render0: Boolean
        get() = (config.builder.module?.enabled ?: config.builder.value) && config.state.value

    val render1: Boolean
        get() = render0 && config.outside

    fun render(graphics: GuiGraphicsExtractor) {
        config.renderer?.invoke(graphics)
    }

    fun preview(graphics: GuiGraphicsExtractor) {
        config.preview?.invoke(graphics)
    }

    fun hovered(x: Float, y: Float): Boolean {
        val offset = 4f * coordinate.scale
        return x >= coordinate.x - offset && x <= coordinate.x + (coordinate.width + 4f) * coordinate.scale && y >= coordinate.y - offset && y <= coordinate.y + (coordinate.height + 4f) * coordinate.scale
    }
}
