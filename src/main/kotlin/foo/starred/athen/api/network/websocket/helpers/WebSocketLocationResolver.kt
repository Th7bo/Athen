package foo.starred.athen.api.network.websocket.helpers

import foo.starred.athen.annotations.Load
import foo.starred.athen.api.location.LocationAPI
import foo.starred.athen.api.network.websocket.base.IWebSocket
import foo.starred.athen.api.network.websocket.data.SocketPacket
import foo.starred.athen.api.scheduling.Scheduler
import foo.starred.athen.events.InternalEvent
import foo.starred.athen.events.LocationEvent
import foo.starred.athen.events.core.on
import foo.starred.snowbird.api.scheduling.scheduler.data.tasks.base.SchedulerTask
import kotlin.time.Duration.Companion.milliseconds

@Load
object WebSocketLocationResolver : IWebSocket {
    private var pending: String? = null
    private var task: SchedulerTask? = null
    private var last: Long = 0L

    init {
        on<InternalEvent.WebSocket.Auth> {
            fn(LocationAPI.id ?: return@on)
        }

        on<LocationEvent.Hypixel.Server> {
            fn(name)
        }

        on<LocationEvent.Server.Disconnect> {
            task?.cancel()
            task = null
            pending = null
        }
    }

    private fun fn(id: String) {
        if (!auth) return

        val now = System.currentTimeMillis()
        if (now - last >= 10_000) {
            task?.cancel()
            task = null
            last = now
            pending = null
            `socket$send`(SocketPacket.WebSocket.ServerBound.Location.id, "b" to id)
            return
        }

        pending = id
        if (task != null) return
        task = Scheduler.schedule((10_000 - (now - last)).milliseconds) {
            task = null
            val pending0 = pending ?: return@schedule
            pending = null
            last = System.currentTimeMillis()
            `socket$send`(SocketPacket.WebSocket.ServerBound.Location.id, "b" to pending0)
        }
    }
}
