package xyz.aerii.athen.modules.impl.general.keybinds.ui

import xyz.aerii.athen.modules.impl.general.keybinds.Keybinds
import xyz.aerii.athen.ui.base.IEntryView

data class BindingEntry(override val index: Int, val binding: Keybinds.KeybindEntry) : IEntryView {
    var condition = binding.condition.copy()
    var toggleAnim = if (binding.enabled) 1f else 0f
}
