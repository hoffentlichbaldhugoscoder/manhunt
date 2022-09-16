package de.toby.manhunt.listener

import de.toby.manhunt.config.implementation.Settings
import de.toby.manhunt.game.Game
import de.toby.manhunt.game.implementation.End
import de.toby.manhunt.util.PlayerHider.hide
import de.toby.manhunt.scoreboard.Board
import de.toby.manhunt.team.TeamManager.setTeam
import de.toby.manhunt.team.TeamManager.updateTeams
import de.toby.manhunt.team.implementation.hunterTeam
import de.toby.manhunt.team.implementation.runnerTeam
import de.toby.manhunt.team.implementation.spectatorTeam
import de.toby.manhunt.user.UserState
import de.toby.manhunt.user.user
import de.toby.manhunt.util.PlayerHider
import de.toby.manhunt.util.eliminate
import de.toby.manhunt.util.hitCooldown
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.runnables.task
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object Connection {

    fun enable() {
        listen<PlayerJoinEvent>(EventPriority.LOWEST) { event ->
            PlayerHider.handleLogin()

            val player = event.player
            player.hitCooldown(Settings.hitCooldown)
            if (player.user().state == UserState.SPECTATING) {
                player.setTeam(spectatorTeam)
                player.hide()
                player.gameMode = GameMode.SPECTATOR
            }

            Board(player)
            onlinePlayers.forEach { it.updateTeams() }
        }
        listen<PlayerQuitEvent> {
            it.player.hitCooldown(true)
        }
    }

    fun handleLogin(player: Player) {
        player.user().task?.cancel()
    }

    fun handleQuit(player: Player) {
        val user = player.user()

        if (user.state != UserState.PLAYING) return
        user.task = task(period = 20, howOften = Settings.offlineTime) {
            if (it.counterDownToZero != 0L) return@task
            if (runnerTeam.player().isEmpty()) Game.current = End(hunterTeam)
            if (hunterTeam.player().isEmpty()) Game.current = End(runnerTeam)

            player.eliminate()
            it.cancel()
            broadcast("${ChatColor.YELLOW}${player.displayName} was offline for to long and got eliminated trying to rejoin")
        }
    }
}