package xyz.aerii.athen.events

import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import xyz.aerii.athen.events.core.Event

@Deprecated("Use xyz.aerii.library.kommand.ICommand")
data class CommandRegistration(
    val event: RegisterCommandsEvent
) : Event()
