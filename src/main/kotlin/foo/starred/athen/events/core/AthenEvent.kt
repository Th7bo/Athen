package foo.starred.athen.events.core

import foo.starred.athen.Athen
import foo.starred.kbus.data.event.base.KBusEvent

abstract class AthenEvent : KBusEvent() {
    @Suppress("NOTHING_TO_INLINE")
    inline fun post(): Boolean {
        return post(Athen.BUS)
    }
}
