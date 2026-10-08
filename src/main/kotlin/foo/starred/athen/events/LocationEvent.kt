package foo.starred.athen.events

import foo.starred.athen.api.location.area.base.ISkyBlockArea
import foo.starred.athen.api.location.island.base.ISkyBlockIsland
import foo.starred.athen.events.core.AthenEvent
import foo.starred.kbus.data.event.traits.KBusUnconditionalTrait
import net.hypixel.data.type.ServerType

sealed class LocationEvent {
    sealed class Hypixel {
        data class Server(
            val name: String,
            val type: ServerType?,
            val lobby: String?,
            val mode: String?,
            val map: String?,
        ) : AthenEvent()
    }

    sealed class SkyBlock {
        data class Island(
            val old: ISkyBlockIsland,
            val new: ISkyBlockIsland
        ) : AthenEvent(), KBusUnconditionalTrait

        data class Area(
            val old: ISkyBlockArea,
            val new: ISkyBlockArea
        ) : AthenEvent(), KBusUnconditionalTrait

        data object Connect : AthenEvent()

        data object Disconnect : AthenEvent()
    }

    sealed class Server {
        data object Connect : AthenEvent(), KBusUnconditionalTrait

        data object Disconnect : AthenEvent(), KBusUnconditionalTrait
    }
}
