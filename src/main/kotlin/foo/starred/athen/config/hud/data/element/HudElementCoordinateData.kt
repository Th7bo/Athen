package foo.starred.athen.config.hud.data.element

data class HudElementCoordinateData(
    var x: Float = 20f,
    var y: Float = 20f,
    var width: Float = 0f,
    var height: Float = 0f,
    var scale: Float = 1f
) {
    val x1: Float
        get() = x + width

    val y1: Float
        get() = y + height
}
