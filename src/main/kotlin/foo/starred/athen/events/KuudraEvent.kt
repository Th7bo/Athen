package foo.starred.athen.events

import foo.starred.athen.api.kuudra.enums.KuudraPhase
import foo.starred.athen.events.core.AthenEvent
import foo.starred.kbus.data.event.traits.KBusCancellableTrait

sealed class KuudraEvent {
    data object Start : AthenEvent()

    sealed class End {
        data object Success : AthenEvent()

        data object Defeat : AthenEvent()

        data object Any : AthenEvent()
    }

    sealed class Supply {
        data class Progress(
            val progress: Int,
            val message: String
        ) : AthenEvent(), KBusCancellableTrait

        data object Drop : AthenEvent()

        data object Pickup : AthenEvent()
    }

    sealed class Phase {
        data object Supply : AthenEvent()

        data object Build : AthenEvent()

        data object Fuel : AthenEvent()

        data object Stun : AthenEvent()

        data object DPS : AthenEvent()

        data object Skip : AthenEvent()

        data object Kill : AthenEvent()

        data class Any(val new: KuudraPhase) : AthenEvent()
    }
}
