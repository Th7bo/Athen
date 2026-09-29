package foo.starred.athen.api.rendering.level.pipelines.base

import foo.starred.athen.api.minecraft.mod.ModWrapper
import foo.starred.athen.api.rendering.level.pipelines.depth.LevelPipelineDepth
import kotlin.jvm.optionals.getOrNull

//~ if >= 26.3 'blaze3d' -> 'renderpearl.api'
import com.mojang.blaze3d.pipeline.RenderPipeline
//~ if >= 26.3 'blaze3d' -> 'renderpearl.api'
import com.mojang.blaze3d.vertex.VertexFormat
//? if >= 26.3 {
/*import com.mojang.renderpearl.api.pipeline.BlendFunction
import com.mojang.renderpearl.api.pipeline.ColorTargetState
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology
*///?} elif 26.2 {
/*import com.mojang.blaze3d.PrimitiveTopology
*///?}

class LevelPipelineBuilder {
    lateinit var snippet: RenderPipeline.Snippet
    lateinit var location: String

    var depth: LevelPipelineDepth = LevelPipelineDepth.LEQUAL
    var cull: Boolean = true

    var vertexFormat: VertexFormat? = null
    //~ if >= 26.2 'VertexFormat.Mode' -> 'PrimitiveTopology'
    var vertexMode: VertexFormat.Mode? = null
    //? if >= 26.3
    //var colorTargetState: ColorTargetState? = null

    fun build(): RenderPipeline {
        //~ if >= 26.2 'snippet.vertexFormat.getOrNull()' -> 'snippet.vertexFormatPerBuffer()?.firstOrNull()'
        if (vertexFormat == null) vertexFormat = snippet.vertexFormat.getOrNull()
        //~ if >= 26.2 'vertexFormatMode' -> 'vertexFormatMode()'
        if (vertexMode == null) vertexMode = snippet.vertexFormatMode.getOrNull()

        val a = RenderPipeline.builder(snippet).withLocation("${ModWrapper.id}/$location")
        val b = vertexFormat
        if (b != null) {
            //? if >= 26.2 {
            /*a.withVertexBinding(0, b)
            a.withPrimitiveTopology(vertexMode ?: PrimitiveTopology.QUADS)
            *///? } else
            a.withVertexFormat(b, vertexMode ?: VertexFormat.Mode.QUADS)
        }

        depth.build(a)

        if (!cull) a.withCull(false)
        //? if >= 26.3 {
        /*val color0 = colorTargetState ?: if (snippet.activeColorTargetStateCount() == 0) ColorTargetState(BlendFunction.TRANSLUCENT) else null
        if (color0 != null) a.withColorTargetState(color0)
        *///?}
        return a.build()
    }
}
