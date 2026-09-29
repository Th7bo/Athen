package foo.starred.athen.utils

import foo.starred.athen.api.minecraft.mod.ModWrapper
import foo.starred.kommand.IKommand
import foo.starred.kommand.scopes.KommandBuilderScope
import foo.starred.kommand.scopes.KommandCommandScope
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

fun command(block: KommandBuilderScope<FabricClientCommandSource>.() -> Unit) {
    Command.command(ModWrapper.id, block)
}

private object Command : IKommand<FabricClientCommandSource> {
    override val loader: KommandCommandScope<FabricClientCommandSource> = KommandCommandScope()

    init {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            loader.register(dispatcher)
        }
    }
}
