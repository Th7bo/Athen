package xyz.aerii.athen.modules.impl.general.messageactions.ui.actions

import xyz.aerii.athen.modules.impl.general.messageactions.data.ActionEntry
import xyz.aerii.athen.ui.base.IEntryView

data class ActionEntryView(override val index: Int, val entry: ActionEntry) : IEntryView {
    var toggle = if (entry.enabled) 1f else 0f
}