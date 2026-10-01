package foo.starred.athen.config.ui.pages.module.elements.group

import com.mojang.blaze3d.platform.InputConstants
import foo.starred.athen.api.storage.ResourceAPI
import foo.starred.athen.config.ConfigManager
import foo.starred.athen.config.data.impl.ConfigGroupElementData
import foo.starred.athen.config.theme.impl.catppuccin.MochaColorScheme
import foo.starred.cascade.animation.extension.impl.animate
import foo.starred.cascade.animation.interpolator.easing.impl.EaseOutEasingInterpolator
import foo.starred.cascade.constraints.impl.data.PositionAlignment
import foo.starred.cascade.constraints.impl.position.AlignPositionConstraint
import foo.starred.cascade.constraints.impl.position.FixedPositionConstraint
import foo.starred.cascade.constraints.impl.size.FixedSizeConstraint
import foo.starred.cascade.events.impl.MouseEvent
import foo.starred.cascade.graphics.geometry.CascadeGeometricColor
import foo.starred.cascade.graphics.geometry.CascadeGeometricOffset
import foo.starred.cascade.primitives.base.impl.IPrimitiveElement
import foo.starred.cascade.primitives.impl.ImagePrimitive.Companion.image
import foo.starred.cascade.primitives.impl.RectanglePrimitive.Companion.rectangle
import kotlin.time.Duration.Companion.milliseconds

object ConfigGroupElement {
    fun of(parent: IPrimitiveElement<*>, config: ConfigGroupElementData, function: (Boolean) -> Unit) {
        var expanded = ConfigManager.get(config.key) as? Boolean ?: !config.collapsed

        val image = image {
            location = ResourceAPI.identify("textures/gui/chevron.png")
            color = CascadeGeometricColor(MochaColorScheme.Subtext0.argb)
            position = AlignPositionConstraint(PositionAlignment.END, PositionAlignment.CENTER)
            offset = CascadeGeometricOffset(-12f, 0f)
            size = FixedSizeConstraint(8f, 8f)
            rotation = if (expanded) -180f else -90f

            attach(parent)
        }

        rectangle {
            position = FixedPositionConstraint(0f, 0f)
            size = FixedSizeConstraint(parent.width.takeIf { it > 0f } ?: 280f, parent.height.takeIf { it > 0f } ?: 26f)
            color = CascadeGeometricColor.TRANSPARENT

            on<MouseEvent.Press> {
                cancel()
                if (button != InputConstants.MOUSE_BUTTON_LEFT) return@on

                expanded = !expanded
                ConfigManager.update(config.key, expanded)

                animate(250.milliseconds, EaseOutEasingInterpolator) {
                    image::rotation to (if (expanded) -180f else -90f)
                }

                function(expanded)
            }

            attach(parent)
        }
    }
}
