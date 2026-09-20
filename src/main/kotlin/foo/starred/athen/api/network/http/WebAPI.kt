package foo.starred.athen.api.network.http

import foo.starred.athen.Athen
import foo.starred.athen.api.minecraft.mod.ModWrapper
import foo.starred.snowbird.api.network.WebUtils

object WebAPI : WebUtils(ModWrapper.name, Athen.LOGGER)
