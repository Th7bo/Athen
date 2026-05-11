package xyz.aerii.athen.api.rendering.level.rendertypes.impl

import xyz.aerii.athen.api.rendering.level.pipelines.base.ILevelPipeline
import xyz.aerii.athen.api.rendering.level.rendertypes.base.ILevelRenderType

//? if >= 1.21.11 {
/*import net.minecraft.client.renderer.rendertype.RenderSetup
import net.minecraft.client.renderer.rendertype.RenderType
*///? } else {
import net.minecraft.client.renderer.RenderType
//? }

class DualLevelRenderType(identifier: String, pipeline: ILevelPipeline) : ILevelRenderType {
    override val depth: RenderType =
    //? if >= 1.21.11 {
    /*RenderType.create(
        "starred_$identifier",
        RenderSetup.builder(pipeline.depth)
            .bufferSize(RenderType.TRANSIENT_BUFFER_SIZE)
            .createRenderSetup()
    )
    *///? } else {
        RenderType.create(
            "starred_$identifier",
            RenderType.TRANSIENT_BUFFER_SIZE,
            false,
            true,
            pipeline.depth,
            RenderType.CompositeState.builder().createCompositeState(false)
        )
    //? }

    override val depthless: RenderType =
    //? if >= 1.21.11 {
    /*RenderType.create(
        "starred_${identifier}_depthless",
        RenderSetup.builder(pipeline.depthless)
            .bufferSize(RenderType.TRANSIENT_BUFFER_SIZE)
            .createRenderSetup()
    )
    *///? } else {
        RenderType.create(
            "starred_${identifier}_depthless",
            RenderType.TRANSIENT_BUFFER_SIZE,
            false,
            true,
            pipeline.depthless,
            RenderType.CompositeState.builder().createCompositeState(false)
        )
    //? }
}