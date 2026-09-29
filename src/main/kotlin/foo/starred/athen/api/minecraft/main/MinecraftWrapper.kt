package foo.starred.athen.api.minecraft.main

import foo.starred.athen.events.GameEvent
import foo.starred.athen.events.core.on

object MinecraftWrapper {
    var loaded: Boolean = false
        private set

    init {
        on<GameEvent.Start> {
            loaded = true
        }
    }
}
