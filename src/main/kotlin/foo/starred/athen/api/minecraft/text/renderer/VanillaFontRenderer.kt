package foo.starred.athen.api.minecraft.text.renderer

import foo.starred.athen.api.minecraft.text.measurer.VanillaFontMeasurer
import foo.starred.snowbird.api.client
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.locale.Language
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FormattedText
import net.minecraft.util.FormattedCharSequence

object VanillaFontRenderer {
    private val font: Font
        get() = client.font

    fun extract(graphics: GuiGraphicsExtractor, string: String, x: Int = 0, y: Int = 0, shadow: Boolean = true, color: Int = -1, center: Boolean = false) {
        val x1 = if (center) x - VanillaFontMeasurer.width(string) / 2 else x

        graphics.text(font, string, x1, y, color, shadow)
    }

    fun extract(graphics: GuiGraphicsExtractor, component: Component, x: Int = 0, y: Int = 0, shadow: Boolean = true, color: Int = -1, center: Boolean = false) {
        val x1 = if (center) x - VanillaFontMeasurer.width(component) / 2 else x

        graphics.text(font, component, x1, y, color, shadow)
    }

    fun extract(graphics: GuiGraphicsExtractor, formatted: FormattedCharSequence, x: Int = 0, y: Int = 0, shadow: Boolean = true, color: Int = -1, center: Boolean = false) {
        val x1 = if (center) x - VanillaFontMeasurer.width(formatted) / 2 else x

        graphics.text(font, formatted, x1, y, color, shadow)
    }

    @JvmName("extract_string_multi")
    fun extract(graphics: GuiGraphicsExtractor, strings: List<String>, x: Int = 0, y: Int = 0, shadow: Boolean = true, color: Int = -1, spacing: Int = 2, center: List<Int> = emptyList()) {
        extract(graphics, strings.map { Language.getInstance().getVisualOrder(FormattedText.of(it)) }, x, y, shadow, color, spacing, center)
    }

    @JvmName("extract_component_multi")
    fun extract(graphics: GuiGraphicsExtractor, components: List<Component>, x: Int = 0, y: Int = 0, shadow: Boolean = true, color: Int = -1, spacing: Int = 2, center: List<Int> = emptyList()) {
        extract(graphics, components.map { it.visualOrderText }, x, y, shadow, color, spacing, center)
    }

    @JvmName("extract_fcs_multi")
    fun extract(graphics: GuiGraphicsExtractor, formatted: List<FormattedCharSequence>, x: Int = 0, y: Int = 0, shadow: Boolean = true, color: Int = -1, spacing: Int = 2, center: List<Int> = emptyList()) {
        val widths = VanillaFontMeasurer.width(formatted)
        val height = VanillaFontMeasurer.height
        val max = widths.maxOrNull() ?: 0

        for (i in formatted.indices) {
            val x1 = if (i in center) x + (max - widths[i]) / 2 else x
            val y1 = y + i * (height + spacing)

            graphics.text(font, formatted[i], x1, y1, color, shadow)
        }
    }
}
