package foo.starred.athen.events

import com.mojang.blaze3d.vertex.PoseStack
import foo.starred.athen.events.core.AthenEvent
import foo.starred.kbus.data.event.traits.KBusCancellableTrait
import net.minecraft.client.renderer.entity.state.EntityRenderState
import net.minecraft.client.renderer.state.level.CameraRenderState

//~ if >= 26.2 'MultiBufferSource' -> 'SubmitNodeCollector'
import net.minecraft.client.renderer.MultiBufferSource

sealed class WorldRenderEvent {
    class Entity(
        val renderState: EntityRenderState,
        val poseStack: PoseStack,
        val cameraRenderState: CameraRenderState,
        val entity: net.minecraft.world.entity.Entity?
    ) : AthenEvent(), KBusCancellableTrait

    data object Extract : AthenEvent()

    data class Render(
        val pose: PoseStack,
        //~ if >= 26.2 'MultiBufferSource.BufferSource' -> 'SubmitNodeCollector'
        val consumers: MultiBufferSource.BufferSource
    ) : AthenEvent()
}
