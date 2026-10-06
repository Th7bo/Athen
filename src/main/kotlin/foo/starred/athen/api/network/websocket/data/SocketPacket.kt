package foo.starred.athen.api.network.websocket.data

object SocketPacket {
    sealed class WebSocket {
        enum class ServerBound(val id: Int) {
            Auth(100),
            Location(107)
        }

        enum class ClientBound(val id: Int) {
            AuthSuccess(500),
            AuthError(501),

            Error(502),
            Warn(503),
            Lobby(511)
        }
    }

    sealed class IRC {
        enum class ServerBound(val id: Int) {
            Create(101),
            Join(102),
            Pin(103),
            Chat(104),
            Leave(105),
            List(106)
        }

        enum class ClientBound(val id: Int) {
            Join(504),
            Left(505),
            Chat(506),
            Error(507),
            Warn(508),
            List(509),
            Discord(510);

            companion object {
                val all: Set<Int> = entries.map { it.id }.toSet()
            }
        }
    }

    sealed class Slayer {
        enum class ServerBound(val id: Int) {
            Spawn(201),
            Death(202)
        }

        enum class ClientBound(val id: Int) {
            Spawn(600),
            Death(601);

            companion object {
                val all: Set<Int> = entries.map { it.id }.toSet()
            }
        }
    }
}
