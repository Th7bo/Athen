package xyz.aerii.athen.modules.impl.general.messageactions.ui.actions

import xyz.aerii.athen.modules.impl.general.messageactions.data.ActionEntry

data class ActionEntryView(val index: Int, val entry: ActionEntry) {
    var toggle = if (entry.enabled) 1f else 0f
}