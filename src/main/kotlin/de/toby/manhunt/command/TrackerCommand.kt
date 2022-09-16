package de.toby.manhunt.command

import de.toby.manhunt.game.Game
import de.toby.manhunt.game.implementation.Ingame
import de.toby.manhunt.listener.Tracker
import de.toby.manhunt.team.TeamManager.getTeam
import de.toby.manhunt.team.implementation.hunterTeam
import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.runs
import org.bukkit.ChatColor

object TrackerCommand {

    fun enable() {
        command("compass") {
            runs {
                if (Game.current !is Ingame || player.getTeam() != hunterTeam)
                    player.sendMessage("${ChatColor.RED}You are currently unable to get a tracker")
                else {
                    if (!player.inventory.contains(Tracker.item)) player.inventory.addItem(Tracker.item)
                    else player.sendMessage("${ChatColor.RED}You already got your tracker")
                }
            }
        }
    }
}