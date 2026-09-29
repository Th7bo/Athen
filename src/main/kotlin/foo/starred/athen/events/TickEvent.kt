package foo.starred.athen.events

import foo.starred.athen.api.scheduling.Scheduler
import foo.starred.athen.events.core.AthenEvent

sealed class TickEvent {
    sealed class Client {
        data object Start : AthenEvent()

        data object End : AthenEvent() {
            val ticks: Int
                get() = Scheduler.ticks.client
        }
    }

    data object Server : AthenEvent() {
        val ticks: Int
            get() = Scheduler.ticks.server
    }
}
