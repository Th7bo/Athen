package foo.starred.athen.api.rendering.level.pipelines.impl

import foo.starred.athen.api.rendering.level.pipelines.base.ILevelPipeline
import foo.starred.athen.api.rendering.level.pipelines.base.LevelPipelineBuilder
import foo.starred.athen.api.rendering.level.pipelines.depth.LevelPipelineDepth
import net.minecraft.client.renderer.RenderPipelines

//~ if >= 26.3 'blaze3d' -> 'renderpearl.api'
import com.mojang.blaze3d.pipeline.RenderPipeline

class DualLevelPipeline(identifier: String, block: LevelPipelineBuilder.() -> Unit) : ILevelPipeline {
    override val depth: RenderPipeline = RenderPipelines.register(
        LevelPipelineBuilder().apply(block).apply {
            location = "depth/$identifier"
        }.build()
    )

    override val depthless: RenderPipeline = RenderPipelines.register(
        LevelPipelineBuilder().apply(block).apply {
            location = "depthless/$identifier"
            depth = LevelPipelineDepth.NONE
        }.build()
    )
}
