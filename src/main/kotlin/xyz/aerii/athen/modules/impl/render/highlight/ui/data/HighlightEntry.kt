package xyz.aerii.athen.modules.impl.render.highlight.ui.data

import xyz.aerii.athen.ui.base.IEntryView

data class HighlightEntry(
    override val index: Int,
    val name: String,
    val color: Int,
    val max: Int,
    val typed: Boolean
) : IEntryView