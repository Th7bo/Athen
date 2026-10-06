package foo.starred.athen.events

import foo.starred.athen.events.core.AthenEvent
import foo.starred.kbus.data.event.traits.KBusUnconditionalTrait

sealed class InternalEvent {
    sealed class WebSocket {
        data class Message(
            val id: Int,
            val body: String?,
            val channel: String?,
            val name: String?
        ) : AthenEvent()

        data object Auth : AthenEvent()
    }

    sealed class Mod {
        sealed class Loading {
            data object Start : AthenEvent(), KBusUnconditionalTrait

            data object End : AthenEvent(), KBusUnconditionalTrait
        }
    }
}
