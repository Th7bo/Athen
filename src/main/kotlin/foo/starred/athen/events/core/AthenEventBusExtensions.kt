package foo.starred.athen.events.core

import foo.starred.athen.Athen
import foo.starred.athen.events.PacketEvent
import foo.starred.kbus.data.event.base.KBusEvent
import foo.starred.kbus.data.node.KBusNode
import foo.starred.kbus.extensions.on
import net.minecraft.network.protocol.Packet

inline fun <reified T : KBusEvent> on(priority: Int = 0, noinline handler: T.() -> Unit): KBusNode<T> {
    return Athen.BUS.on(priority, handler)
}

inline fun <reified E : PacketEvent, reified P : Packet<*>> on(priority: Int = 0, noinline handler: P.(E) -> Unit): KBusNode<*> {
    return on<E>(priority) {
        (packet as? P)?.handler(this)
    }
}
