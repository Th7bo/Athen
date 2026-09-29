package foo.starred.athen.compat.modmenu

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import foo.starred.athen.config.ui.ConfigUI
import net.minecraft.client.gui.screens.Screen

object ModMenuCompatibility : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
        return ConfigScreenFactory { _: Screen? ->
            ConfigUI
        }
    }
}
