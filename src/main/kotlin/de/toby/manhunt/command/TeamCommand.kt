package de.toby.manhunt.command

import de.toby.manhunt.game.Game
import de.toby.manhunt.game.implementation.Lobby
import de.toby.manhunt.team.TeamManager.getTeam
import de.toby.manhunt.team.TeamManager.setTeam
import de.toby.manhunt.team.implementation.hunterTeam
import de.toby.manhunt.team.implementation.runnerTeam
import de.toby.manhunt.team.implementation.spectatorTeam
import de.toby.manhunt.ui.TeamUI
import de.toby.manhunt.user.UserState
import de.toby.manhunt.user.user
import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.requiresPermission
import net.axay.kspigot.commands.runs
import net.axay.kspigot.gui.openGUI
import net.axay.kspigot.sound.sound
import org.bukkit.ChatColor
import org.bukkit.Sound

object TeamCommand {

    fun enable() {
        command("teams") {
            runs {
                if (Game.current !is Lobby) player.sendMessage("${ChatColor.RED}The game already started")
                else {
                    player.openGUI(TeamUI.ui())
                    player.sound(Sound.BLOCK_CHEST_OPEN)
                }
            }
        }
        command("runner") {
            requiresPermission("manhunt.runner")
            runs {
                if (Game.current !is Lobby) player.sendMessage("${ChatColor.RED}The game already started")
                else {
                    if (player.getTeam() == runnerTeam) player.sendMessage("${ChatColor.RED}You are already in this team")
                    else {
                        player.user().state = UserState.PLAYING
                        player.setTeam(runnerTeam)
                        player.sendMessage("${ChatColor.WHITE}You joined the ${runnerTeam.color}${runnerTeam.name} ${ChatColor.WHITE}team")
                    }
                }
            }
        }
        command("hunter") {
            runs {
                if (Game.current !is Lobby) player.sendMessage("${ChatColor.RED}The game already started")
                else {
                    if (player.getTeam() == hunterTeam) player.sendMessage("${ChatColor.RED}You are already in this team")
                    else {
                        player.user().state = UserState.PLAYING
                        player.setTeam(hunterTeam)
                        player.sendMessage("${ChatColor.WHITE}You joined the ${hunterTeam.color}${hunterTeam.name} ${ChatColor.WHITE}team")
                    }
                }
            }
        }
        command("spectator") {
            runs {
                if (Game.current !is Lobby) player.sendMessage("${ChatColor.RED}The game already started")
                else {
                    if (player.getTeam() == spectatorTeam) player.sendMessage("${ChatColor.WHITE}You are already spectating")
                    else {
                        player.user().state = UserState.SPECTATING
                        player.setTeam(spectatorTeam)
                        player.sendMessage("${ChatColor.WHITE}You are now spectating")
                    }
                }
            }
        }
    }
}
