package de.toby.manhunt.game.implementation

import de.toby.manhunt.Manager
import de.toby.manhunt.config.implementation.Settings
import de.toby.manhunt.game.Game
import de.toby.manhunt.game.Phase
import de.toby.manhunt.listener.Connection
import de.toby.manhunt.scoreboard.implementation.mainScoreboard
import de.toby.manhunt.team.TeamManager.getTeam
import de.toby.manhunt.team.implementation.hunterTeam
import de.toby.manhunt.team.implementation.runnerTeam
import de.toby.manhunt.user.UserState
import de.toby.manhunt.user.user
import de.toby.manhunt.util.PlayerHider.hide
import de.toby.manhunt.util.eliminate
import de.toby.manhunt.util.formatToMinutes
import de.toby.manhunt.util.reset
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.entity.EnderDragon
import org.bukkit.entity.Player
import org.bukkit.event.entity.*
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent

class Invincibility : Phase() {

    init {
        Manager.world.run {
            time = 1000
            setStorm(false)
            isThundering = false
        }

        onlinePlayers.forEach { player ->
            player.reset()
            player.teleport(Manager.world.spawnLocation)
        }

        onlinePlayers.filter { it.user().state == UserState.SPECTATING }.forEach {
            it.hide()
            it.gameMode = GameMode.SPECTATOR
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

        event<PlayerInteractEvent> {
            it.isCancelled = it.player.getTeam() == hunterTeam
        }

        event<EntityPickupItemEvent> {
            it.isCancelled = (it.entity as? Player)?.getTeam() == hunterTeam
        }

        event<EntityDamageEvent> {
            it.isCancelled = it.entity is Player && !Settings.graceDamage && (it.entity as? Player)?.getTeam() == hunterTeam
        }

        event<EntityDamageByEntityEvent> {
            it.isCancelled = it.entity is Player && it.damager is Player
        }

        event<FoodLevelChangeEvent> {
            it.isCancelled = (it.entity as? Player)?.getTeam() == hunterTeam
        }

        event<EntityTargetEvent> {
            it.isCancelled = true
        }

        event<PlayerMoveEvent> {
            it.isCancelled = it.player.getTeam() == hunterTeam
        }

        event<EntityDeathEvent> { event ->
            if (event.entity is EnderDragon) Game.current = End(runnerTeam)
            else {
                (event.entity as? Player ?: return@event).eliminate()
                onlinePlayers.forEach { updateScoreboard(it) }
                if (runnerTeam.player().size <= 1) Game.current = End(hunterTeam)
            }
        }
    }

    override fun run() {
        onlinePlayers.forEach { updateScoreboard(it) }

        when (countdown()) {
            2, 3, 4, 5, 10 -> broadcast("${ChatColor.YELLOW}The grace period ends in ${countdown()} seconds")
            1 -> broadcast("${ChatColor.YELLOW}The grace period ends in one second")
            0 -> {
                broadcast("${ChatColor.YELLOW}The grace period has ended")
                Game.current = Ingame()
            }
        }
    }

    private fun countdown() = Settings.graceDuration - time

    private fun updateScoreboard(player: Player) {
        player.user().board?.run{
            update(6, "${ChatColor.YELLOW}${ChatColor.BOLD}Grace: ${ChatColor.WHITE}${countdown().formatToMinutes()}")
            update(4, "Team: ${player.getTeam()?.color}${player.getTeam()?.name}")
            update(2, "Hunter: ${ChatColor.GRAY}${hunterTeam.player().size}")
            update(1, "Runner: ${ChatColor.GRAY}${runnerTeam.player().size}")
        }
    }
}