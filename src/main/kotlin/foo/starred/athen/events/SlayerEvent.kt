package foo.starred.athen.events

import foo.starred.athen.api.slayers.data.SlayerInfo
import foo.starred.athen.events.core.AthenEvent
import net.minecraft.world.entity.Entity

sealed class SlayerEvent {
    sealed class Boss {
        data class Spawn(
            val entity: Entity,
            val slayerInfo: SlayerInfo
        ) : AthenEvent()

        data class Death(
            val entity: Entity,
            val slayerInfo: SlayerInfo
        ) : AthenEvent()
    }

    sealed class Miniboss {
        data class Spawn(
            val entity: Entity,
            val slayerInfo: SlayerInfo
        ) : AthenEvent()

        data class Death(
            val entity: Entity,
            val slayerInfo: SlayerInfo
        ) : AthenEvent()
    }

    sealed class Demon {
        data class Spawn(
            val entity: Entity,
            val slayerInfo: SlayerInfo
        ) : AthenEvent()

        data class Death(
            val entity: Entity,
            val slayerInfo: SlayerInfo
        ) : AthenEvent()
    }

    sealed class Quest {
        data object Start : AthenEvent()

        data object End : AthenEvent()
    }

    sealed class Reset : AthenEvent() {
        data object QuestFail : Reset()

        data object ServerChange : Reset()

        data object Any : Reset()
    }
}
