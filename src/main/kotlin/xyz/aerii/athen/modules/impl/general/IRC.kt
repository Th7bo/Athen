@file:Suppress("Unused")

package xyz.aerii.athen.modules.impl.general

import com.google.gson.JsonParser
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.network.protocol.game.ServerboundChatPacket
import xyz.aerii.athen.Athen
import xyz.aerii.athen.annotations.Websocket
import xyz.aerii.athen.api.websocket.SocketPacket
import xyz.aerii.athen.api.websocket.base.IWebSocket
import xyz.aerii.athen.config.Category
import xyz.aerii.athen.events.CommandRegistration
import xyz.aerii.athen.events.PacketEvent
import xyz.aerii.athen.events.core.on
import xyz.aerii.athen.events.core.runWhen
import xyz.aerii.athen.handlers.Typo
import xyz.aerii.athen.handlers.Typo.modMessage
import xyz.aerii.athen.modules.Module
import xyz.aerii.athen.ui.themes.Catppuccin
import xyz.aerii.library.api.center
import xyz.aerii.library.api.lie
import xyz.aerii.library.api.name
import xyz.aerii.library.api.repeat
import xyz.aerii.library.handlers.Observable
import xyz.aerii.library.handlers.Observable.Companion.and
import xyz.aerii.library.handlers.parser.parse

@Websocket
object IRC : IWebSocket() {
    private val ob: Observable<Boolean> = Observable(false)
    private var cc: String = "general"

    @JvmStatic
    val a = object : Module(
        "IRC",
        "Enables the IRC by default on launch if the module is enabled.",
        Category.GENERAL,
        true
    ) {}

    private val _unused by a.config.textParagraph("Run <red>\"/athen irc help\" <r>to view all commands!")
    private val help by a.config.switch("Help message", true)
    private val format0 by a.config.textInput("Message format", "<#A6E3A1>#name <dark_gray>➤ <white>#message")
    private val discord by a.config.switch("Discord IRC", true)
    private val format1 by a.config.textInput("Discord format", "<#A6E3A1>#name <dark_gray>➤ <white>#message").dependsOn { discord }

    init {
        on<CommandRegistration> {
            event.register("airc") {
                thenCallback("message", StringArgumentType.greedyString()) {
                    send(StringArgumentType.getString(this@thenCallback, "message"))
                }

                thenCallback("toggle") {
                    val b = !ob.value
                    ob.value = b
                    "Send all messages to IRC <gray>➤ ${if (b) "<green>Enabled" else "<red>Disabled"}".parse().modMessage()
                }
            }

            event.register(Athen.modId) {
                then("irc") {
                    then("create") {
                        then("channel", StringArgumentType.string()) {
                            callback {
                                if (!auth) return@callback "Not connected to IRC! Use <yellow>/${Athen.modId} irc connect".parse().modMessage(Typo.PrefixType.ERROR)
                                create(StringArgumentType.getString(this, "channel"))
                            }

                            thenCallback("pin", StringArgumentType.string()) {
                                if (!auth) return@thenCallback "Not connected to IRC! Use <yellow>/${Athen.modId} irc connect".parse().modMessage(Typo.PrefixType.ERROR)
                                create(StringArgumentType.getString(this, "channel"), StringArgumentType.getString(this, "pin"))
                            }
                        }
                    }

                    then("join") {
                        then("channel", StringArgumentType.string()) {
                            callback {
                                if (!auth) return@callback "Not connected to IRC! Use <yellow>/${Athen.modId} irc connect".parse().modMessage(Typo.PrefixType.ERROR)
                                join(StringArgumentType.getString(this, "channel"))
                            }

                            thenCallback("pin", StringArgumentType.string()) {
                                if (!auth) return@thenCallback "Not connected to IRC! Use <yellow>/${Athen.modId} irc connect".parse().modMessage(Typo.PrefixType.ERROR)
                                join(StringArgumentType.getString(this, "channel"), StringArgumentType.getString(this, "pin"))
                            }
                        }
                    }

                    then("pin") {
                        thenCallback("pin", StringArgumentType.string()) {
                            if (!auth) return@thenCallback "Not connected to IRC!".modMessage(Typo.PrefixType.ERROR)
                            pin(StringArgumentType.getString(this, "pin"))
                        }
                    }

                    thenCallback("leave") {
                        if (!auth) return@thenCallback "Not connected to IRC!".modMessage(Typo.PrefixType.ERROR)
                        leave()
                    }

                    then("chat") {
                        thenCallback("message", StringArgumentType.greedyString()) {
                            if (!auth) return@thenCallback "Not connected to IRC!".modMessage(Typo.PrefixType.ERROR)
                            send(StringArgumentType.getString(this, "message"))
                        }
                    }

                    thenCallback("list") {
                        if (!auth) return@thenCallback "Not connected to IRC!".modMessage(Typo.PrefixType.ERROR)
                        list()
                    }

                    thenCallback("help") {
                        help()
                    }
                }
            }
        }

        on<PacketEvent.Send, ServerboundChatPacket> {
            if (message.startsWith('/')) return@on
            send(message)
            it.cancel()
        }.runWhen(ob and a.observable)
    }

    override fun fn0(t: Int, c: String?, n: String?, b: String?) {
        if (t !in SocketPacket.IRC.ClientBound.all) return

        when (t) {
            SocketPacket.IRC.ClientBound.Join.id -> {
                if (c == null) return

                cc = c
                "<gray>Joined channel <aqua>#$c".parse().modMessage()
                if (help) "<gray>Need help? Run <red>\"/athen irc help\"<r>!".parse().modMessage()
            }

            SocketPacket.IRC.ClientBound.Left.id -> {
                if (c == null) return
                if (cc == c) cc = "general"

                "<gray>Left channel <aqua>#$c".parse().modMessage()
            }

            SocketPacket.IRC.ClientBound.Chat.id -> {
                if (c == null) return
                if (n == null) return
                if (b == null) return
                if (n == name) return
                if (n == "[Discord]") return

                "<dark_gray>[<aqua>#$c<dark_gray>]".format0(n, b).parse().modMessage()
            }

            SocketPacket.IRC.ClientBound.Discord.id -> {
                if (!discord) return
                if (n == null) return
                if (b == null) return

                "<dark_gray>[<aqua>Discord<dark_gray>]".format1(n, b).parse().modMessage()
            }

            SocketPacket.IRC.ClientBound.Error.id -> {
                "<red>IRC error: <gray>$b".parse().modMessage(Typo.PrefixType.ERROR)
            }

            SocketPacket.IRC.ClientBound.Warn.id -> {
                if (b != null) "<yellow>IRC: <gray>$b".parse().modMessage(Typo.PrefixType.ERROR)
            }

            SocketPacket.IRC.ClientBound.List.id -> {
                if (b == null) return
                val ch = runCatching {
                    JsonParser.parseString(b).asJsonArray.map {
                        val arr = it.asJsonArray
                        "${arr[0].asString} (${arr[1].asInt})"
                    }.sortedWith(compareBy({ if (it.startsWith("general ")) 0 else 1 }, { it }))
                }.getOrNull() ?: return

                if (ch.isEmpty()) "<gray>No active channels.".parse().modMessage()
                else "<gray>Active channels: <aqua>${ch.joinToString("<dark_gray>, <aqua>") { ch -> "#$ch" }}".parse().modMessage()
            }
        }
    }

    override fun fn1(): Observable<Boolean> {
        return a.observable
    }

    private fun create(channel: String, pin: String? = null) {
        `socket$send`(SocketPacket.IRC.ServerBound.Create.id, "c" to channel, "p" to pin)
    }

    private fun pin(pin: String) {
        `socket$send`(SocketPacket.IRC.ServerBound.Pin.id, "p" to pin)
    }

    private fun join(channel: String, pin: String? = null) {
        `socket$send`(SocketPacket.IRC.ServerBound.Join.id, "c" to channel, "p" to pin)
    }

    private fun leave() {
        `socket$send`(SocketPacket.IRC.ServerBound.Leave.id)
    }

    private fun list() {
        `socket$send`(SocketPacket.IRC.ServerBound.List.id)
    }

    private fun send(body: String) {
        `socket$send`(SocketPacket.IRC.ServerBound.Chat.id, "b" to body)

        "<dark_gray>[<aqua>#$cc<dark_gray>]".format0(name, body).parse(true).modMessage()
    }

    private fun String.format0(n: String, b: String): String {
        return "$this " + format0.replace("#name", n).replace("#message", b)
    }

    private fun String.format1(n: String, b: String): String {
        return "$this " + format1.replace("#name", n).replace("#message", b)
    }

    private fun help() {
        val a = ("<dark_gray>" + ("-".repeat())).parse()
        val b = Athen.modId
        val c = Catppuccin.Mocha.Green.argb

        a.lie()
        ("<red>" + ("Athen IRC".center())).parse().lie()
        a.lie()

        " <dark_gray>- <$c>/$b irc create [channel] [pin <gray>- optional<$c>]".parse().lie()
        " <dark_gray>- <$c>/$b irc join [channel] [pin <gray>- optional<$c>]".parse().lie()
        " <dark_gray>- <$c>/$b irc leave <gray>- leave channel".parse().lie()
        " <dark_gray>- <$c>/$b irc pin [pin] <gray>- sets a pin".parse().lie()
        " <dark_gray>- <$c>/$b irc chat [message]".parse().lie()
        " <dark_gray>- <$c>/$b irc list <gray>- list channels".parse().lie()

        a.lie()

        " <dark_gray>- <$c>/airc [message] <gray>- send message alias".parse().lie()
        " <dark_gray>- <$c>/airc toggle <gray>- send all messages to irc".parse().lie()

        a.lie()
    }
}