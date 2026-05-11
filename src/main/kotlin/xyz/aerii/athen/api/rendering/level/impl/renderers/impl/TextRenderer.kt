package xyz.aerii.athen.api.rendering.level.impl.renderers.impl

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.MultiBufferSource
import xyz.aerii.athen.api.rendering.level.impl.queue.impl.LevelQueueImpl
import xyz.aerii.athen.api.rendering.level.impl.renderers.base.ILevelRenderer
import xyz.aerii.athen.api.rendering.level.internal.annotations.impl.LevelRenderer
import xyz.aerii.library.api.client

//? if >= 26.1 {
/*import net.minecraft.util.LightCoordsUtil
*///? } else {
import net.minecraft.client.renderer.LightTexture
//? }

@LevelRenderer
object TextRenderer : ILevelRenderer {
    override fun render(poseStack: PoseStack, pose: PoseStack.Pose, consumers: MultiBufferSource.BufferSource) {
        fn(poseStack, consumers)
    }

    private fun fn(poseStack: PoseStack, consumers: MultiBufferSource.BufferSource) {
        val cam = client.gameRenderer.mainCamera

        for (text in LevelQueueImpl.texts) {
            poseStack.pushPose()

            val pose = poseStack.last().pose()
            val scale = text.scale * 0.025f

            pose.translate(text.pos.x.toFloat(), text.pos.y.toFloat(), text.pos.z.toFloat())
                .rotate(cam.rotation())
                .scale(scale, -scale, scale)

            client.font.drawInBatch(
                text.text,
                -client.font.width(text.text) / 2f,
                0f,
                text.color0,
                text.shadow,
                pose,
                consumers,
                if (text.depth) Font.DisplayMode.NORMAL else Font.DisplayMode.SEE_THROUGH,
                text.color1,
                //~ if >= 26.1 'LightTexture' -> 'LightCoordsUtil'
                LightTexture.FULL_BRIGHT
            )

            poseStack.popPose()
        }
    }
}