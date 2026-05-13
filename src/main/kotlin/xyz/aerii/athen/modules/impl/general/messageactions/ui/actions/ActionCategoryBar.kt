package xyz.aerii.athen.modules.impl.general.messageactions.ui.actions

import xyz.aerii.athen.modules.impl.general.messageactions.MessageActions
import xyz.aerii.athen.modules.impl.general.messageactions.ui.UIZoneType
import xyz.aerii.athen.ui.IZoneType
import xyz.aerii.athen.ui.base.AbstractCategoryBar
import xyz.aerii.athen.ui.base.ICategoryEntry

class ActionCategoryBar(
    height0: Int,
    height1: Int
) : AbstractCategoryBar(height0, height1) {
    override val zone0: IZoneType = UIZoneType.CATEGORY_TAB
    override val zone1: IZoneType = UIZoneType.CATEGORY_TOGGLE
    override val zone2: IZoneType = UIZoneType.CATEGORY_ADD

    override fun categories(): List<ICategoryEntry> {
        return MessageActions.categories
    }

    override fun addCategory(name: String) {
        MessageActions.add(name)
    }
}