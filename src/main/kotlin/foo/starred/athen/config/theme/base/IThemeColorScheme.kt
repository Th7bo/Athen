package foo.starred.athen.config.theme.base

import foo.starred.snowbird.utils.withAlpha

interface IThemeColorScheme {
    val rgba: Int
    val argb: Int

    fun alpha(alpha: Float, rgba: Boolean = false): Int =
        if (rgba) this.rgba.withAlpha(alpha, rgba = true)
        else this.argb.withAlpha(alpha, rgba = false)
}
