package foo.starred.athen.events

import foo.starred.athen.events.core.AthenEvent
import foo.starred.kbus.data.event.traits.KBusCancellableTrait
import net.minecraft.core.BlockPos
import net.minecraft.world.item.ItemStack

sealed class PlayerEvent {
    data class Drop(
        val item: ItemStack?,
        val gui: Boolean
    ) : AthenEvent(), KBusCancellableTrait

    sealed class Interact {
        data object None : AthenEvent(), KBusCancellableTrait

        data class Block(val pos: BlockPos)  : AthenEvent(), KBusCancellableTrait

        data class Entity(val entity: net.minecraft.world.entity.Entity) : AthenEvent(), KBusCancellableTrait

        data object Any : AthenEvent(), KBusCancellableTrait
    }

    sealed class Attack {
        data class Block(val pos: BlockPos) : AthenEvent(), KBusCancellableTrait

        data class Entity(val entity: net.minecraft.world.entity.Entity) : AthenEvent(), KBusCancellableTrait

        data object Any : AthenEvent(), KBusCancellableTrait
    }
}
