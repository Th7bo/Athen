package foo.starred.athen.events

import foo.starred.athen.events.core.AthenEvent
import foo.starred.kbus.data.event.traits.KBusUnconditionalTrait

sealed class GameEvent {
    data object Start : AthenEvent(), KBusUnconditionalTrait

    data object Stop : AthenEvent(), KBusUnconditionalTrait
}
