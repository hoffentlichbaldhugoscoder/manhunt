package de.toby.manhunt.game.implementation

import de.toby.manhunt.game.Game
import de.toby.manhunt.game.Phase
import de.toby.manhunt.listener.Connection
import de.toby.manhunt.listener.Tracker
import de.toby.manhunt.scoreboard.implementation.mainScoreboard
import de.toby.manhunt.team.TeamManager.getTeam
import de.toby.manhunt.team.implementation.hunterTeam
import de.toby.manhunt.team.implementation.runnerTeam
import de.toby.manhunt.user.UserState
import de.toby.manhunt.user.user
import de.toby.manhunt.util.eliminate
import de.toby.manhunt.util.formatToMinutes
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.ChatColor
import org.bukkit.entity.EnderDragon
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent

class Ingame : Phase() {

    init {
        hunterTeam.player().forEach {
            it.inventory.addItem(Tracker.item)
        }

        event<PlayerJoinEvent> { event ->
            val player = event.player
            if (player.user().state == null) player.user().state = UserState.SPECTATING

            player.user().board?.layout(mainScoreboard)
            Connection.handleLogin(player)

            onlinePlayers.forEach { updateScoreboard(it) }
        }

        event<PlayerQuitEvent> {
            Connection.handleQuit(it.player)
        }

        event<EntityDamageByEntityEvent> {
            val player = it.damager as? Player ?: return@event
            val enemy = it.entity as? Player ?: return@event

            if (player.getTeam() != enemy.getTeam()) return@event
            it.isCancelled = true
        }

        event<PlayerRespawnEvent> {
            if (it.player.getTeam() == hunterTeam) it.player.inventory.addItem(Tracker.item)
        }

        event<EntityDeathEvent> { event ->
            if (event.entity is EnderDragon) Game.current = End(runnerTeam)
            else {
                val player = event.entity as? Player ?: return@event
                event.drops.remove(Tracker.item)

                if (player.getTeam() == hunterTeam) return@event

                player.eliminate()
                onlinePlayers.forEach { updateScoreboard(it) }
                if (runnerTeam.player().size > 1) return@event
                Game.current = End(hunterTeam)
            }
        }
    }

    override fun run() {
        onlinePlayers.forEach { updateScoreboard(it) }
    }

    private fun updateScoreboard(player: Player) {
        player.user().board?.run{
            update(6, "${ChatColor.YELLOW}${ChatColor.BOLD}Time: ${ChatColor.WHITE}${time.formatToMinutes()}")
            update(4, "Team: ${player.getTeam()?.color}${player.getTeam()?.name}")
            update(2, "Hunter: ${ChatColor.GRAY}${hunterTeam.player().size}")
            update(1, "Runner: ${ChatColor.GRAY}${runnerTeam.player().size}")
        }
    }
}