package xyz.aerii.athen.utils

import xyz.aerii.athen.Athen
import xyz.aerii.library.kommand.ICommand
import xyz.aerii.library.kommand.dsl.BuilderScope

fun command(block: BuilderScope.() -> Unit) {
    Command.command(Athen.modId, block)
}

private object Command : ICommand