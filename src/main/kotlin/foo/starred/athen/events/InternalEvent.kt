package foo.starred.athen.events

import foo.starred.athen.events.core.Event

sealed class InternalEvent {
    sealed class WebSocket {
        data class Message(
            val id: Int,
            val body: String?,
            val channel: String?,
            val name: String?
        ) : Event()
    }

    sealed class Mod {
        sealed class Loading {
            data object Start : Event()

            data object End : Event()
        }
    }
}
