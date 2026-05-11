package xyz.aerii.athen.api.rendering.level.impl.data.impl

import net.minecraft.core.BlockPos
import xyz.aerii.athen.api.rendering.level.impl.data.base.ILevelExtractable

data class ExtractedBeam(
    val pos: BlockPos,
    val color: Int
) : ILevelExtractable