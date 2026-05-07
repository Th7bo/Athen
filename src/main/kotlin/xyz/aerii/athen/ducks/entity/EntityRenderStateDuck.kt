@file:Suppress("FunctionName")

package xyz.aerii.athen.ducks.entity

import net.minecraft.world.entity.Entity

interface EntityRenderStateDuck {
    fun `athen$getEntity`(): Entity?
    fun `athen$setEntity`(entity: Entity?)
}