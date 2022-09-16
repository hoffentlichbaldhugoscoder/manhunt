package de.toby.manhunt.command

import de.toby.manhunt.game.Game
import de.toby.manhunt.game.implementation.Ingame
import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.requiresPermission
import net.axay.kspigot.commands.runs
import net.axay.kspigot.event.listen
import org.bukkit.ChatColor
import org.bukkit.event.player.PlayerMoveEvent

object FreezeCommand {

    fun enable() {
        var enabled = false

        command("freeze") {
            requiresPermission("manhunt.freeze")
            runs {
                if (Game.current !is Ingame) player.sendMessage("${ChatColor.RED}You are currently unable to freeze")
                else {
                    enabled = !enabled
                    if (enabled) player.sendMessage("${ChatColor.RED}Everyone is now incapable of moving")
                    else player.sendMessage("${ChatColor.RED}Everyone can now move again")
                }
            }
        }

        listen<PlayerMoveEvent> {
            if (!enabled) return@listen
            it.isCancelled = !it.player.hasPermission("manhunt.byPassFreeze")
        }
    }
}