@file:Suppress("Unused")

package xyz.aerii.athen.modules.impl.dungeon.terminals.simulator

import xyz.aerii.athen.Athen
import xyz.aerii.athen.annotations.Load
import xyz.aerii.athen.config.Category
import xyz.aerii.athen.config.ConfigManager
import xyz.aerii.athen.events.LocationEvent
import xyz.aerii.athen.events.TickEvent
import xyz.aerii.athen.events.core.override
import xyz.aerii.athen.handlers.Typo.modMessage
import xyz.aerii.athen.modules.Module
import xyz.aerii.athen.modules.impl.dungeon.terminals.simulator.base.SimulatorMenu
import xyz.aerii.athen.modules.impl.dungeon.terminals.simulator.impl.*
import xyz.aerii.library.api.client
import xyz.aerii.library.handlers.Observable
import xyz.aerii.library.kommand.ICommand

@Load
object TerminalSimulator : Module(
    "Terminal simulator",
    "Simulator terminal, terminal simulators?",
    Category.DUNGEONS
), ICommand {
    private val ipInput by config.textInput("Simulator server IP", "hypixelp3sim.zapto.org")
    private val _unused0 by config.textParagraph("The simulator server IP is optional. You can still do <red>\"/${Athen.modId} simulate terminals\"<r> to simulate.")
    private val pingInput = config.textInput("Ping", "0", "0").custom("ping")

    var ping = 0
    val s = Observable(false)
    val s0 = Observable(false)

    init {
        run {
            ping = pingInput.value.toIntOrNull() ?: return@run
        }

        pingInput.state.onChange {
            ping = it.toIntOrNull() ?: return@onChange
        }

        observable.onChange {
            SimulatorMenu.a()
            if (it) {
                "Run \"/${Athen.modId} simulate terminals ping <ping>\" to change ping!".modMessage()
                ConfigManager.updateConfig(configKey ?: return@onChange, false)
            }
        }

        command(Athen.modId) {
            "simulate" / "terminals" {
                SimulatorMenu.a()
            }

            "simulate" / "terminals" / "ping" / int("int") {
                ConfigManager.updateConfig("$configKey.ping", int("int").toString())
                "Ping set to ${ping}ms".modMessage()
            }

            "simulate" / "terminals" / "rubix" {
                RubixSimulator().a()
            }

            "simulate" / "terminals" / "color" {
                ColorSimulator().a()
            }

            "simulate" / "terminals" / "melody" {
                MelodySimulator().a()
            }

            "simulate" / "terminals" / "name" {
                NameSimulator().a()
            }

            "simulate" / "terminals" / "panes" {
                PanesSimulator().a()
            }

            "simulate" / "terminals" / "numbers" {
                NumbersSimulator().a()
            }
        }

        on<TickEvent.Client.End> {
            TickEvent.Server.post()
        }.override(s0)

        on<LocationEvent.Server.Connect> {
            s0.value = client.currentServer?.ip == ipInput
        }

        on<LocationEvent.Server.Disconnect> {
            s0.value = false
        }
    }
}