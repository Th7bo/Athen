@file:Suppress("Unused")

package xyz.aerii.athen.api.rendering.level.impl.extensions.impl

import net.minecraft.core.BlockPos
import xyz.aerii.athen.api.rendering.level.impl.data.impl.ExtractedBeam
import xyz.aerii.athen.api.rendering.level.impl.queue.impl.LevelQueueImpl

fun extractBeam(
    pos: BlockPos,
    color: Int
) {
    LevelQueueImpl.beams.add(ExtractedBeam(pos, color))
}