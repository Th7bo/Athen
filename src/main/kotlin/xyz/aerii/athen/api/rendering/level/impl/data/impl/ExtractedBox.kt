package xyz.aerii.athen.api.rendering.level.impl.data.impl

import net.minecraft.world.phys.AABB
import xyz.aerii.athen.api.rendering.level.impl.data.base.ILevelExtractable

data class ExtractedBox(
    val aabb: AABB,
    val color: Int,
    val width: Float
) : ILevelExtractable