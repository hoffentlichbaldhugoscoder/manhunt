package de.toby.manhunt

import de.toby.manhunt.command.*
import de.toby.manhunt.game.Game
import de.toby.manhunt.game.implementation.Lobby
import de.toby.manhunt.listener.Connection
import de.toby.manhunt.listener.DamageNerf
import de.toby.manhunt.listener.Tracker
import de.toby.manhunt.ui.SettingsUI
import de.toby.manhunt.ui.TeamUI
import net.axay.kspigot.main.KSpigot
import org.bukkit.World

class Manhunt : KSpigot() {

    lateinit var world: World

    companion object {
        lateinit var INSTANCE: Manhunt; private set
    }

    override fun load() {
        INSTANCE = this
    }

    override fun startup() {
        world = server.getWorld("world") ?: return

        Connection.enable()
        Tracker.enable()

        TeamCommand.enable()
        StartCommand.enable()
        TrackerCommand.enable()
        SettingsCommand.enable()
        FreezeCommand.enable()

        TeamUI.enable()
        SettingsUI.enable()
        DamageNerf.enable()

        Game.current = Lobby()
    }
}

val Manager by lazy { Manhunt.INSTANCE }