package de.toby.manhunt.command

import de.toby.manhunt.game.Game
import de.toby.manhunt.game.implementation.Lobby
import de.toby.manhunt.ui.SettingsUI
import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.requiresPermission
import net.axay.kspigot.commands.runs
import net.axay.kspigot.gui.openGUI
import net.axay.kspigot.sound.sound
import org.bukkit.ChatColor
import org.bukkit.Sound

object SettingsCommand {

    fun enable() {
        command("settings") {
            requiresPermission("manhunt.settings")
            runs {
                if (Game.current !is Lobby) player.sendMessage("${ChatColor.RED}The game already started")
                else {
                    player.openGUI(SettingsUI.ui())
                    player.sound(Sound.BLOCK_CHEST_OPEN)
                }
            }
        }
    }
}