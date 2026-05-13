package xyz.aerii.athen.modules.impl.general.messageactions.data

import xyz.aerii.athen.ui.base.ICategoryEntry

data class CategoryEntry(
    override val name: String,
    override val enabled: Boolean = true
) : ICategoryEntry