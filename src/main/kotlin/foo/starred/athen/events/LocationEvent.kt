package foo.starred.athen.events

import foo.starred.athen.api.location.area.base.ISkyBlockArea
import foo.starred.athen.api.location.island.base.ISkyBlockIsland
import foo.starred.athen.events.core.Event
import net.hypixel.data.type.ServerType

sealed class LocationEvent {
    sealed class Hypixel {
        data class Server(
            val name: String,
            val type: ServerType?,
            val lobby: String?,
            val mode: String?,
            val map: String?,
        ) : Event()
    }

    sealed class SkyBlock {
        data class Island(
            val old: ISkyBlockIsland,
            val new: ISkyBlockIsland
        ) : Event()

        data class Area(
            val old: ISkyBlockArea,
            val new: ISkyBlockArea
        ) : Event()

        data object Connect : Event()

        data object Disconnect : Event()
    }

    sealed class Server : Event() {
        data object Connect : Server()

        data object Disconnect : Server()
    }
}
