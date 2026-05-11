@file:Suppress("Unused")

package xyz.aerii.athen.api.rendering.level.rendertypes

import xyz.aerii.athen.annotations.Load
import xyz.aerii.athen.api.rendering.level.pipelines.LevelPipelineImpl
import xyz.aerii.athen.api.rendering.level.rendertypes.impl.DualLevelRenderType

@Load
object LevelRenderTypeImpl {
    val LINES = DualLevelRenderType("lines", LevelPipelineImpl.LINES)
    val DEBUG_FILLED = DualLevelRenderType("debug_filled", LevelPipelineImpl.DEBUG_FILLED)
    val TRIANGLE_FAN = DualLevelRenderType("triangle_fan", LevelPipelineImpl.TRIANGLE_FAN)
}