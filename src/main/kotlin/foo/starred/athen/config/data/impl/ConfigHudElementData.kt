package foo.starred.athen.config.data.impl

import foo.starred.athen.config.data.base.IConfigElementData
import foo.starred.athen.config.dsl.impl.builders.config.ConfigMainBuilder
import foo.starred.athen.config.dsl.impl.builders.hud.ConfigHudBuilder
import foo.starred.athen.config.hud.data.element.HudElementCoordinateData

data class ConfigHudElementData(
    override val name: String,
    override val key: String,
    val default: Boolean,
    val coordinate: HudElementCoordinateData,
    val hud: ConfigHudBuilder,
    val config: ConfigMainBuilder,
    override val parent: String? = null,
    override val description: String? = null
) : IConfigElementData {
    override fun with(key: String, parent: String?, description: String?): ConfigHudElementData {
        return copy(key = key, parent = parent, description = description)
    }
}