package xyz.aerii.athen.api.rendering.level.rendertypes.base

//? if >= 1.21.11 {
/*import net.minecraft.client.renderer.rendertype.RenderType
*///? } else {
import net.minecraft.client.renderer.RenderType
//? }

interface ILevelRenderType {
    val depth: RenderType
    val depthless: RenderType
}