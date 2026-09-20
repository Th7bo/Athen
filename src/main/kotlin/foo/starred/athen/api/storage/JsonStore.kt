package foo.starred.athen.api.storage

import foo.starred.athen.api.minecraft.mod.ModWrapper
import foo.starred.snowbird.api.storage.AbstractJsonStore

class JsonStore(path: String, tts: Int = 15) : AbstractJsonStore(ModWrapper.id, path, tts)
