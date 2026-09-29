package foo.starred.athen.events

import foo.starred.athen.events.core.AthenEvent
import foo.starred.kbus.data.event.traits.KBusCancellableTrait
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonInfo

sealed class InputEvent {
    sealed class Keyboard {
        data class Press(
            val keyEvent: KeyEvent
        ) : AthenEvent(), KBusCancellableTrait

        data class Release(
            val keyEvent: KeyEvent
        ) : AthenEvent()
    }

    sealed class Mouse {
        data class Press(
            val buttonInfo: MouseButtonInfo
        ) : AthenEvent(), KBusCancellableTrait

        data class Release(
            val buttonInfo: MouseButtonInfo
        ) : AthenEvent()

        data class Move(
            val x: Double,
            val y: Double
        ) : AthenEvent(), KBusCancellableTrait
    }
}
